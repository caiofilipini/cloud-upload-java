package com.caiofilipini.upload.progress;

import java.util.HashMap;
import java.util.Map;

public class InProgress {

    private static final Map<String, UploadProgress> IN_PROGRESS = new HashMap<String, UploadProgress>();

    public static void store(String uid, UploadProgress progress) {
        IN_PROGRESS.put(uid, progress);
    }

    public static UploadProgress now(String uid) throws ProgressNotFoundException {
        if (isValid(uid)) {
            return get(uid);
        }
        throw notFound(uid);
    }

    public static void abort(String uid) {
        IN_PROGRESS.remove(uid);
    }

    private static UploadProgress get(String uid) {
        UploadProgress progress = IN_PROGRESS.get(uid);
        if (progress == null) {
            throw notFound(uid);
        }
        return progress;
    }

    private static ProgressNotFoundException notFound(String uid) {
        return new ProgressNotFoundException("Invalid uid: " + uid);
    }

    private static boolean isValid(String uid) {
        return uid != null && !uid.trim().isEmpty();
    }

}
