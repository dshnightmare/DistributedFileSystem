package common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 对象和字节转换器
 * 
 * @author straw
 * 
 */
public class SwitchObjectAndByte {
	/**
	 * 将对象转换成字节数组
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public static byte[] switchObjectToByte(Object object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		byte[] bs = baos.toByteArray();
		oos.close();
		return bs;
	}

	/**
	 * 将字节数组还原成对象
	 * 
	 * @param src
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object switchByteToObject(byte[] src) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream byteIn = new ByteArrayInputStream(src);
		ObjectInputStream objIn = new ObjectInputStream(byteIn);
		Object o = objIn.readObject();
		objIn.close();
		return o;
	}
}