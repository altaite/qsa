package global.io;

import java.io.File;

public class FileOperations {

	public static boolean deleteDirectory(File f) {
		if (f.isDirectory()) {
			String[] children = f.list();
			for (String child : children) {
				boolean b = deleteDirectory(new File(f, child));
				if (!b) {
					return false;
				}
			}
		}
		return f.delete();
	}

	/*
     * Makes sure the directory is ready, throws exception if not.
	 */
	public static File safeDir(File d) {
		if (!d.exists()) {
			d.mkdirs();
		}
		if (!d.isDirectory()) {
			throw new RuntimeException(d + " is a file, not a directory.");
		}
		return d;
	}

	/*
     * Returns the File object with 'dir' path and 'subname' name. If not
     * exception is thrown, it is guaranteed that whole directory structure
     * exists, if not it is created automatically.
     *
     * If subname contains '.', it is treated as a file, otherwise as a
     * directory.
	 */
	public static File safeSub(File dir, String subname) {
		if (subname.contains(".")) {
			return safeSubfile(dir, subname);
		} else {
			return safeSubdir(dir, subname);
		}
	}

	public static File safeSubdir(File dir, String subdirName) {
		safeDir(dir);
		File subdir = new File(dir.getPath()
			+ File.separator + subdirName);
		safeDir(subdir);
		return subdir;

	}

	public static File safeSubfile(File dir, String subdirName) {
		safeDir(dir);
		File subfile = new File(dir.getPath()
			+ File.separator + subdirName);
		return subfile;

	}
}