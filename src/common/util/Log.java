package common.util;

/**
 * Simplify use of System.out.println(), and distinguish it into 3 types
 * @author gengyufeng
 *
 */
public class Log {

	/**
	 * SHOULD be printed as necessary hint for user
	 * @param print
	 */
	public static void print(String print){
		System.out.println(print);
	}
	
	/**
	 * CAN be printed for user to know some status
	 * @param info
	 */
	public static void info(String info){
		System.out.println(info);
	}
	
	/**
	 * SHOULD NOT be printed as debug information
	 * @param debug
	 */
	public static void debug(String debug){
		System.out.println(debug);
	}
}
