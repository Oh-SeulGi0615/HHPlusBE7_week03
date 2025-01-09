package kr.hhplus.be.server.exeption;

public class InsufficientPointException extends RuntimeException{
    public InsufficientPointException(String message) {
        super(message);
    }
}
