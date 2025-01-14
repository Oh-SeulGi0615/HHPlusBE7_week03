package kr.hhplus.be.server.exeption.customExceptions;

public class InvalidPointException extends RuntimeException {
    public InvalidPointException(String message) {
        super(message);
    }
}
