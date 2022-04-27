package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class AspectV2 {

    // 포인트컷과 어드바이스를 분리할 수도 있음.
    @Pointcut("execution(* hello.aop.order..*(..))")
    private void allOrder() {} // 포인트컷 시그니쳐

    // 만약 분리하게 된다면 메소드() 를 Around에 넣어줘야 함.
    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature()); // join point 시그니쳐
        return joinPoint.proceed(); // 이걸해야 실제 타켓 호출됨
    }
}