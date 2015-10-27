package net.discoveringpossibilities.attendancesharp.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class ListFileManager {

	private AssetManager mAssetManager;
	private File mFilesDirectory;
	private Context mContext;
	private ArrayList<String> DirectoryArray = new ArrayList<String>();

	public ListFileManager(Context _context) {
		mContext = _context;
		mAssetManager = mContext.getAssets();
		mFilesDirectory = mContext.getFilesDir();
	}
	
	public void copyAllDirectories() {
		if (DirectoryArray != null)
			for (int num = 0; num < DirectoryArray.size(); num++)
				copyAssetsFolderToPhone(DirectoryArray.get(num));
	}

	public void copyAssetsFolderToPhone(String assetsFolder) {
		String[] files = null;
		String ExternalFolder = mFilesDirectory + "/" + assetsFolder;
		try {
			files = mAssetManager.list(assetsFolder);
		} catch (IOException e) {
			Log.e("etag", e.getMessage());
		}
		File f = new File(ExternalFolder);
		if (!f.exists()) {
			f.mkdir();
			File f2 = new File(ExternalFolder);
			if (f2.isDirectory())
				Log.i("Directory Created", ExternalFolder);
		}
		for (String filename : files) {
			try {
				if (mAssetManager.list(assetsFolder + "/" + filename).length != 0)
					DirectoryArray.add(assetsFolder + "/" + filename);
			} catch (IOException e) {
				Log.e("exceptiontag", e.getMessage());
			}
			copyAssetsFileToPhone(assetsFolder + "/" + filename);
		}
	}
	
	public void copyAssetsFileToPhone(String filename){
		InputStream in = null;
		OutputStream out = null;
		try {
			in = mAssetManager.open(filename);
			File OutSourceFile = new File(mFilesDirectory + "/", filename);
			OutSourceFile.createNewFile();
			out = new FileOutputStream(OutSourceFile);
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			Log.i("File Created", mFilesDirectory  + "/" + filename);
		} catch (Exception e) {
			Log.e("Directory exception", e.getMessage());
		}
	}

	public void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public void clearApplicationFiles() {
		if (mFilesDirectory != null && mFilesDirectory.isDirectory()) {
			try {
				ArrayList<File> stack = new ArrayList<File>();
				File[] children = mFilesDirectory.listFiles();
				for (File child : children) {
					stack.add(child);
				}
				while (stack.size() > 0) {
					File f = stack.get(stack.size() - 1);
					if (f.isDirectory() == true) {
						boolean empty = f.delete();
						if (empty == false) {
							File[] files = f.listFiles();
							if (files.length != 0) {
								for (File tmp : files) {
									stack.add(tmp);
								}
							}
						} else {
							stack.remove(stack.size() - 1);
						}
					} else {
						f.delete();
						stack.remove(stack.size() - 1);
					}
				}
			} catch (Exception e) {
				// Failed to clean files.
			}
		}
	}
}