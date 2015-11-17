package com.shell.broker.broadsign.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.shell.broker.util.AbstractServer;
import com.shell.broker.util.jna.Kernel32;
import com.shell.broker.util.jna.Kernel32.SYSTEM_POWER_STATUS;
import com.shell.broker.util.jna.Wlanapi;
import com.shell.broker.util.jna.Wlanapi.WLAN_AVAILABLE_NETWORK;
import com.shell.broker.util.jna.Wlanapi.WLAN_AVAILABLE_NETWORK_LIST;
import com.shell.broker.util.jna.Wlanapi.WLAN_INTERFACE_INFO;
import com.shell.broker.util.jna.Wlanapi.WLAN_INTERFACE_INFO_LIST;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Module that can receive requests for changing the dispenser connected to this broker.
 */
public class BroadSignContainerService extends AbstractServer {

	private static final Logger LOG = LoggerFactory.getLogger(BroadSignContainerService.class);

	public static final String XML_DTD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	/**
	 * The null byte is used to signal the end of a message when sending a response to flash
	 */
	private static final byte[] NULL_BYTE = new byte[] { 0 };

	private class ServiceResponse {
		private String type;
		private String data;

		public ServiceResponse(String type, String data) {
			this.type = type;
			this.data = data;
		}

		@Override
		public String toString() {
			return XML_DTD + "<Service_Reponse type=\"" + type + "\" data=\"" + data + "\" />";
		}
	}

	/**
	 * Timeout before rejecting a change request if it's not transmitted in time.
	 */
	public static final int READ_TIMEOUT = 1 * 1000;

	/**
	 * The dispenser connection change request listener.
	 */
	private final IDispenserChangeRequestListener listener;

	/**
	 * Class constructor.
	 * 
	 * @throws IllegalArgumentException
	 *             if the port < 1
	 */
	public BroadSignContainerService(IDispenserChangeRequestListener listener, int port)
			throws IllegalArgumentException {
		// Input processing
		super(port);
		this.listener = listener;

		LOG.debug("Starting {} using port {}", this.getClass().getSimpleName(), port);
	}

	/**
	 * Reads the request from the {@link InputStream}.
	 */
	@Override
	protected void processSocket(Socket socket) {
		try (OutputStream out = socket.getOutputStream();
				InputStreamReader in = new InputStreamReader(socket.getInputStream())) {
			socket.setSoTimeout(READ_TIMEOUT);

			ServiceResponse reponse = new ServiceResponse("UNKNOWN", "NOK");

			try { // read request
				final StringBuilder cbuf = new StringBuilder(100);
				for (int data; ((data = in.read()) > -1);) {
					cbuf.append((char) data);
					if (data == '>' && cbuf.toString().endsWith("/>")) { // finished reading request
						break;
					}
				}

				final String request = cbuf.toString();
				LOG.debug("Received request: {}", request);
				reponse = handleRequest(transformRequest(request));

			} catch (IOException | SAXException e) {
				LOG.error("Error handling request: " + e.getMessage(), e);
				reponse.data = "NOK";
			}

			out.write(reponse.toString().getBytes()); // send response
			out.write(NULL_BYTE);
			out.flush();

			LOG.debug("Sent reponse: {}", reponse.toString());
		} catch (IOException e) {
			LOG.error("Error sending request response.", e);
		}
	}

