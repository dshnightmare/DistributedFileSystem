package test.nameserver.meta;

import nameserver.meta.Directory;
import nameserver.meta.File;
import nameserver.meta.Meta;
import junit.framework.TestCase;

public class TestMeta extends TestCase {
	public void testMeta() {
		Meta meta = Meta.getInstance();

		assertNull(meta.getDirectory("/"));
		assertFalse(meta.containDirectory("/"));

		meta.addDirectory(new Directory("/"));

		assertNotNull(meta.getDirectory("/"));
		assertTrue(meta.containDirectory("/"));
	}

	public void testRename() {
		Meta meta = Meta.getInstance();

		assertNull(meta.getFile("/a/", "b"));

		File file = new File("b", 1);
		meta.addFile("/a/", file);

		assertNotNull(meta.getFile("/a/", "b"));
		assertNull(meta.getFile("/c/", "d"));

		meta.renameFile("/a/", "b", "/c/", "d");

		assertNotNull(meta.getDirectory("/a/"));
		assertNull(meta.getFile("/a/", "b"));
		assertNotNull(meta.getFile("/c/", "d"));

	}

	public void testDirectory() {
		final Meta meta = Meta.getInstance();
		File file = null;

		file = new File("f2", 2);
		meta.addFile("/a/b/c/d/e/", file);
		meta.addDirectory(new Directory("/a/b/x/"));

		assertNotNull(meta.getFile("/a/b/c/d/e/", "f2"));
		assertNotNull(meta.getDirectory("/a/b/c/d/"));
		// Only /a/b/x/
		assertTrue(1 == meta.getSubDirectoryName("/a/b/").size());
	}
}
