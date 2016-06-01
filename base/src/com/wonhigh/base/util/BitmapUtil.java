package com.wonhigh.base.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

/**
 * @ClassName: BitmapUtil
 * @Description: Bitmap工具类
 * @author li.xy
 * @date 2014-6-17 下午3:38:56
 */
public class BitmapUtil {
	private static final int ww = 480;

	private static final int hh = 800;

	/**
	 * 图片变圆角
	 * 
	 * @param bitmap 需要修改的图片
	 * @param pixels 圆角的弧度
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		if (bitmap == null) {
			return null;
		}
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 保存bitmap
	 * 
	 * @param path
	 * @param buffer
	 * @return
	 */
	public static boolean saveBitmap(String path, byte[] buffer) {
		if (TextUtils.isEmpty(path) || null == buffer) {
			return false;
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(path));
			out.write(buffer);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
			} catch (Exception e2) {
			}
		}
		return false;
	}

	/**
	 * @param filePath
	 * @return
	 */
	public static Bitmap getdecodeBitmap(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return null;
		}
		BitmapFactory.Options options = new Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

		int width = options.outWidth;
		int height = options.outHeight;
		float scale = 1f;
		if (width > ww && width > height) {
			scale = width / ww;
		} else if (height > hh && height > width) {
			scale = height / hh;
		} else {
			scale = 1;
		}

		options.inSampleSize = (int) scale;
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(filePath, options);
		return bitmap;
	}

	/**
	 * 
	 * @param filePath
	 * @param expectWidth
	 * @param expectHeight
	 * @return
	 */
	public static boolean getdecodeBitmap(String filePath, int expectWidth, int expectHeight) {
		boolean isSuccess = false;
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		BitmapFactory.Options options = new Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

		int width = options.outWidth;
		int height = options.outHeight;
		int scale = 1;
		while (true) {
			if (width <= expectWidth && height <= expectHeight) {
				break;
			}
			width /= 2;
			height /= 2;
			scale *= 2;
		}

		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;

		bitmap = BitmapFactory.decodeFile(filePath, options);
		if (bitmap == null) {
			isSuccess = false;
		} else {
			byte[] bitmapByte = bitmap2Bytes(bitmap);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(new File(filePath));
				fos.write(bitmapByte);
				fos.flush();
				fos.close();
				isSuccess = true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isSuccess = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isSuccess = false;
			} finally {
				try {
					if (fos != null)
						fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return isSuccess;
	}


	/**
	 * @Description: 保存bitmap
	 * @param path
	 * @param bitmap
	 * @return：int
	 */
	public static boolean saveBitmap(String path, Bitmap bitmap) {
		if (TextUtils.isEmpty(path) || bitmap == null) {
			return false;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(path));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (Exception e2) {
			}
		}
		return false;
	}

	/**
	 * @Description: 指定图片长宽生成新图片
	 * @param bitmap
	 * @param height
	 * @param width
	 * @return：Bitmap
	 */
	public static Bitmap decodeBitmap(Bitmap bitmap, int height, int width) {
		if (bitmap == null || height < 0 || width < 0) {
			return null;
		}
		int bmpHeight = bitmap.getHeight();
		int bmpWeight = bitmap.getWidth();
		float scale = Math.min(height / bmpHeight, width / bmpWeight);
		Bitmap mutableBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bmpWeight * scale), (int) (bmpHeight * scale),
				true);// 指定图片长宽，生成新图片
		return mutableBitmap;
	}

	/**
	 * @Description: 将Bitmap另存为指定的JPG文件
	 * @param bitmap
	 * @param pathName
	 * @return：boolean
	 */
	public static boolean writePhotoJpg(Bitmap bitmap, String pathName) {
		if (bitmap == null || TextUtils.isEmpty(pathName)) {
			return false;
		}
		File file = new File(pathName);
		FileOutputStream os = null;
		try {
			file.createNewFile();
			// BufferedOutputStream os = new BufferedOutputStream(
			// new FileOutputStream(file));
			os = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.flush();
				os.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * @Description: 将Bitmap另存为指定的PNG文件
	 * @param bitmap
	 * @param pathName
	 * @return：void
	 */
	public static boolean writePhotoPng(Bitmap bitmap, String pathName) {
		if (bitmap == null || TextUtils.isEmpty(pathName)) {
			return false;
		}
		File file = new File(pathName);
		FileOutputStream os = null;
		try {
			file.createNewFile();
			os = new FileOutputStream(file);
			// BufferedOutputStream os = new BufferedOutputStream(
			// new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.flush();
				os.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * bitmap转化成字节数组
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		return baos.toByteArray();
	}
}