	private Document transformRequest(String request) throws SAXException {
		if (!request.startsWith("<?xml"))
			request = XML_DTD + request;
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(request)));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new SAXException(e.getMessage());
		}
	}

	private ServiceResponse handleRequest(Document request) throws IOException, SAXException {
		final String type = request.getDocumentElement().getAttribute("type");
		final String data = request.getDocumentElement().getAttribute("data");
		if (type.equalsIgnoreCase("CHANGE_PUMP")) {
			return fireDispenserChangeRequest(data);
		} else if (type.equalsIgnoreCase("GET_BATTERY_LEVEL")) {
			return getBatteryLevel();
		} else if (type.equalsIgnoreCase("GET_WIFI_STATUS")) {
			return getWifiStatus();
		} else {
			throw new IOException("Unknown request type!");
		}
	}

	private ServiceResponse fireDispenserChangeRequest(String data) throws SAXException {
		try {
			LOG.debug("Firing dipenser change request");
			// Parse data values
			final String[] dataArray = data.split(",");
			// Transform values into correct types
			final InetAddress host = InetAddress.getByName(dataArray[0]);
			final int port = Integer.parseInt(dataArray[1]);
			final int pumpNumber = Integer.parseInt(dataArray[2]);
			// Validate types
			if (port < 1)
				throw new IllegalArgumentException("Port must be > 0 but was " + port);
			// Execute request
			if (listener != null) {
				listener.receiveChangeRequest(host, port, pumpNumber);
				LOG.debug("Fired dipenser change request");
			} else {
				LOG.warn("Connection type does not support pump changing!");
			}
			// Create response
			return new ServiceResponse("CHANGE_PUMP", String.valueOf(pumpNumber));
		} catch (UnknownHostException e) {
			LOG.error("Request refused. Bad host.", e);
			return new ServiceResponse("CHANGE_PUMP", "BAD_HOST");
		} catch (IllegalArgumentException e) {
			LOG.error("Request refused. Bad port.", e);
			return new ServiceResponse("CHANGE_PUMP", "BAD_PORT");
		}
	}

	private ServiceResponse getBatteryLevel() {
		LOG.debug("Getting battery level");
		SYSTEM_POWER_STATUS status = new Kernel32.SYSTEM_POWER_STATUS();
		Kernel32.INSTANCE.GetSystemPowerStatus(status);
		String level;
		if (status.BatteryLifePercent > 75) {
			level = "HIGH";
		} else if (status.BatteryLifePercent > 50) {
			level = "MIDDLE";
		} else if (status.BatteryLifePercent > 15) {
			level = "LOW";
		} else {
			level = "CRITICAL";
		}
		if (status.ACLineStatus == 1) {
			level += ",CHARGING";
		} else {
			level += ",DISCHARGING";
		}
		LOG.debug("Got Battery level: {}", level);
		return new ServiceResponse("GET_BATTERY_LEVEL", level);
	}

	private ServiceResponse getWifiStatus() {
		LOG.debug("Getting wifi status");
		HANDLEByReference clientHandle = new HANDLEByReference();
		Wlanapi.INSTANCE.WlanOpenHandle(2, null, new PointerByReference(), clientHandle);

		PointerByReference pref = new PointerByReference();
		Wlanapi.INSTANCE.WlanEnumInterfaces(clientHandle.getValue(), null, pref);
		WLAN_INTERFACE_INFO_LIST list = new WLAN_INTERFACE_INFO_LIST(pref.getValue());

		Guid.GUID guid = null;
		for (WLAN_INTERFACE_INFO info : list.InterfaceInfo) {
			LOG.debug("{}: {}", info.InterfaceGuid.toString(), info.isState);
			if (info.isState == 1) {
				guid = info.InterfaceGuid;
				break;
			}
		}

		if (guid != null) {
			PointerByReference pref2 = new PointerByReference();
			Wlanapi.INSTANCE.WlanGetAvailableNetworkList(clientHandle.getValue(), guid.getPointer(), 1, null, pref2);
			WLAN_AVAILABLE_NETWORK_LIST list2 = new WLAN_AVAILABLE_NETWORK_LIST(pref2.getValue());

			for (WLAN_AVAILABLE_NETWORK network : list2.Network) {
				// first network in array is currently connected network
				String signalStrength;
				int signalQuality = network.wlanSignalQuality;
				if (signalQuality > 74) {
					signalStrength = "HIGH";
				} else if (signalQuality > 49) {
					signalStrength = "MIDDLE";
				} else if (signalQuality > 24) {
					signalStrength = "LOW";
				} else {
					signalStrength = "CRITICAL";
				}
				return new ServiceResponse("GET_WIFI_STATUS", signalStrength);
			}

			return new ServiceResponse("GET_WIFI_STATUS", "N/A");
		} else {
			return new ServiceResponse("GET_WIFI_STATUS", "N/A");
		}
	}

}
