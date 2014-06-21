package common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
	private static final Logger LOG = Logger.getLogger(FileUtil.class);

	public static boolean deleteFile(final File file) {
		final boolean wasDeleted = file.delete();
		if (wasDeleted) {
			return true;
		}
		final boolean ex = file.exists();
		if (ex) {
			LOG.warn("Failed to delete file [" + file.getAbsolutePath()
					+ "]: it still exists.");
		}
		return !ex;
	}

	public static boolean fullyDeleteContent(final File dir) {
		boolean deltetionSucceeded = true;
		final File[] contents = dir.listFiles();
		if (contents != null) {
			for (File file : contents) {
				if (file.isFile()) {
					if (!deleteFile(file))
						deltetionSucceeded = false;
				}
			}
		}
		return deltetionSucceeded;
	}

	public static boolean copyFile(String src, String dst) {
		int bytesum = 0;
		int byteread = 0;
		InputStream in = null;
		OutputStream out = null;
		try {
			File srcFile = new File(src);
			if (srcFile.isFile()) {
				in = new FileInputStream(src);
				out = new FileOutputStream(dst);
				byte[] buffer = new byte[2000];
				while ((byteread = in.read(buffer)) != -1) {
					bytesum += byteread;
					out.write(buffer, 0, byteread);
				}
			}
			in.close();
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
