package com.caiofilipini.upload;

import java.util.HashMap;
import java.util.Map;

public class InProgress {
	
	private static final Map<String, UploadProgress> IN_PROGRESS = new HashMap<String, UploadProgress>();
	
	public static UploadProgress now(String uid) {
		return IN_PROGRESS.get(uid);
	}
	
	public static void store(String uid, UploadProgress progress) {
		IN_PROGRESS.put(uid, progress);
	}

}
