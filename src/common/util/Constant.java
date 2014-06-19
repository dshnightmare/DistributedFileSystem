package common.util;

/**
 * 
 * @author geng yufeng
 *
 */
public class Constant {

	public static final String serverIP = "172.31.203.219";	//put it in config file
	public static final int serverPort = 5001;
	
	//client-server operation
	public static final int ADD_FILE = 001;
	public static final int GET_FILE = 002;
	public static final int DELETE_FILE = 003;
	public static final int EXIST_FILE = 004;
	public static final int SIZEOF_FILE = 005;

	public static final int CREATE_DIR = 011;
	public static final int DELETE_DIR = 012;
	public static final int LIST_DIR = 013;
	public static final int RENAME_DIR = 014;
}
