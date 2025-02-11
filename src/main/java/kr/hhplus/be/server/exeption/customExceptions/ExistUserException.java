package kr.hhplus.be.server.exeption.customExceptions;

public class ExistUserException extends RuntimeException{
    public ExistUserException(String message) {
        super(message);
    }
}
