package com.caiofilipini.upload;

public class UploadProgress {
	
	private final Long total;
	private final Long completed;

	public UploadProgress(Long total, Long completed) {
		this.total = total;
		this.completed = completed;
	}
	
	public Integer calculatePercentage() {
		int percentage = (int) Math.round(completed.doubleValue() / total.doubleValue() * 100.0);
		// This may seem a little awkward, but since total size
		// is based in the Content-Length header (which includes other POST data),
		// it is necessary.
		return percentage > 100 ? 100 : percentage;
	}

}
