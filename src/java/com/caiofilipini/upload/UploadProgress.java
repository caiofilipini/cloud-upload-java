package com.caiofilipini.upload;

public class UploadProgress {

    private final Long total;
    private Long completed;

    public UploadProgress(Long total, Long completed) {
        this.total = total;
        this.completed = completed;
    }

    public UploadProgress(Long total) {
        this(total, 0L);
    }

    public void completedMore(Integer increment) {
        this.completed += increment;
    }

    public Integer calculatePercentage() {
        return (int) Math.round(completed.doubleValue() / total.doubleValue() * 100.0);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!UploadProgress.class.equals(other.getClass())) {
            return false;
        }
        UploadProgress otherProgress = (UploadProgress) other;
        return this.total.equals(otherProgress.total)
                && this.completed.equals(otherProgress.completed);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + total.intValue();
        hash = hash * 37 + completed.intValue();
        return hash;
    }

}
