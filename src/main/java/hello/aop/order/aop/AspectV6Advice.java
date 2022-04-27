package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * 어드바이스는 앞서 살펴본 @Around 외에도 여러가지 종류가 있다.
 * <p>
 * 어드바이스 종류
 *
 * @Around : 메서드 호출 전후에 수행, 가장 강력한 어드바이스, 조인 포인트 실행 여부 선택, 반환 값 변환, 예외 변환 등이 가능
 * @Before : 조인 포인트 실행 이전에 실행
 * @AfterReturning : 조인 포인트가 정상 완료후 실행
 * @AfterThrowing : 메서드가 예외를 던지는 경우 실행
 * @After : 조인 포인트가 정상 또는 예외에 관계없이 실행(finally)
 *
 * JoinPoint 인터페이스의 주요 기능
 *  - getArgs() : 메서드 인수를 반환합니다.
 *  - getThis() : 프록시 객체를 반환합니다.
 *  - getTarget() : 대상 객체를 반환합니다.
 *  - getSignature() : 조언되는 메서드에 대한 설명을 반환합니다. toString() : 조언되는 방법에 대한 유용한 설명을 인쇄합니다.
 *
 * ProceedingJoinPoint 인터페이스의 주요 기능
 *  - proceed() : 다음 어드바이스나 타켓을 호출한다.
 */
@Slf4j
@Aspect
public class AspectV6Advice {

