package com.caiofilipini.upload.progress;

import org.junit.Test;

import com.caiofilipini.upload.progress.InProgress;
import com.caiofilipini.upload.progress.ProgressNotFoundException;
import com.caiofilipini.upload.progress.UploadProgress;

import static org.junit.Assert.assertEquals;

public class InProgressTest {

    @Test
    public void shouldStoreInitialProgressInformation() {
        String uid = String.valueOf(System.currentTimeMillis());
        UploadProgress progress = new UploadProgress(1024L);

        InProgress.store(uid, progress);
        assertEquals(progress, InProgress.now(uid));
    }

    @Test(expected = ProgressNotFoundException.class)
    public void shouldAbortProgress() {
        String uid = String.valueOf(System.currentTimeMillis());
        UploadProgress progress = new UploadProgress(1024L);
        InProgress.store(uid, progress);

        InProgress.abort(uid);

        // expect this to throw a ProgressNotFoundException after aborting.
        InProgress.now(uid);
    }

    @Test(expected = ProgressNotFoundException.class)
    public void shouldThrowExceptionWhenNoProgressIsFoundForUid() {
        InProgress.now("42");
    }

    @Test(expected = ProgressNotFoundException.class)
    public void shouldThrowExceptionWhenUidIsNull() {
        InProgress.now(null);
    }

    @Test(expected = ProgressNotFoundException.class)
    public void shouldThrowExceptionWhenUidIsEmpty() {
        InProgress.now("");
    }

    @Test(expected = ProgressNotFoundException.class)
    public void shouldThrowExceptionWhenUidContainsWhitespacesOnly() {
        InProgress.now("   ");
    }

}
