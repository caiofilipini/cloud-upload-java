package com.caiofilipini.upload;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class InProgressTest {

	@Test
	public void shouldStoreInitialProgressInformation() {
		String uid = String.valueOf(System.currentTimeMillis());
		UploadProgress progress = new UploadProgress(1024L);
		
		InProgress.store(uid, progress);
		assertEquals(progress, InProgress.now(uid));
	}

}
