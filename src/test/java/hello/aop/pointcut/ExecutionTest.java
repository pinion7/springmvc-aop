package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 형태: execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)
 * 예시: execution(public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String) Exception)
 */
@Slf4j
public class ExecutionTest {

    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }


    /** 1. 메서드 정보 출력 */
    @Test
    @DisplayName("1. 메서드 정보 출력")
    void printMethod() {
        log.info("helloMethod={}", helloMethod);
    }


    /** 2. 가장 정확한 포인트컷
     * 매칭 조건
     *  - 접근제어자?: public
     *  - 반환타입: String
     *  - 선언타입?: hello.aop.member.MemberServiceImpl
     *  - 메서드이름: hello
     *  - 파라미터: (String)
     *  - 예외?: 생략
     * */
    @Test
    @DisplayName("2. 가장 정확한 포인트컷")
    void exactMatch() {
        pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }


    /** 3. 가장 많이 생략한 포인트컷
     * *은 아무 값이 들어와도 된다는 뜻이다.
     * 파라미터에서 .. 은 파라미터의 타입과 파라미터 수가 상관없다는 뜻이다.
     * ( 0..* ) 파라미터는 뒤에 자세히 정리하겠다.
     *
     * 매칭 조건
     *  - 접근제어자?: 생략
     *  - 반환타입: *
     *  - 선언타입?: 생략
     *  - 메서드이름: *
     *  - 파라미터: (..)
     *  - 예외?: 없음
     */
    @Test
    @DisplayName("3. 가장 많이 생략한 포인트컷")
    void allMatch() {
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }


    /**
     * 4. 메서드 이름 매칭 관련 포인트컷
     * 메서드 이름 앞뒤에 *을 사용해서 매칭할 수 있다.
     */
    @Test
    @DisplayName("4-1. 메서드 이름 매칭 관련 포인트컷")
    void nameMatch() {
        pointcut.setExpression("execution(* hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("4-2. 메서드 이름 매칭 관련 포인트컷")
    void nameMatchStar1() {
        pointcut.setExpression("execution(* hel*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("4-3. 메서드 이름 매칭 관련 포인트컷")
    void nameMatchStar2() {
        pointcut.setExpression("execution(* *el*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("4-4. 메서드 이름 매칭 관련 포인트컷")
    void nameMatchFalse() {
        pointcut.setExpression("execution(* nono(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }


    /**
     * 5. 패키지 매칭 관련 포인트컷
     *
     * hello.aop.member.*(1).*(2)
       (1): 타입
       (2): 메서드 이름
     * 패키지에서 . , .. 의 차이를 이해해야 한다.
       . : 정확하게 해당 위치의 패키지
       .. : 해당 위치의 패키지와 그 하위 패키지도 포함
     */
    @Test
    @DisplayName("5-1. 패키지 매칭 관련 포인트컷 - 전부 기입")
    void packageExactMatch1() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("5-2. 패키지 매칭 관련 포인트컷 - 클래스랑 메서드에 와일드카드")
    void packageExactMatch2() {
        pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("5-3. 패키지 매칭 관련 포인트컷 - 패키지명 누락")
    void packageExactFalse() {
        pointcut.setExpression("execution(* hello.aop.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    @DisplayName("5-4. 패키지 매칭 관련 포인트컷 - 서브 패키지1")
    void packageMatchSubPackage1() {
        pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("5-5. 패키지 매칭 관련 포인트컷 - 서브 패키지2")
    void packageMatchSubPackage2() {
        pointcut.setExpression("execution(* hello.aop..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }


    /**
     * 6. 타입 매칭 - 부모 타입 허용
     *
     * typeExactMatch() 는 타입 정보가 정확하게 일치하기 때문에 매칭된다.
     * typeMatchSuperType() 을 주의해서 보아야 한다.
     * execution 에서는 MemberService 처럼 부모 타입을 선언해도 그 자식 타입은 매칭된다.
     * 다형성에서 부모타입 = 자식타입 이 할당 가능하다는 점을 떠올려보면 된다.
     */
    @Test
    @DisplayName("6-1. 타입 매칭 - 자기 자신 타입 허용")
    void typeExactMatch() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("6-2. 타입 매칭 - 부모 타입 허용")
    void typeMatchSuperType() {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }


    /**
     * 7. 타입 매칭 - 부모 타입에 있는 메서드만 허용
     *
     * typeMatchInternal() 의 경우 MemberServiceImpl 를 표현식에 선언했기 때문에 그 안에 있는 internal(String) 메서드도 매칭 대상이 된다.
     * typeMatchNoSuperTypeMethodFalse() 를 주의해서 보아야 한다.
     * 이 경우 표현식에 부모 타입인 MemberService 를 선언했다.
     * 그런데 자식 타입인 MemberServiceImpl 의 internal(String) 메서드를 매칭하려 한다.
     * 이 경우 매칭에 실패한다. MemberService 에는 internal(String) 메서드가 없다!
     * 부모 타입을 표현식에 선언한 경우 부모 타입에서 선언한 메서드가 자식 타입에 있어야 매칭에 성공한다.
     * 그래서 부모 타입에 있는 hello(String) 메서드는 매칭에 성공하지만, 부모 타입에 없는 internal(String) 는 매칭에 실패한다.
     */
    @Test
    @DisplayName("7-1. 타입 매칭 - MemberServiceImpl에 속하는 자신의 메서드는 당연 매칭 가능")
    void typeMatchInternal() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
    }

    // 포인트컷으로 지정한 MemberService 는 internal 이라는 이름의 메서드가 없다.
    @Test
    @DisplayName("7-2. 타입 매칭 - MemberService는 impl의 부모 타입이기 때문에, 자식타입의 메서드 매칭 불가")
    void typeMatchSuperTypeMethodFalse() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
    }


    /**
     * 8. 파라미터 매칭
     *
     * execution 파라미터 매칭 규칙은 다음과 같다.
     *  (String) : 정확하게 String 타입 파라미터
     *  () : 파라미터가 없어야 한다.
     *  (*) : 정확히 하나의 파라미터, 단 모든 타입을 허용한다.
     *  (*, *) : 정확히 두 개의 파라미터, 단 모든 타입을 허용한다.
     *  (..) : 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다. 참고로 파라미터가 없어도 된다. 0..* 로 이해하면 된다.
     *  (String, ..) : String 타입으로 시작해야 한다. 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다.
     *      - 예) (String) , (String, Xxx) , (String, Xxx, Xxx) 허용
     */
    @Test
    @DisplayName("8-1. 파라미터 매칭")
    void argsMatch() {
        // String 타입의 파라미터 허용
        // (String)
        pointcut.setExpression("execution(* *(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("8-2. 파라미터 매칭")
    void argsMatchNoArgs() {
        // 파라미터가 없어야 함
        // ()
        pointcut.setExpression("execution(* *())");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    @DisplayName("8-3. 파라미터 매칭")
    void argsMatchStar() {
        // 정확히 하나의 파라미터 허용, 모든 타입 허용
        // (Xxx)
        pointcut.setExpression("execution(* *(*))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("8-4. 파라미터 매칭")
    void argsMatchAll() {
        // 숫자와 무관하게 모든 파라미터, 모든 타입 허용
        // 파라미터가 없어도 됨
        // (), (Xxx), (Xxx, Xxx)
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("8-5 파라미터 매칭")
    void argsMatchComplex() {
        // String 타입으로 시작, 숫자와 무관하게 모든 파라미터, 모든 타입 허용
        // (String), (String, Xxx), (String, Xxx, Xxx) 허용
        pointcut.setExpression("execution(* *(String, ..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
}
