package kr.hhplus.be.server.exeption;

import kr.hhplus.be.server.api.controller.*;
import kr.hhplus.be.server.exeption.customExceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = {RestController.class}, basePackageClasses = {
        UserController.class, PaymentController.class, OrderController.class, GoodsController.class, CouponController.class
})
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserException(InvalidUserException ex) {
        log.error("InvalidUserException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("InvalidUserException", "유저를 찾을 수 없습니다.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ExistUserException.class)
    public ResponseEntity<ErrorResponse> handleExistUserException(ExistUserException ex) {
        log.error("ExistUserException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("ExistUserException", "이미 등록된 유저입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidChargeUnitException.class)
    public ResponseEntity<ErrorResponse> handleInvalidChargeUnitException(InvalidChargeUnitException ex) {
        log.error("InvalidChargeUnitException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("InvalidChargeUnitException", "충전 가능한 금액은 0원 초과 10원 단위입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ExceedMaxChargeAmountException.class)
    public ResponseEntity<ErrorResponse> handleExceedMaxChargeAmountException(ExceedMaxChargeAmountException ex) {
        log.error("ExceedMaxChargeAmountException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("ExceedMaxChargeAmountException", "1회 충전 가능한 금액은 최대 1,000,000원 입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ExceedMaxBalanceException.class)
    public ResponseEntity<ErrorResponse> handleExceedMaxBalanceException(ExceedMaxBalanceException ex) {
        log.error("ExceedMaxBalanceException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("ExceedMaxBalanceException", "보유할 수 있는 최대 금액은 10,000,000원 입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaymentException(InvalidPaymentException ex) {
        log.error("InvalidPaymentException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("InvalidPaymentException", "결제 정보를 찾을 수 없습니다.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderException(InvalidOrderException ex) {
        log.error("InvalidOrderException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("InvalidOrderException", "주문 정보를 찾을 수 없습니다.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(AlreadyProcessedOrderException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyProcessedOrderException(AlreadyProcessedOrderException ex) {
        log.error("AlreadyProcessedOrderException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("AlreadyProcessedOrderException", "이미 처리된 주문입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AlreadyProcessedPaymentException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyProcessedPaymentException(AlreadyProcessedPaymentException ex) {
        log.error("AlreadyProcessedPaymentException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("AlreadyProcessedPaymentException", "이미 처리 완료된 결제입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidGoodsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidGoodsException(InvalidGoodsException ex) {
        log.error("InvalidGoodsException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("InvalidGoodsException", "상품 정보를 찾을 수 없습니다.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ExistGoodsException.class)
    public ResponseEntity<ErrorResponse> handleExistGoodsException(ExistGoodsException ex) {
        log.error("ExistGoodsException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("ExistGoodsException", "이미 등록된 상품입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidCouponException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCouponException(InvalidCouponException ex) {
        log.error("InvalidCouponException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("InvalidCouponException", "쿠폰 정보를 찾을 수 없습니다.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ExistCouponException.class)
    public ResponseEntity<ErrorResponse> handleExistCouponException(ExistCouponException ex) {
        log.error("ExistCouponException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("ExistCouponException", "이미 발급받은 쿠폰입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CannotUseCouponException.class)
    public ResponseEntity<ErrorResponse> handleCannotUseCouponException(CannotUseCouponException ex) {
        log.error("CannotUseCouponException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("CannotUseCouponException", "사용할 수 없는 쿠폰입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InsufficientPointException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientPointException(InsufficientPointException ex) {
        log.error("InsufficientPointException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("InsufficientPointException", "잔액이 부족합니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(GoodsOutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleGoodsOutOfStockException(GoodsOutOfStockException ex) {
        log.error("GoodsOutOfStockException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("GoodsOutOfStockException", "재고가 부족합니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ExpiredCouponException.class)
    public ResponseEntity<ErrorResponse> handleExpiredCouponException(ExpiredCouponException ex) {
        log.error("ExpiredCouponException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("ExpiredCouponException", "만료된 쿠폰입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CouponOutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleCouponOutOfStockException(CouponOutOfStockException ex) {
        log.error("CouponOutOfStockException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("CouponOutOfStockException", "쿠폰이 모두 소진되었습니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        log.error("IllegalStateException 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("IllegalStateException", "예상치 못한 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Exception 발생: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("Exception", "다른 프로세스에서 이미 락을 사용 중입니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}