package hello.aop.pointcut;

import hello.aop.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * @annotation
 *
 * 정의
 * @annotation : 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭
 *
 * 설명
 * @annotation(hello.aop.member.annotation.MethodAop)
 * 다음과 같이 메서드(조인 포인트)에 애노테이션이 있으면 매칭한다.
 * public class MemberServiceImpl {
 *       @MethodAop("test value")
 *       public String hello(String param) {
 *           return "ok";
 *       }
 * }
 *
 * @args
 *
 * 정의
 * @args : 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트
 *
 * 설명
 * 전달된 인수의 런타임 타입에 @Check 애노테이션이 있는 경우에 매칭한다. @args(test.Check)
 */
@Slf4j
@Import(AtAnnotationTest.AtAnnotationAspect.class)
@SpringBootTest
public class AtAnnotationTest {

    @Autowired
    MemberService memberService;

    @Test
    void success() {
        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Aspect
    static class AtAnnotationAspect {

        @Around("@annotation(hello.aop.member.annotation.MethodAop)")
        public Object dotAtAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[@annotation] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}
