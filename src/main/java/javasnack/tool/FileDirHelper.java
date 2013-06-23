package javasnack.tool;

import java.io.File;
import java.io.IOException;

public class FileDirHelper {

    /**
     * Create temporary directory (<= JDK 1.6) (from JDK 1.7, Files.createTempDirectory available)
     * 
     * @see http://stackoverflow.com/questions/617414/create-a-temporary-directory-in-java
     * @return
     * @throws IOException
     */
    public static File createTmpDir() throws IOException {
        File tmpFile = File.createTempFile("temp",
                Long.toString(System.nanoTime()));
        String fullPath = tmpFile.getAbsolutePath();
        tmpFile.delete();
        File tmpDir = new File(fullPath);
        if (!tmpDir.mkdir()) {
            throw new IOException("Failed to create tmpDir: " + fullPath);
        }
        return tmpDir;
    }
}
