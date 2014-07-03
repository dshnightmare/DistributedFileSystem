package common.call.n2s;

import java.util.List;

import common.call.Call;

public class SyncCallN2S extends Call {
	private static final long serialVersionUID = 2018349521385214230L;

	private List<Long> files;

	public SyncCallN2S(List<Long> files) {
		super(Call.Type.SYNC_N2S);
		this.files = files;
	}

	public List<Long> getFiles() {
		return files;
	}
}