    /**
     * 1. @Around: 메서드의 실행의 주변에서 실행 (메서드 실행 전후에 작업을 수행 - 다음에 나올 모든 기능을 다 가지고 있는 것)
     *  - 가장 강력한 어드바이스
     *      - 조인 포인트 실행 여부 선택 joinPoint.proceed() 호출 여부 선택
     *      - 전달 값 변환: joinPoint.proceed(args[])
     *      - 반환 값 변환
     *      - 예외 변환
     *      - 트랜잭션 처럼 try ~ catch~ finally 모두 들어가는 구문 처리 가능
     *  - 어드바이스의 첫 번째 파라미터는 ProceedingJoinPoint 를 사용해야 한다.
     *  - proceed() 를 통해 대상을 실행한다.
     *  - proceed() 를 여러번 실행할 수도 있음(재시도)
     */
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            //@Before
            log.info("[around][트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();
            //@AfterReturning
            log.info("[around][트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            //@AfterThrowing
            log.info("[around][트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            //@After
            log.info("[around][리소스 릴리즈] {}", joinPoint.getSignature()); }
    }


    /**
     * 2. @Before: 조인 포인트 실행 전
     *  - @Around 와 다르게 작업 흐름을 변경할 수는 없다.
     *  - @Around 는 ProceedingJoinPoint.proceed() 를 호출해야 다음 대상이 호출된다. 만약 호출하지 않으면 다음 대상이 호출되지 않는다.
     *  - 반면에 @Before 는 ProceedingJoinPoint.proceed() 자체를 사용하지 않는다.
     *  - 메서드 종료시 자동으로 다음 타켓이 호출된다.
     *  - 물론 예외가 발생하면 다음 코드가 호출되지는 않는다.
     *  - Before는 joinPoint로 getSignature를 로그에 남기고 싶으면, ProceedingJoinPoint가 아닌 JoinPoint를 매개변수 타입으로 받아야함.
     *  - 그리고 joinPoint.proceed() 코드를 작성할 필요가 없음. 그냥 Before 자체적으로 마지막에 이를 실행시켜줌
     */
    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doBefore(JoinPoint joinPoint) {
        log.info("[before] {}", joinPoint.getSignature());
    }


    /**
     * 3. @AfterReturning: 메서드 실행이 정상적으로 반환될 때 실행
     *  - returning 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다.
     *  - returning 절에 지정된 타입의 값을 반환하는 메서드만 대상으로 실행한다. (부모 타입을 지정하면 모든 자식 타입은 인정된다.)
     *  - @Around 와 다르게 반환되는 객체를 변경할 수는 없다. 반환 객체를 변경하려면 @Around 를 사용해야한다.
     *  - 참고로 반환 객체를 조작할 수는 있다.
     */
    @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
    public void doReturn(JoinPoint joinPoint, Object result) {
        log.info("[return] {} return={}", joinPoint.getSignature(), result);
    }


    /**
     * 4. @AfterThrowing: 메서드 실행이 예외를 던져서 종료될 때 실행
     *  - throwing 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다.
     *  - throwing 절에 지정된 타입과 맞은 예외를 대상으로 실행한다. (부모 타입을 지정하면 모든 자식 타입은 인정된다.)
     */
    @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
    public void doThrowing(JoinPoint joinPoint, Exception ex) {
        log.info("[ex] {} message={}", joinPoint.getSignature(), ex.getMessage());
    }


    /**
     * 5. @After: 메서드 실행이 종료되면 실행
     *  - finally를 생각하면 된다.
     *  - 정상 및 예외 반환 조건을 모두 처리한다.
     *  - 일반적으로 리소스를 해제하는 데 사용한다.
     */
    @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doAfter(JoinPoint joinPoint) {
        log.info("[after] {}", joinPoint.getSignature());
    }
}

/**
 * 6. 기타사항
 * 1) 순서
 *  - 스프링은 5.2.7 버전부터 동일한 @Aspect 안에서 동일한 조인포인트의 우선순위를 정했다.
 *  - 실행 순서: @Around , @Before , @After , @AfterReturning , @AfterThrowing
 *  - 어드바이스가 적용되는 순서는 이렇게 적용되지만, 호출 순서와 리턴 순서는 반대라는 점을 알아두자.
 *  - 물론 @Aspect 안에 동일한 종류의 어드바이스가 2개 있으면 순서가 보장되지 않는다. 이 경우 앞서 배운 것 처럼 @Aspect 를 분리하고 @Order 를 적용하자.
 *
 * 2) @Around 외에 다른 어드바이스가 존재하는 이유
 *  - @Around 하나만 있어도 모든 기능을 수행할 수 있다.
 *  - 그런데 다른 어드바이스들이 존재하는 이유는 무엇일까?
 *  - @Around 예제 코드)
 *   @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
 *   public void doBefore(ProceedingJoinPoint joinPoint) {
 *       log.info("[before] {}", joinPoint.getSignature());
 *   }
 *  - 이 코드의 문제점을 찾을 수 있겠는가? 이 코드는 타켓을 호출하지 않는 문제가 있다.
 *  - 이 코드를 개발한 의도는 타켓 실행 전에 로그를 출력하는 것이다.
 *  - 그런데 @Around 는 항상 joinPoint.proceed() 를 호출해야 한다.
 *  - 만약 실수로 호출하지 않으면 타켓이 호출되지 않는 치명적인 버그가 발생한다.
 *
 *  - @Before 예제 코드)
 *   @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
 *   public void doBefore(JoinPoint joinPoint) {
 *       log.info("[before] {}", joinPoint.getSignature());
 *   }
 *  - @Before 는 joinPoint.proceed() 를 호출하는 고민을 하지 않아도 된다.
 *  - @Around 가 가장 넓은 기능을 제공하는 것은 맞지만, 실수할 가능성이 있다.
 *  - 반면에 @Before , @After 같은 어드바이스는 기능은 적지만 실수할 가능성이 낮고, 코드도 단순하다.
 *  - 그리고 가장 중요한 점이 있는데, 바로 이 코드를 작성한 의도가 명확하게 들어난다는 점이다.
 *  - @Before 라는 애노테이션을 보는 순간 아~ 이 코드는 타켓 실행 전에 한정해서 어떤 일을 하는 코드구나 라는 것이 들어난다.
 *
 * 3) 좋은 설계는 제약이 있는 것이다
 *  - @Around 만 있으면 되는데 왜? 이렇게 제약을 두는가? 제약은 실수를 미연에 방지한다. 일종에 가이드 역할을 한다.
 *  - 만약 @Around 를 사용했는데, 중간에 다른 개발자가 해당 코드를 수정해서 호출하지 않았다면?
 *  - 큰 장애가 발생했을 것이다. 처음부터 @Before 를 사용했다면 이런 문제 자체가 발생하지 않는다.
 *  - 제약 덕분에 역할이 명확해진다. 다른 개발자도 이 코드를 보고 고민해야 하는 범위가 줄어들고 코드의 의도도 파악하기 쉽다.
 */