package kr.hhplus.be.server.exeption;

public class InvalidOrderException extends RuntimeException{
    public InvalidOrderException(String message) {
        super(message);
    }
}
