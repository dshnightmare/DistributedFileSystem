package common.protocol;

public class StorageNodeInfo {
	private long SSID;
	private String address;
	private long fingerprint;

	public StorageNodeInfo() {
		SSID = 0;
		address = "";
		fingerprint = 0;
	}

	public long getSSID() {
		return SSID;
	}

	public void setSSID(long sSID) {
		SSID = sSID;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(long fingerprint) {
		this.fingerprint = fingerprint;
	}
}
