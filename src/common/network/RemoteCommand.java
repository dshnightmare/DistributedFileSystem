package common.network;

import java.io.Serializable;

/**
 * 远程调用命令格式
 * @author geng yufeng
 *
 */
public class RemoteCommand implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int commandType;
	public int missionId;
	public String[] params;
	
	public RemoteCommand(int _commandType, String[] _params){
		commandType = _commandType;
		params = _params;
		missionId = -1;
	}
}
