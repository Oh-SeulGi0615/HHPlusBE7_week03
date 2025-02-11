package kr.hhplus.be.server.exeption.customExceptions;

public class InvalidGoodsException extends RuntimeException{
    public InvalidGoodsException(String message) {
        super(message);
    }
}
