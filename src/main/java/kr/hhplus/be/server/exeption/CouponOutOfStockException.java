package kr.hhplus.be.server.exeption;

public class CouponOutOfStockException extends RuntimeException {
    public CouponOutOfStockException(String message) {
        super(message);
    }
}
