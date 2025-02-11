package kr.hhplus.be.server.exeption.customExceptions;

public class GoodsOutOfStockException extends RuntimeException{
    public GoodsOutOfStockException(String message) {
        super(message);
    }
}
