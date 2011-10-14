package com.caiofilipini.upload.progress;

import org.junit.Test;

import com.caiofilipini.upload.progress.UploadProgress;

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
    public void shouldMakeFilePathAvailable() {
        UploadProgress progress = new UploadProgress(2048L);
        progress.fileAvailableAt("/files/completed_file.txt");
        assertEquals("/files/completed_file.txt", progress.getFilePath());
    }

    @Test
    public void shouldReturnJsonRepresentation() {
        UploadProgress progress = new UploadProgress(2048L, 225L);
        assertEquals("{\"completed\" : \"11\"}", progress.toJson());
    }

    @Test
    public void shouldIncludeFilePathInJsonRepresentation() {
        UploadProgress progress = new UploadProgress(2048L, 2048L);
        progress.fileAvailableAt("/files/my_greatest_song.mp3");
        assertEquals("{\"completed\" : \"100\", \"filePath\" : \"/files/my_greatest_song.mp3\"}", progress.toJson());
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
