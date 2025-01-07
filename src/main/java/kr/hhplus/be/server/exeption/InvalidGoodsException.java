package kr.hhplus.be.server.exeption;

public class InvalidGoodsException extends RuntimeException{
    public InvalidGoodsException(String message) {
        super(message);
    }
}
