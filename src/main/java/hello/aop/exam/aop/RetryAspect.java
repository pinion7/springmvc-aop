package hello.aop.exam.aop;

import hello.aop.exam.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 재시도 하는 애스펙트이다.
 * @annotation(retry) , Retry retry 를 사용해서 어드바이스에 애노테이션을 파라미터로 전달한다.
 * retry.value() 를 통해서 애노테이션에 지정한 값을 가져올 수 있다.
 * 예외가 발생해서 결과가 정상 반환되지 않으면 retry.value() 만큼 재시도한다.
 */
@Slf4j
@Aspect
public class RetryAspect {


    // AOP자체를 파라미터로 넘겨주게 되면, 굳이 @annotation()안에 패키지 경로부터 끝까지 쓰지 않고 해당 AOP와 파라미터의 이름만 잘 맞춰도 인식이 됨!
    @Around("@annotation(retry)")
    public Object doRetry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        log.info("[retry] {} args={}", joinPoint.getSignature(), retry);

        int maxRetry = retry.value();
        Exception exceptionHolder = null;

        for (int retryCount = 1; retryCount <= maxRetry; ++retryCount) {
            try {
                log.info("[retry] try count={}/{}", retryCount, maxRetry);
                return joinPoint.proceed();
            } catch (Exception e) {
                exceptionHolder = e;
            }
        }

        throw exceptionHolder;
    }
}
