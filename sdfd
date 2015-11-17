package test;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.PointerByReference;

import util.jna.Wlanapi;
import util.jna.Wlanapi.WLAN_AVAILABLE_NETWORK;
import util.jna.Wlanapi.WLAN_AVAILABLE_NETWORK_LIST;
import util.jna.Wlanapi.WLAN_INTERFACE_INFO;
import util.jna.Wlanapi.WLAN_INTERFACE_INFO_LIST;

public class TEST implements Runnable {

	public static final String XML_DTD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	//#######################
	//# TEST CODE GOES HERE #
	//#######################
	@Override
	public void run() {
		ServiceResponse status = getWifiStatus();
		LOG.debug("{}: {}", status.type, status.data);
	}

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

	private ServiceResponse getWifiStatus() {
		LOG.debug("Getting wifi status");
		HANDLEByReference clientHandle = new HANDLEByReference();
		Wlanapi.INSTANCE.WlanOpenHandle(2, null, new PointerByReference(), clientHandle);

		PointerByReference pref = new PointerByReference();
		Wlanapi.INSTANCE.WlanEnumInterfaces(clientHandle.getValue(), null, pref);
		WLAN_INTERFACE_INFO_LIST list = new WLAN_INTERFACE_INFO_LIST(pref.getValue());

		Guid.GUID guid = null;
		int count = 0;
		for (WLAN_INTERFACE_INFO info : list.InterfaceInfo) {
			LOG.debug("{}: {} = {}", info.InterfaceGuid.toGuidString(), info.isState,
					new String(info.strInterfaceDescription));
			count++;
			if (info.isState == 1) {
				guid = info.InterfaceGuid;
			}
		}

		if (guid != null) {
			PointerByReference pref2 = new PointerByReference();
			Wlanapi.INSTANCE.WlanGetAvailableNetworkList(clientHandle.getValue(), guid.getPointer(), 0, null, pref2);
			WLAN_AVAILABLE_NETWORK_LIST list2 = new WLAN_AVAILABLE_NETWORK_LIST(pref2.getValue());

			for (WLAN_AVAILABLE_NETWORK network : list2.Network) {
				// first network in array is currently connected network

				if ((network.dwFlags & 1) == 1) {
					LOG.debug("CONNECTED");
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
					LOG.debug("LOOP:" + new String(network.dot11Ssid.ucSSID) + ";" + signalStrength);
				}


			}

			//			LOG.debug("LOOP:" + new String(list2.Network[count].dot11Ssid.ucSSID) + ";"
			//					+ list2.Network[count].wlanSignalQuality + ";" + list2.Network[count].);

			Wlanapi.INSTANCE.WlanFreeMemory(list2.getPointer());
			Wlanapi.INSTANCE.WlanCloseHandle(clientHandle.getValue(), null);

			return new ServiceResponse("GET_WIFI_STATUS", "N/A");
		} else {

			Wlanapi.INSTANCE.WlanCloseHandle(clientHandle.getValue(), null);
			return new ServiceResponse("GET_WIFI_STATUS", "N/A");
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(TEST.class);

	private static Thread thread;

	public static void main(String... args) throws Exception {
		LOG.info("===STARTING TEST-APP===");
		final long start = System.nanoTime();

		thread = new Thread(new TEST());
		thread.run();

		final long end = System.nanoTime();
		LOG.info(String.format("[execTime=%dms]", TimeUnit.NANOSECONDS.toMillis(end - start)));
		LOG.info("===EXITING TEST-APP===");
	}

	public static void stop(String... args) throws Exception {
		thread.interrupt();
		LOG.info("### STOP RCVD ###");
	}

}
