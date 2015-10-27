package net.discoveringpossibilities.attendancesharp;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import net.discoveringpossibilities.attendancesharp.helpers.ListFileManager;

public class SplashScreen extends Activity {

	private Thread mSplashThread;
	private ListFileManager mListFileManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);
		final SplashScreen mSplashScreen = this;
		mListFileManager = new ListFileManager(mSplashScreen);

		mSplashThread = new Thread() {
			@Override
			public void run() {
				synchronized (this) {
					File Application = new File(getFilesDir() + "/Application.txt");
					try {
						if (Application.exists()
								&& Application.length() == getAssets().open("Application.txt").available()) {
							try {
								// Wait given period of time or exit on touch
								wait(4000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							mListFileManager.clearApplicationFiles();
							mListFileManager.copyAssetsFileToPhone("Application.txt");
							mListFileManager.copyAssetsFolderToPhone("Attendance");
							mListFileManager.copyAssetsFolderToPhone("TimeTable");
							mListFileManager.copyAllDirectories();
							mSplashScreen.runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(mSplashScreen, "Application files created!", Toast.LENGTH_SHORT).show();
								}
							});
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				SplashScreen.this.finish();
				Intent intent = new Intent();
				intent.setClass(mSplashScreen, HomeActivity.class);
				startActivity(intent);
			}
		};
		mSplashThread.start();
	}
}