package com.example.androidgifmaker;

import java.io.File;
import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	ProgressDialog progressBar;
	private TextView tv;
	private int count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView)findViewById(R.id.text);
		
		new Thread(new TvRunnable()).start();
	}
	
	class TvRunnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				count++;
				myHandler.sendEmptyMessage(1);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_settings:
			new Thread(new ShootRunnable(2)).start();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class ShootRunnable implements Runnable{

		private int second;
		private int frameNum = 0;
		ShootRunnable(int second){
			this.second = second;
			ScreenShot.prepareShoot();
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(second>0){
				while (frameNum<10) {
					long start = System.currentTimeMillis();
					ScreenShot.shoot(MainActivity.this);
					long offset;
					if((offset = (System.currentTimeMillis()-start))<100){
						try {
							Thread.sleep(offset);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					frameNum++;
				}
				second--;
				frameNum = 0;
			}
			myHandler.sendEmptyMessage(0);
		}
	}
	
	Handler myHandler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what) {
			case 0:
				makeGif();
				break;
			case 1:
				tv.setText(count+"");
				break;
			default:
				break;
			}
		};
	};
	
	public void makeGif() {
		String name = "MyGif";
		
	    progressBar = ProgressDialog.show(this, "Converting...", "0%", true, false);
	    
	    GifThread gt = new GifThread(name);
		gt.start();
		
		Toast.makeText(
				this,
				"You can access the gif in your SD Card storage, under the file Flippy. This directory is: "
						+ Environment.getExternalStorageDirectory().toString()
						+ "/Gifs",
				Toast.LENGTH_LONG).show();
	}
	
	private class GifThread extends Thread{
		private String name; 
		private int i;
		
		public GifThread(String proj) { // ONLY WORKS AFTER SAVING
				name=proj;
		}
		
		@Override 
		public void run(){
			String root = Environment.getExternalStorageDirectory().toString();
			File myDir = new File(root + "/Gifs/");
			if(!myDir.exists())
				myDir.mkdirs();
			String fname = name;
			File file = new File(myDir, fname + ".gif");
			if (file.exists()){
				file.delete();
			}
			try {
				FileOutputStream out = new FileOutputStream(file);
				AnimatedGifMaker gifs = new AnimatedGifMaker();
				gifs.start(out);
				gifs.setFrameRate(10);
				gifs.setRepeat(0);
				gifs.setTransparent(new Color());
	
				for (i = 0; i < ScreenShot.bitmaps.size(); i++) {
					gifs.addFrame(ScreenShot.bitmaps.get(i));
					handler.sendEmptyMessage(1);
				}
				gifs.finish();
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
						Uri.parse("file://"
								+ Environment.getExternalStorageDirectory()))); // uM
																				// HACK
			} catch (Exception e) {
				e.printStackTrace();
			}
			handler.sendEmptyMessage(0);
		}
		@SuppressLint("HandlerLeak")
		private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	switch (msg.what) {
				case 0:
					progressBar.dismiss();
					break;
				case 1:
					progressBar.setMessage(i*100/(ScreenShot.bitmaps.size()-1)+"%");
					break;
				default:
					break;
				}
               
            }
        };
	}
}
