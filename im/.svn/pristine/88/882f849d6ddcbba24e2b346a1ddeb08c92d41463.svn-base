package com.wonhigh.im.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.os.Environment;

import com.wonhigh.im.service.IMMainService;

/**
 * TODO: 增加描述
 * 
 * @author USER
 * @date 2014-12-29 下午4:18:12
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class IMFileUtil {

	/**
	 * 
	 * @param path
	 * @return
	 */
	public static byte[] readFile(String path) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File(path));
			byte[] buf = new byte[1024];
			int n;
			while (-1 != (n = fis.read(buf))) {
				baos.write(buf, 0, n);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}

	public static boolean writeFile(byte[] b, String path) {

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			byte buf[] = new byte[1024];
			File download = new File(path);
			FileOutputStream fos = new FileOutputStream(download);
			do {
				// 循环读取
				int numread = bais.read(buf);
				if (numread == -1) {
					break;
				}
				fos.write(buf, 0, numread);
			} while (true);
			bais.close();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
