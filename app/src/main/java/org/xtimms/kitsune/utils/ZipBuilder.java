package org.xtimms.kitsune.utils;

import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipBuilder implements Closeable {

    private final ZipOutputStream mZipOutputStream;
    private final byte[] mBuffer = new byte[1024];

    public ZipBuilder(File outputFile) throws IOException {
        mZipOutputStream = new ZipOutputStream(new FileOutputStream(outputFile));
    }

    @SuppressWarnings("ReturnInsideFinallyBlock")
    @Nullable
    public static File[] unzipFiles(File file, File outputDir) {
        final byte[] buffer = new byte[1024];
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            return null;
        }

        ZipInputStream zipInputStream = null;
        FileOutputStream outputStream = null;
        try {
            zipInputStream = new ZipInputStream(new FileInputStream(file));
            ArrayList<File> files = new ArrayList<>();
            File outFile;
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                outFile = new File(outputDir, zipEntry.getName());
                if (outFile.exists() || outFile.createNewFile()) {
                    outputStream = new FileOutputStream(outFile);
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, len);
                    }
                    outputStream.close();
                    files.add(outFile);
                }
            }
            return files.toArray(new File[0]);
        } catch (Exception e) {
            FileLogger.getInstance().report("ZIP", e);
            return null;
        } finally {
            try {
                if (zipInputStream != null) {
                    zipInputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                FileLogger.getInstance().report("ZIP", e);
                return null;
            }
        }
    }

    public void addFile(File file) throws IOException {
        addFile(file, file.getName());
    }

    public ZipBuilder addFile(File file, String name) throws IOException {
        FileInputStream in = null;
        try {
            ZipEntry zipEntry = new ZipEntry(name);
            mZipOutputStream.putNextEntry(zipEntry);
            in = new FileInputStream(file);
            int len;
            while ((len = in.read(mBuffer)) > 0) {
                mZipOutputStream.write(mBuffer, 0, len);
            }
            mZipOutputStream.closeEntry();
            return this;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public ZipBuilder addFiles(File[] files) throws IOException {
        for (File o : files) {
            if (o.isFile()) {
                addFile(o);
            }
        }
        return this;
    }

    public void build() throws IOException {
        mZipOutputStream.finish();
    }

    @Override
    public void close() {
        try {
            mZipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
