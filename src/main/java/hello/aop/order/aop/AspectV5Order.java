package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Slf4j
//@Aspect // 이거를 해당 어드바이스를 직접 감싸고 있는 클래스에다 적용해야함
// 사실 이렇게 클래스 안에 내부 static 클래스로 만들지 않고, 그냥 각각을 따로 외부에 독립적인 클래스로 만들고 거기에 어드바이스 구현로직을 삽입해도됨.
public class AspectV5Order {

    @Aspect
    @Order(2) // 어드바이스 적용 순서 세팅하려면 이걸써야하는데, 주의점은 클래스 위에다만 적용이 가능함
    public static class LogAspect {
        @Around("hello.aop.order.aop.Pointcuts.allOrder()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[log] {}", joinPoint.getSignature()); // join point 시그니쳐
            return joinPoint.proceed(); // 이걸해야 실제 타켓 호출됨
        }
    }

    @Aspect
    @Order(1)
    public static class TxAspect {
        @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
        public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
            try {
                log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
                Object result = joinPoint.proceed();
                log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
                return result;
            } catch (Exception e) {
                log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
                throw e;
            } finally {
                log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
            }
        }
    }

}
