package kr.hhplus.be.server.exeption.customExceptions;

public class ExistGoodsException extends RuntimeException{
    public ExistGoodsException(String message) {
        super(message);
    }
}
