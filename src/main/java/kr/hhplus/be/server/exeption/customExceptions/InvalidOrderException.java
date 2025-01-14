package kr.hhplus.be.server.exeption.customExceptions;

public class InvalidOrderException extends RuntimeException{
    public InvalidOrderException(String message) {
        super(message);
    }
}
