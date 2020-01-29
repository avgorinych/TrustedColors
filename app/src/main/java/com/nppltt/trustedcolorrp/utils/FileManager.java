package com.nppltt.trustedcolorrp.utils;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileManager {

	public void saveData(Activity activity, Object object, String fileName) throws IOException {

		Gson gson = new Gson();
		String json = gson.toJson(object);
		FileOutputStream fos = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
		fos.write(json.getBytes());
		fos.close();
	}

	public <T> T loadData(Activity activity,  Class<T> classOfT, String fileName) throws Exception {

		FileInputStream inputStream = activity.openFileInput(fileName);
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			total.append(line);
		}
		r.close();
		inputStream.close();
		Gson gson = new Gson();
		Object obj = gson.fromJson(total.toString(), classOfT);
		if (obj != null) {
			return (T)obj;
		}
		else {
			throw new Exception("No Data found");
		}
	}
}
