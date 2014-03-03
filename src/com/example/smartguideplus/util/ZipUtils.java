package com.example.smartguideplus.util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.util.Log;
public class  ZipUtils{


	private static final int COMPRESSION_LEVEL = 8;

	private static final int BUFFER_SIZE = 1024 * 2;

	/**
	 * ������ ����� Zip ���Ϸ� �����Ѵ�.
	 * @param sourcePath - ���� ��� ���丮
	 * @param output - ���� zip ���� �̸�
	 * @throws Exception
	 */
	public static void zip(String sourcePath, String output) throws Exception {

		// ���� ���(sourcePath)�� ���丮�� ������ �ƴϸ� �����Ѵ�.
		File sourceFile = new File(sourcePath);

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipOutputStream zos = null;

		try {
			fos = new FileOutputStream(output); // FileOutputStream
			bos = new BufferedOutputStream(fos); // BufferedStream
			zos = new ZipOutputStream(bos); // ZipOutputStream
			zos.setLevel(COMPRESSION_LEVEL); // ���� ���� - �ִ� ������� 9, ����Ʈ 8
			zipEntry(sourceFile, sourcePath, zos); // Zip ���� ��
			zos.finish(); // ZipOutputStream finish
		} finally {
			if (zos != null) {
				zos.close();
			}
			if (bos != null) {
				bos.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * ����
	 * @param sourceFile
	 * @param sourcePath
	 * @param zos
	 * @throws Exception
	 */
	private static void zipEntry(File sourceFile, String sourcePath, ZipOutputStream zos) throws Exception {
		// sourceFile �� ���丮�� ��� ���� ���� ����Ʈ ������ ���ȣ��
		if (sourceFile.isDirectory()) {
			if (sourceFile.getName().equalsIgnoreCase(".metadata")) { // .metadata ���丮 return
				return;
			}
			File[] fileArray = sourceFile.listFiles(); // sourceFile �� ���� ���� ����Ʈ
			for (int i = 0; i < fileArray.length; i++) {
				zipEntry(fileArray[i], sourcePath, zos); // ��� ȣ��
			}
		} else { // sourcehFile �� ���丮�� �ƴ� ���
			BufferedInputStream bis = null;
			try {
				String sFilePath = sourceFile.getPath();
				String zipEntryName = sFilePath.substring(sourcePath.length() + 1, sFilePath.length());

				bis = new BufferedInputStream(new FileInputStream(sourceFile));
				ZipEntry zentry = new ZipEntry(zipEntryName);
				zentry.setTime(sourceFile.lastModified());
				zos.putNextEntry(zentry);

				byte[] buffer = new byte[BUFFER_SIZE];
				int cnt = 0;
				while ((cnt = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
					zos.write(buffer, 0, cnt);
				}
				zos.closeEntry();
			} finally {
				if (bis != null) {
					bis.close();
				}
			}
		}
	}

	
	
	public static boolean unzip(File zipFile, File targetDir) {
        FileInputStream fis = null;
        ZipInputStream zis = null;
        ZipEntry zentry = null;

        try {
			try {

				fis = new FileInputStream(zipFile);
				zis = new ZipInputStream(fis);

				while ((zentry = zis.getNextEntry()) != null) {
					String fileNameToUnzip = zentry.getName(); 

					File targetFile = new File(targetDir, fileNameToUnzip);

					if (zentry.isDirectory()) {//if is Directory
						File path = new File(targetFile.getAbsolutePath());
						if (!path.isDirectory()) {
							path.mkdirs();
						}
					} else { //if is File 
						File path = new File(targetFile.getParent());
						if (!path.isDirectory()) {
							path.mkdirs();
						}
						unzipEntry(zis, targetFile);
					}
				}
			} finally {
				if (zis != null) {
					zis.close();
				}
				if (fis != null) {
					fis.close();
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
    }
	
    public static File unzipEntry(ZipInputStream zis, File targetFile) throws Exception {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);

            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = zis.read(buffer)) != -1) {
            	if(len == 0){
            		return null;
            	}
                fos.write(buffer, 0, len);
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        return targetFile;
    }
}
