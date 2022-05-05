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
 * this, target
 *
 * 1) 정의
 *  - this : 스프링 빈 객체(스프링 AOP 프록시)를 대상으로 하는 조인 포인트
 *  - target : Target 객체(스프링 AOP 프록시가 가르키는 실제 대상)를 대상으로 하는 조인 포인트
 *
 * 2) 설명
 *  - this , target 은 다음과 같이 적용 타입 하나를 정확하게 지정해야 한다.
 *      this(hello.aop.member.MemberService)
 *      target(hello.aop.member.MemberService)
 *  - * 같은 패턴을 사용할 수 없다.
 *  - 부모 타입을 허용한다.
 *
 * 3) this vs target
 *  - 단순히 타입 하나를 정하면 되는데, this 와 target 은 어떤 차이가 있을까?
 *  - 스프링에서 AOP를 적용하면 실제 target 객체 대신에 프록시 객체가 스프링 빈으로 등록된다.
 *      - this 는 스프링 빈으로 등록되어 있는 프록시 객체를 대상으로 포인트컷을 매칭한다.
 *      - target 은 실제 target 객체를 대상으로 포인트컷을 매칭한다.
 *
 * 4) 프록시 생성 방식에 따른 차이
 *  - 스프링은 프록시를 생성할 때 JDK 동적 프록시와 CGLIB를 선택할 수 있다. 둘의 프록시를 생성하는 방식이 다르기 때문에 차이가 발생한다.
 *      - JDK 동적 프록시: 인터페이스가 필수이고, 인터페이스를 구현한 프록시 객체를 생성한다.
 *      - CGLIB: 인터페이스가 있어도 구체 클래스를 상속 받아서 프록시 객체를 생성한다.
 *
 * 5) JDK 동적 프록시 vs CGLIB 차이에 따른 this, target의 AOP 적용 여부에 대해선 pdf 참고 자료 및 그림을 통해 이해하기!
 */
@Slf4j
@Import(ThisTargetTest.ThisTargetAspect.class)
@SpringBootTest
//@SpringBootTest(properties = "spring.aop.proxy-target-class=false") // application.properties에 옵션을 줘도 되고, 직접 이렇게 줘도 됨
public class ThisTargetTest {

    @Autowired
    MemberService memberService;

    @Test
    void success() {
        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Aspect
    static class ThisTargetAspect {

        // this-interface: 부모 타입 허용
        @Around("this(hello.aop.member.MemberService)")
        public Object doThisInterface(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[this-interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        // target-interface: 부모 타입 허용
        @Around("target(hello.aop.member.MemberService)")
        public Object doTargetInterface(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[target-interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        // this-concrete
        @Around("this(hello.aop.member.MemberServiceImpl)")
        public Object doThis(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[this-concrete] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        // target-concrete
        @Around("target(hello.aop.member.MemberServiceImpl)")
        public Object doTarget(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[target-concrete] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}
