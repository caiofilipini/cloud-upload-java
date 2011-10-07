package com.caiofilipini.upload;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class InProgressTest {

	@Test
	public void shouldStoreProgressInformation() {
		String uid = String.valueOf(System.currentTimeMillis());
		UploadProgress progress = new UploadProgress(1024L, 0L);
		
		InProgress.start(uid, progress);
		assertEquals(progress, InProgress.now(uid));
	}

}
