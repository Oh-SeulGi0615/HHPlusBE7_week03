package kr.hhplus.be.server.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class DistributedLockAop {
    @Autowired
    private final RedissonClient redissonClient;

    public DistributedLockAop(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;}

    @Around("@annotation(distributedLock)")
    public Object distributedLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        System.out.println("AOP 진입");
        String lockKey = distributedLock.key();
        RLock rLock = redissonClient.getLock(lockKey);

        boolean available = false;
        try {
            available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            System.out.println("Locked");
            if (!available) {
                throw new IllegalStateException("다른 프로세스에서 이미 락을 사용 중입니다.");
            }

            return joinPoint.proceed();
        } finally {
            rLock.unlock();
            System.out.println("Unlocked");
        }
    }
}