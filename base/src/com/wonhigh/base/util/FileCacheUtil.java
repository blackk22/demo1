
package com.wonhigh.base.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

/**
 * @ClassName: FileCacheUtil
 * @Description: 缓存文件管理
 * @author: li.xy
 * @date: 2014-5-5 下午6:02:19
 */
public class FileCacheUtil {
    /** 根文件夹名 */
    private final static String CACHE_DIR_ROOT = "/belle/bt/";
    
    /** 图片缓存目录 */
    private final static String CACHE_DIR_PIC = CACHE_DIR_ROOT + "pic";
    
    /** 语音缓存目录 */
    private final static String CACHE_DIR_VOICE = CACHE_DIR_ROOT + "voice";
    
    /** 视频缓存目录 */
    private final static String CACHE_DIR_VIDEO = CACHE_DIR_ROOT + "video";
    
    /** 其他文件缓存目录 */
    private final static String CACHE_DIR_FILE = CACHE_DIR_ROOT + "file";
    
    /** 临时文件缓存目录 */
    private final static String CACHE_DIR_TEMP = CACHE_DIR_ROOT + "temp";
    
    /** webview在内存中的缓存目录名 */
    public final static String CACHE_WEBVIEW_DATABASE = "webview_database";
    
    private static long NUM = 0;
    
    /**
     * @Description: 获取缓存根路径
     * @return: String
     */
    public static String getRootCacheDir() {
        String rootPath = FileHelper.getSDCardPath();
        if (!TextUtils.isEmpty(rootPath)) {
            return rootPath + CACHE_DIR_ROOT;
        }
        return "";
    }
    
    /**
     * @Description: 获取图片缓存目录
     * @return: String
     */
    public static String getPicCacheDir() {
        String rootPath = FileHelper.getSDCardPath();
        if (!TextUtils.isEmpty(rootPath)) {
            return rootPath + CACHE_DIR_PIC;
        }
        return "";
    }
    
    /**
     * @Description: 获取音频文件缓存目录
     * @return: String
     */
    public static String getVoiceCacheDir() {
        String rootPath = FileHelper.getSDCardPath();
        if (!TextUtils.isEmpty(rootPath)) {
            return rootPath + CACHE_DIR_VOICE;
        }
        return "";
    }
    
    /**
     * @Description: 获取视频缓存目录
     * @return: String
     */
    public static String getVideoCacheDir() {
        String rootPath = FileHelper.getSDCardPath();
        if (!TextUtils.isEmpty(rootPath)) {
            return rootPath + CACHE_DIR_VIDEO;
        }
        return "";
    }
    
    /**
     * @Description: 获取文件缓存目录
     * @return: String
     */
    public static String getFileCacheDir() {
        String rootPath = FileHelper.getSDCardPath();
        if (!TextUtils.isEmpty(rootPath)) {
            return rootPath + CACHE_DIR_FILE;
        }
        return "";
    }
    
    /**
     * @Description: 获取临时文件缓存目录
     * @return: String
     */
    public static String getTempCacheDir() {
        String rootPath = FileHelper.getSDCardPath();
        if (!TextUtils.isEmpty(rootPath)) {
            return rootPath + CACHE_DIR_TEMP;
        }
        return "";
    }
    
    /**
     * @Description: 删除图片缓存
     * @return: void
     */
    public static void deletePicCache() {
        FileHelper.deleteDirectory(getPicCacheDir());
    }
    
    /**
     * @Description: 删除音频缓存
     * @return: void
     */
    public static void deleteVoiceCache() {
        FileHelper.deleteDirectory(getVoiceCacheDir());
    }
    
    /**
     * @Description: 删除视频缓存
     * @return: void
     */
    public static void deleteVideoCache() {
        FileHelper.deleteDirectory(getVideoCacheDir());
    }
    
    /**
     * @Description: 删除文件缓存
     * @return: void
     */
    public static void deleteFileCache() {
        FileHelper.deleteDirectory(getFileCacheDir());
    }
    
    /**
     * @Description: 删除临时文件缓存
     * @return: void
     */
    public static void deleteTempCache() {
        FileHelper.deleteDirectory(getTempCacheDir());
    }
    
    /**
     * @Description: 删除所有外部SD卡缓存
     * @return: void
     */
    public static void deleteExternalAllCache(Context context) {
        FileHelper.deleteDirectory(getRootCacheDir());
        deleteExternalCache(context);
    }
    
    /**
     * @Description: 删除所有内存内部缓存
     * @return: void
     */
    public static void deleteInternalAllCache(Context context) {
        cleanRAMCache(context);
        cleanRAMDatabases(context);
        cleanRAMFiles(context);
        cleanSharedPreference(context);
    }
    
    /**
     * @Description: 删除所有缓存(包含内部和外部)
     * @return: void
     */
    public static void deleteAllCache(Context context) {
        deleteExternalAllCache(context);
        deleteInternalAllCache(context);
    }
    
    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
 	 * @param context
     */
    private static void deleteExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            FileHelper.deleteDirectory(context.getExternalCacheDir().getAbsolutePath());
        }
    }
    
    /** 
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
     *  @param context 
     */
    private static void cleanRAMCache(Context context) {
        FileHelper.deleteDirectory(context.getCacheDir().getAbsolutePath());
    }
    
    /** 
     * @Description: 清楚内存中的数据库
     * @param context    
     * @return：void    
     */
    private static void cleanRAMDatabases(Context context) {
        FileHelper.deleteDirectory("/data/data/"+ context.getPackageName() + "/databases");
    }
    
    /** 
     * @Description: 删除内存中的SharedPreference
     * @param context    
     * @return：void    
     */
    private static void cleanSharedPreference(Context context) {
        FileHelper.deleteDirectory("/data/data/" + context.getPackageName() + "/shared_prefs");
    }
    
    /** 
     * 清除/data/data/com.xxx.xxx/files下的内容
     *  @param context */
    private static void cleanRAMFiles(Context context) {
        FileHelper.deleteDirectory(context.getFilesDir().getAbsolutePath());
    }
    
    /**
     * @Description: 生成一个缓存文件或文件夹名
     * @param type 文件类型，后缀（如：.mp3, .jpg等）,可为空
     * @return: String
     */
    public static String generateFileName(String type) {
        StringBuffer stringBuffer =
            new StringBuffer(String.valueOf(System.currentTimeMillis()));
        stringBuffer.append(String.valueOf((int)(Math.random() * 1000)));
        stringBuffer.append(String.valueOf(NUM));
        if (!TextUtils.isEmpty(type)) {
            stringBuffer.append(type);
        }
        NUM++;
        return stringBuffer.toString();
    }
}
