package kr.hhplus.be.server.exeption;

public class InvalidPointException extends RuntimeException {
    public InvalidPointException(String message) {
        super(message);
    }
}
