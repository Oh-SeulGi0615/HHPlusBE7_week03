package kr.hhplus.be.server.exeption;

public class InvalidUserException extends RuntimeException{
    public InvalidUserException(String message) {
        super(message);
    }
}
