package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class WithinTest {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method hellMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        hellMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    /**
     * 1. within
     * within 지시자는 특정 타입 내의 조인 포인트에 대한 매칭을 제한한다.
     * 쉽게 이야기해서 해당 타입이 매칭되면 그 안의 메서드(조인 포인트)들이 전부 자동으로 매칭된다.
     * 문법은 단순한데 execution 에서 타입 부분만 사용한다고 보면 된다.
     */
    @Test
    @DisplayName("1-1. within 기본 테스트")
    void withinExact() {
        pointcut.setExpression("within(hello.aop.member.MemberServiceImpl)");
        assertThat(pointcut.matches(hellMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("1-2. within 기본 테스트: *도 가능")
    void withinStar() {
        pointcut.setExpression("within(hello.aop.member.*Service*)");
        assertThat(pointcut.matches(hellMethod, MemberServiceImpl.class)).isTrue();
    }


    @Test
    @DisplayName("1-3 within 기본 테스트: 서브패키지")
    void withinSubPackage() {
        pointcut.setExpression("within(hello.aop..*)");
        assertThat(pointcut.matches(hellMethod, MemberServiceImpl.class)).isTrue();
    }


    /**
     * 2. withinTest - 추가
     * 주의
     *  - 그런데 within 사용시 주의해야 할 점이 있다. 표현식에 부모 타입을 지정하면 안된다는 점이다.
     *  - 정확하게 타입이 맞아야 한다. 이 부분에서 execution 과 차이가 난다.
     */
    @Test
    @DisplayName("2-1. 타켓의 타입에만 직접 적용, 인터페이스를 선정하면 안된다.")
    void withinSuperTypeFalse() {
        pointcut.setExpression("within(hello.aop.member.MemberService)");
        assertThat(pointcut.matches(hellMethod, MemberServiceImpl.class)).isFalse();
    }


    // 부모 타입(여기서는 MemberService 인터페이스) 지정시 within 은 실패하고, execution 은 성공하는 것을 확인할 수 있다.
    @Test
    @DisplayName("2-2. execution은 타입 기반, 인터페이스를 선정 가능.")
    void executionSuperTypeTrue() {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(hellMethod, MemberServiceImpl.class)).isTrue();

    }
}
