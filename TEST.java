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

	//#######################
	//# TEST CODE GOES HERE #
	//#######################
	@Override
	public void run() {
		LOG.debug(getNetworkState());
	}

	private String getNetworkState() {
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

			for (WLAN_AVAILABLE_NETWORK net : list2.Network)
				// first network in array is currently connected network
				return new String(net.dot11Ssid.ucSSID) + " " + net.wlanSignalQuality + "%";

			return "No networks available";
		} else {
			return "No network connected!";
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
