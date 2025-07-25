package vn.edu.fpt.transitlink.storage.domain.exception;

public class FileSizeExceededException extends StorageException {
    public FileSizeExceededException(String message) { super(message); }
}