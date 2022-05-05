package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.member.annotation.ClassAop;
import hello.aop.member.annotation.MethodAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * 매개변수 전달
 * 다음은 포인트컷 표현식을 사용해서 어드바이스에 매개변수를 전달할 수 있다.
 *  - this, target, args,@target, @within, @annotation, @args
 *
 * 다음과 같이 사용한다.
 *   @Before("allMember() && args(arg,..)")
 *   public void logArgs3(String arg) {
 *       log.info("[logArgs3] arg={}", arg);
 *   }
 * 포인트컷의 이름과 매개변수의 이름을 맞추어야 한다. 여기서는 arg 로 맞추었다.
 * 추가로 타입이 메서드에 지정한 타입으로 제한된다. 여기서는 메서드의 타입이 String 으로 되어 있기 때문에 다음과 같이 정의되는 것으로 이해하면 된다.
 *  - args(arg,..) args(String,..)
 *
 *  다양한 매개변수 전달 예시를 확인해보자.
 * logArgs1 : joinPoint.getArgs()[0] 와 같이 매개변수를 전달 받는다.
 * logArgs2 : args(arg,..) 와 같이 매개변수를 전달 받는다.
 * logArgs3 : @Before 를 사용한 축약 버전이다. 추가로 타입을 String 으로 제한했다.
 * this : 프록시 객체를 전달 받는다.
 * target : 실제 대상 객체를 전달 받는다.
 * @target , @within : 타입의 애노테이션을 전달 받는다.
 * @annotation : 메서드의 애노테이션을 전달 받는다. 여기서는 annotation.value() 로 해당
 * 애노테이션의 값을 출력하는 모습을 확인할 수 있다.
 */
@Slf4j
@Import(ParameterTest.ParameterAspect.class)
@SpringBootTest
public class ParameterTest {

    @Autowired
    MemberService memberService;

    @Test
    void success() {
        log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Aspect
    static class ParameterAspect {

        @Pointcut("execution(* hello.aop.member..*.*(..))")
        private void allMember() {

        }

        @Around("allMember()")
        public Object logArgs1(ProceedingJoinPoint joinPoint) throws Throwable {
            Object arg1 = joinPoint.getArgs()[0];
            log.info("[logArgs1]{}, arg={}", joinPoint.getSignature(), arg1);
            return joinPoint.proceed();
        }

        @Around("allMember() && args(arg, ..)")
        public Object logArgs2(ProceedingJoinPoint joinPoint, Object arg) throws Throwable {
            log.info("[logArgs2]{}, arg={}", joinPoint.getSignature(), arg);
            return joinPoint.proceed();
        }

        @Before("allMember() && args(arg, ..)")
        public void logArgs3(String arg) {
            log.info("[logArgs3] arg={}", arg);
        }

        // 프록시 객체
        @Before("allMember() && this(obj)")
        public void thisArgs(JoinPoint joinPoint, MemberService obj) {
            log.info("[this]{}, obj={}", joinPoint.getSignature(), obj.getClass());
        }

        // 실제 객체
        @Before("allMember() && target(obj)")
        public void targetArgs(JoinPoint joinPoint, MemberService obj) {
            log.info("[target]{}, obj={}", joinPoint.getSignature(), obj.getClass());
        }

        @Before("allMember() && @target(annotation)")
            public void atTarget(JoinPoint joinPoint, ClassAop annotation) {
            log.info("[@target]{}, obj={}", joinPoint.getSignature(), annotation);
        }

        @Before("allMember() && @within(annotation)")
        public void atWithin(JoinPoint joinPoint, ClassAop annotation) {
            log.info("[@within]{}, obj={}", joinPoint.getSignature(), annotation);
        }

        @Before("allMember() && @annotation(annotation)")
        public void atAnnotation(JoinPoint joinPoint, MethodAop annotation) {
            log.info("[@annotation]{}, annotationValue={}", joinPoint.getSignature(), annotation.value());
        }
    }
}
