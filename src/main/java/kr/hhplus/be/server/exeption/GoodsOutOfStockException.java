package kr.hhplus.be.server.exeption;

public class GoodsOutOfStockException extends RuntimeException{
    public GoodsOutOfStockException(String message) {
        super(message);
    }
}
