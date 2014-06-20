package common.observe.call;

import java.io.Serializable;

/**
 * 远程调用命令格式
 * @author geng yufeng
 *
 */
public class Call implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int callType;
	public int missionId;
	public String[] params;
	
	/**
	 * 
	 * @param _commandType defined in constant.java
	 * @param _params most commands from client to server have only one parameter.
	 *  
	 */
	public Call(int _commandType, String[] _params){
		callType = _commandType;
		params = _params;
		missionId = -1;
	}
	
	public String getParamsString(){
		String ret = "";
		for(int i=0; i<params.length; i++){
			ret += " "+params[i];
		}
		return ret;
	}
}
