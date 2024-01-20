package mc.passcraft.utils.resources;

public class ArchiveFileException extends Exception {

    public ArchiveFileException() {}

    public ArchiveFileException(String message) {
        super(message);
    }

    public ArchiveFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchiveFileException(Throwable cause) {
        super(cause);
    }

    protected ArchiveFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}