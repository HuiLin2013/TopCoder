package util.jna;

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.PointerByReference;

@SuppressWarnings("rawtypes")
public interface Wlanapi extends Library {

	public Wlanapi INSTANCE = (Wlanapi) Native.loadLibrary("Wlanapi", Wlanapi.class);

	public int WlanOpenHandle(int dwClientVersion, PVOID pReserved, PointerByReference pdwNegotiatedVersion,
			HANDLEByReference phClientHandle);

	public int WlanEnumInterfaces(HANDLE hClientHandle, PVOID pReserved, PointerByReference ppInterfaceList);

	public int WlanGetAvailableNetworkList(HANDLE hClientHandle, Pointer GUID, int dwFlags, PVOID pReserved,
			PointerByReference ppAvailableNetworkList);

	public class WLAN_INTERFACE_INFO_LIST extends Structure {
		public int dwNumberOfItems;
		public int dwIndex;
		public WLAN_INTERFACE_INFO[] InterfaceInfo;

		public WLAN_INTERFACE_INFO_LIST(Pointer p) {
			super(p);
			this.dwNumberOfItems = p.getInt(0);
			this.InterfaceInfo = new WLAN_INTERFACE_INFO[this.dwNumberOfItems];
			read();
		}

		@Override
		protected List getFieldOrder() {
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("dwNumberOfItems");
			fields.add("dwIndex");
			fields.add("InterfaceInfo");
			return fields;
		}
	}

	public class WLAN_INTERFACE_INFO extends Structure {
		public GUID InterfaceGuid;
		public char[] strInterfaceDescription = new char[256];
		public int isState;

		@Override
		protected List getFieldOrder() {
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("InterfaceGuid");
			fields.add("strInterfaceDescription");
			fields.add("isState");
			return fields;
		}
	}

	public class WLAN_AVAILABLE_NETWORK_LIST extends Structure {
		public int dwNumberOfItems;
		public int dwIndex;
		public WLAN_AVAILABLE_NETWORK[] Network;

		public WLAN_AVAILABLE_NETWORK_LIST(Pointer p) {
			super(p);
			this.dwNumberOfItems = p.getInt(0);
			this.Network = new WLAN_AVAILABLE_NETWORK[this.dwNumberOfItems];
			read();
		}

		@Override
		protected List getFieldOrder() {
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("dwNumberOfItems");
			fields.add("dwIndex");
			fields.add("Network");
			return fields;
		}
	}

	public class WLAN_AVAILABLE_NETWORK extends Structure {
		public char[] strProfileName;
		public DOT11_SSID dot11Ssid;
		public int dot11BssType;
		public int uNumberOfBssids;
		public boolean bNetworkConnectable;
		public int wlanNotConnectableReason;
		public int uNumberOfPhyTypes;
		public int[] dot11PhyTypes;
		public boolean bMorePhyTypes;
		public int wlanSignalQuality;
		public boolean bSecurityEnabled;
		public int dot11DefaultAuthAlgorithm;
		public int dot11DefaultcipherAlgorithm;
		public int dwFlags;
		public Pointer dwReserved;

		public WLAN_AVAILABLE_NETWORK() {
			dot11PhyTypes = new int[8];
			strProfileName = new char[256];
			dwReserved = null;
		}

		@Override
		protected List getFieldOrder() {
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("strProfileName");
			fields.add("dot11Ssid");
			fields.add("dot11BssType");
			fields.add("uNumberOfBssids");
			fields.add("bNetworkConnectable");
			fields.add("wlanNotConnectableReason");
			fields.add("uNumberOfPhyTypes");
			fields.add("dot11PhyTypes");
			fields.add("bMorePhyTypes");
			fields.add("wlanSignalQuality");
			fields.add("bSecurityEnabled");
			fields.add("dot11DefaultAuthAlgorithm");
			fields.add("dot11DefaultcipherAlgorithm");
			fields.add("dwFlags");
			fields.add("dwReserved");
			return fields;
		}
	}

	public class DOT11_SSID extends Structure {
		public int uSSIDLenght;
		public byte[] ucSSID;

		public DOT11_SSID() {
			this.ucSSID = new byte[32];
		}

		@Override
		protected List getFieldOrder() {
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("uSSIDLenght");
			fields.add("ucSSID");
			return fields;
		}
	}

}
