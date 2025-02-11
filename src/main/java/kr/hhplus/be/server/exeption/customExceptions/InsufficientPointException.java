package kr.hhplus.be.server.exeption.customExceptions;

public class InsufficientPointException extends RuntimeException{
    public InsufficientPointException(String message) {
        super(message);
    }
}
