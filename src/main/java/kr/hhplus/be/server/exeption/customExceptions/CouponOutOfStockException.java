package kr.hhplus.be.server.exeption.customExceptions;

public class CouponOutOfStockException extends RuntimeException {
    public CouponOutOfStockException(String message) {
        super(message);
    }
}
