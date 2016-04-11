package com.example.androidgifmaker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class ScreenShot {
	public static String TAG = "ScreenShot";
	public static ArrayList<Bitmap> bitmaps; //Add your bitmaps from internal or external storage.

	// 获取指定Activity的截屏，保存到png文件
	private static Bitmap takeScreenShot(Activity activity) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		Log.i(TAG, "状态栏的高度" + statusBarHeight);
		
		int wintop = activity.getWindow().findViewById(android.R.id.content).getTop();
		int titleBarHeight = wintop-statusBarHeight;
		Log.i(TAG, "标题栏的高度:"+ titleBarHeight);

		// 获取屏幕长和高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		Log.i(TAG, "屏幕的宽度：" + width);
		Log.i(TAG, "屏幕的高度：" + height);
		// 去掉标题栏
		// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return b;
	}

	// 保存到sdcard
	private static void savePic(Bitmap b, String strFileName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(strFileName);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void prepareShoot(){
		bitmaps = new ArrayList<Bitmap>();
	}

	// 程序入口
	public static void shoot(Activity a) {
//		Date now = new Date();
//		DateFormat date = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM); // 显示日期时间（精确到秒）
//		String str2 = date.format(now);
//		String[] strs = str2.split(":");
//		StringBuffer str3 = new StringBuffer();
//		for (String str : strs) {
//			str3.append(str);
//			str3.append("_");
//		}
//		str3.deleteCharAt(str3.length() - 1);
//		ScreenShot.savePic(ScreenShot.takeScreenShot(a), "sdcard/save_" + str3 + ".png");
		if(bitmaps!=null){
			bitmaps.add(ScreenShot.takeScreenShot(a));
		}
	}
}
