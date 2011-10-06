package com.caiofilipini.upload;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UploadProgressTest {
	
	@Test
	public void shouldCalculatePercentageCompleted() {
		UploadProgress progress = new UploadProgress(1024L, 256L);
		Integer expectedPercentage = 25;
		assertEquals(expectedPercentage, progress.calculatePercentage());
	}
	
	@Test
	public void shouldRoundPercentageValueToAnInteger() {
		UploadProgress progress = new UploadProgress(2048L, 225L);
		Integer expectedPercentage = 11;
		assertEquals(expectedPercentage, progress.calculatePercentage());
	}
	
	@Test
	public void shouldReturn100EvenIfPercentageIsGreater() {
		// This may seem a little awkward, but since total size
		// is based in the Content-Length header (which includes other POST data),
		// it is necessary.
		UploadProgress progress = new UploadProgress(42L, 44L);
		Integer expectedPercentage = 100;
		assertEquals(expectedPercentage, progress.calculatePercentage());
	}

}
