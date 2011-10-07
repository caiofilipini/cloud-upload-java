package com.caiofilipini.upload;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UploadProgressTest {

	@Test
	public void shouldStartWithZeroCompleted() {
		UploadProgress progress = new UploadProgress(2048L);
		Integer expectedPercentage = 0;
		assertEquals(expectedPercentage, progress.calculatePercentage());
	}
	
	@Test
	public void shouldUpdateCompleted() {
		UploadProgress progress = new UploadProgress(2048L);
		progress.completedMore(205);
		
		Integer expectedPercentage = 10;
		assertEquals(expectedPercentage, progress.calculatePercentage());
	}
	
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
		// is based in the Content-Length header (which may include other POST data),
		// it is necessary.
		UploadProgress progress = new UploadProgress(42L, 44L);
		Integer expectedPercentage = 100;
		assertEquals(expectedPercentage, progress.calculatePercentage());
	}

	@Test
	public void defineEquals() {
		shouldReturnTrueForSameInstance();
		shouldReturnTrueForEqualValues();
		shouldReturnFalseForInstancesOfOtherClasses();
		shouldReturnFalseForNull();
	}

	@Test
	public void defineHashCode() {
		shouldBeDifferentIfObjectsAreDifferent();
		shouldBeTheSameIfObjectsAreEqual();
	}

	private void shouldBeDifferentIfObjectsAreDifferent() {
		UploadProgress aProgress = new UploadProgress(42L, 21L);
		UploadProgress anotherProgress = new UploadProgress(88L, 12L);
		assertFalse(aProgress.hashCode() == anotherProgress.hashCode());
	}
	
	private void shouldBeTheSameIfObjectsAreEqual() {
		UploadProgress aProgress = new UploadProgress(42L, 21L);
		UploadProgress anotherProgress = new UploadProgress(42L, 21L);
		assertEquals(aProgress.hashCode(), anotherProgress.hashCode());
	}
	
	private void shouldReturnTrueForSameInstance() {
		UploadProgress aProgress = new UploadProgress(42L, 21L);
		assertEquals(aProgress, aProgress);
	}
	
	private void shouldReturnTrueForEqualValues() {
		UploadProgress aProgress = new UploadProgress(42L, 21L);
		UploadProgress anotherProgress = new UploadProgress(42L, 21L);
		assertEquals(aProgress, anotherProgress);
	}
	
	private void shouldReturnFalseForInstancesOfOtherClasses() {
		UploadProgress aProgress = new UploadProgress(42L, 21L);
		String notAProgress = "please return false!";
		assertFalse(aProgress.equals(notAProgress));
	}
	
	private void shouldReturnFalseForNull() {
		UploadProgress aProgress = new UploadProgress(42L, 21L);
		assertFalse(aProgress.equals(null));
	}

}
