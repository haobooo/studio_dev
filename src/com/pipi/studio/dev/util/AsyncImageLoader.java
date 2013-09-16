package com.pipi.studio.dev.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pipi.studio.dev.common.Constants;
import com.pipi.studio.dev.common.MyApplication;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.widget.Toast;
import cn.azsy.android.bjly_en.R;

/**
 * 异步图片下载器
 * 
 * @author huangyu
 * 
 */
public class AsyncImageLoader {
	private static final String TAG = AsyncImageLoader.class.getSimpleName();
	private static Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	// static ThreadPool threadPool_image = new ThreadPool(2);
	public static ExecutorService pool = Executors.newFixedThreadPool(2);

	public static void clearImageMap() {
		// if(imageCache!=null && imageCache.size()>100){//当缓存大于100的时候
		imageCache.clear();
		// }
	}

	public static Bitmap loadDrawable(final String imageUrl,
			final ImageCallback callback) {
		MyApplication app = Constants.appInstance;
		String cacheDir = app.getCacheDirPath(MyApplication.CACHE_SDCARD);
		String chacheInternal = app.getCacheDirPath(MyApplication.CACHE_INTERNAL);
		
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "[loadDrawable] cacheDir=" + cacheDir);
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "[loadDrawable] chacheInternal=" + chacheInternal);
		
		Bitmap drawable = null;
		// 1、从缓存中取bitmap
		SoftReference<Bitmap> sf = imageCache.get(imageUrl);
		drawable = sf != null ? sf.get() : null;
		if (drawable != null) {
			if (LogUtil.IS_LOG) LogUtil.d(TAG, "[loadDrawable] load from cache");
			return drawable;
		}
		if (avaiableSdcard()) {// 如果有sd卡，读取sd卡
			drawable = getPicByPath(cacheDir, imageUrl);
			if (drawable != null) {
				if (LogUtil.IS_LOG) LogUtil.d(TAG, "get bitmap from sdcard!");
				imageCache.put(imageUrl, new SoftReference<Bitmap>(drawable));
				return drawable;
			}
		} else {// 如果没有sd卡，读取手机里面
			drawable = getPicByPath(chacheInternal, imageUrl);
			if (drawable != null) {
				if (LogUtil.IS_LOG) LogUtil.d(TAG, "get bitmap from disk");
				imageCache.put(imageUrl, new SoftReference<Bitmap>(drawable));
				return drawable;
			}
		}

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (callback != null) {
					callback.imageLoaded((Bitmap) msg.obj, imageUrl);
				}
			}
		};
		Runnable task = new Runnable() {
			public void run() {
				Bitmap drawable = loadImageFromUrl(imageUrl);
				handler.sendMessage(handler.obtainMessage(0, drawable));
			};
		};

		// new Thread(task).start();
		pool.execute(task);

		return null;
	}

	/**
	 * 判断是否存在sd卡
	 * 
	 * @return
	 */
	public static boolean avaiableSdcard() {
		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取图片
	 * 
	 * @param picName
	 * @return
	 */
	public static Drawable getPic_Draw_ByPath(String path, String picName) {
		picName = picName.substring(picName.lastIndexOf("/") + 1);
		String filePath = path + picName;
		return Drawable.createFromPath(filePath);
	}

	/**
	 * 获取图片
	 * 
	 * @param picName
	 * @return
	 */
	public static Bitmap getPicByPath(String path, String picName) {
		if (!"".equals(picName) && picName != null) {
			//picName = picName.substring(picName.lastIndexOf("/") + 1);
			String filePath = path + picName;

			// 判断文件是否存在
			File file = new File(filePath);
			if (!file.exists()) {// 文件不存在
				return null;
			}

			Bitmap bitmap = BitmapFactory.decodeFile(filePath);
			return bitmap;
		} else {
			return null;
		}

	}

	public static Bitmap loadDrawable(String imageUrl) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Bitmap> sf = imageCache.get(imageUrl);
			Bitmap drawable = sf != null ? sf.get() : null;
			if (drawable != null) {
				return drawable;
			} else {
				return null;
			}
		}
		return null;
	}

	protected static Bitmap loadImageFromUrl(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10 * 1000);
			conn.connect();
			InputStream inputStream = conn.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
	        if(null != inputStream){
	        	inputStream.close();
	        	inputStream = null;
	        }
			conn.disconnect();
			if (bitmap != null) {
				LogUtil.d("from net", "get bitmap from net");
				imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));
				savePic(bitmap, imageUrl);// 保存图片
			}
			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}

	public static void savePic(Bitmap bitmap, String imageUrl) {
		if (bitmap != null && imageUrl != null && !"".equals(imageUrl)) {
			if (avaiableSdcard()) {// 如果有sd卡，保存在sd卡
				savePicToSdcard(bitmap, imageUrl);
			} else {// 如果没有sd卡，保存在手机里面
				saveToDataDir(bitmap, imageUrl);
			}
		}
	}

	/**
	 * 将图片保存在sd卡
	 * 
	 * @param bitmap
	 *            图片
	 * @param picName
	 *            图片名称（同新闻id名）
	 */
	private static void savePicToSdcard(Bitmap bitmap, String picName) {
		MyApplication app = Constants.appInstance;
		String cacheDir = app.getCacheDirPath(MyApplication.CACHE_SDCARD);
		
		//picName = picName.substring(picName.lastIndexOf("/") + 1);
		File file = new File(cacheDir + picName);
		FileOutputStream outputStream;
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				outputStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
				if(null != outputStream){
					outputStream.close();
					outputStream = null;
				}
			} catch (Exception e) {
				// Log.e("", e.toString());
			}
		}
	}

	/**
	 * 保存文件到应用目录
	 * 
	 * @param bitmap
	 * @param fileName
	 *            文件名称
	 */
	static void saveToDataDir(Bitmap bitmap, String fileName) {
		MyApplication app = Constants.appInstance;
		String chacheInternal = app.getCacheDirPath(MyApplication.CACHE_INTERNAL);
		
		//fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		File file = new File(chacheInternal + fileName);
		FileOutputStream outputStream;
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				outputStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
				outputStream.close();
			} catch (Exception e) {
				Log.e("", e.toString());
			}
		}
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl);
	}

	/**
	 * @Title :deleteFile
	 * @Description :TODO
	 * @params @param file
	 * @return void
	 * 
	 */
	public static  void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		}
	}
	
	public static void loadAndSaveFile(final Context context, final String url, final String path) {
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "[loadAndSaveFile] parent=" + path);
		if (isFileExist(path, url)) {
			Toast.makeText(context.getApplicationContext(), context.getString(R.string.download_finished_with_file_already_exists), Toast.LENGTH_SHORT).show();
			return;
		}
		
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				boolean success = (Boolean) msg.obj;
				
				if (LogUtil.IS_LOG) LogUtil.d(TAG, "[loadAndSaveFile] success=" + success);
				if (success) {
					String name = url.substring(url.lastIndexOf("/") + 1);
					String filePath = path + File.separator + name;
					
					File picture = new File(filePath);
					if (picture.exists()) {
						Uri result = Uri.fromFile(picture);
						
						if (LogUtil.IS_LOG) LogUtil.d(TAG, "[loadAndSaveFile] result=" + result);
						
						ContentValues values = new ContentValues(2);
						values.put(ImageColumns.DISPLAY_NAME, name);
						values.put(ImageColumns.DATA, filePath);
						context.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
						
//						Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result);
//						intent.setDataAndType(result, "image/*");
//						context.sendBroadcast(intent);
					}
					Toast.makeText(context.getApplicationContext(), context.getString(R.string.download_finished, path), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context.getApplicationContext(), context.getString(R.string.download_failed, path), Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		Runnable task = new Runnable() {
			public void run() {
				boolean success = loadAndSaveFileFromUrl(url, path);
				handler.sendMessage(handler.obtainMessage(0, success));
			};
		};

		new Thread(task).start();
		//pool.execute(task);
	}
	
	protected static boolean loadAndSaveFileFromUrl(String fileUrl, String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			if (LogUtil.IS_LOG) LogUtil.d(TAG, "[loadAndSaveFileFromUrl] create parent directory =" + path);
			dir.mkdirs();
		}
		
		String name = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
		String filePath = path + File.separator + name;
		
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "[loadAndSaveFileFromUrl] filePath=" + filePath);
		try {
			URL url = new URL(fileUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10 * 1000);
			conn.connect();
			InputStream inputStream = conn.getInputStream();
			FileOutputStream outputStream = new FileOutputStream(filePath);
			
	        if(null != inputStream){
	        	byte[] buffer = new byte[8192];
				int count = 0;
				
				// 开始复制db文件
				while ((count = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, count);
				}
				
				outputStream.close();
				inputStream.close();
	        }
			conn.disconnect();

			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isFileExist(String path, String picName) {
		if (!"".equals(picName) && picName != null) {
			picName = picName.substring(picName.lastIndexOf("/") + 1);
			String filePath = path + File.separator + picName;

			// 判断文件是否存在
			File file = new File(filePath);
			if (!file.exists()) {// 文件不存在
				return false;
			}

			return true;
		} else {
			return false;
		}

	}
}
