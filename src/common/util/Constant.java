package common.util;

/**
 * 
 * @author geng yufeng
 *
 */
public class Constant {

	public static final String serverIP = "172.31.203.220";	//put it in config file
	public static final int serverPort = 5001;
	
	//callType: client-server operation 
	public static final int ADD_FILE = 001;
	public static final int GET_FILE = 002;
	public static final int DELETE_FILE = 003;
	public static final int EXIST_FILE = 004;
	public static final int SIZEOF_FILE = 005;

	public static final int CREATE_DIR = 011;
	public static final int DELETE_DIR = 012;
	public static final int LIST_DIR = 013;
	public static final int RENAME_DIR = 014;
	
	// Configuration stuff
	public static final String CONFIGURATION_PATH = "conf.properties";
	public static final String HEARTBEAT_INTERVAL_KEY = "heartbeat_interval";
	public static final String LEASE_PERIOD_KEY = "lease_period";
}
