package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgsTest {

    Method helloMethod;

    @BeforeEach
    void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    private AspectJExpressionPointcut pointcut(String expression) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        return pointcut;
    }


    /**
     * 1. args - execution에서 파라미터 매칭 부분만 가져와서 매칭하는 거임
     * args : 인자가 주어진 타입의 인스턴스인 조인 포인트로 매칭. 기본 문법은 execution 의 args 부분과 같다.
     *
     * execution과 args의 차이점
     *  - execution 은 파라미터 타입이 정확하게 매칭되어야 한다. execution 은 클래스에 선언된 정보를 기반으로 판단한다.
     *  - args 는 부모 타입을 허용한다. args 는 실제 넘어온 파라미터 객체 인스턴스를 보고 판단한다.
     */
    @Test
    void args() {
        //hello(String)과 매칭
        assertThat(pointcut("args(String)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("args(Object)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("args()").matches(helloMethod, MemberServiceImpl.class)).isFalse();
        assertThat(pointcut("args(..)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("args(*)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("args(String, ..)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }


    /**
     * 2. args Vs. execution
     * execution(* *(java.io.Serializable)): 메서드의 시그니처(정해둔 것으로)로 판단 (정적)
     * args(java.io.Serializable): 런타임에 전달된 실제 인수로 판단 (동적)
     *
     * pointcut() : AspectJExpressionPointcut 에 포인트컷은 한번만 지정할 수 있다.
     * 이번 테스트에서는 테스트를 편리하게 진행하기 위해 포인트컷을 여러번 지정하기 위해 포인트컷 자체를 생성하는 메서드를 만들었다.
     * 자바가 기본으로 제공하는 String 은 Object , java.io.Serializable 의 하위 타입이다.
     * 정적으로 클래스에 선언된 정보만 보고 판단하는 execution(* *(Object)) 는 매칭에 실패한다.
     * 동적으로 실제 파라미터로 넘어온 객체 인스턴스로 판단하는 args(Object) 는 매칭에 성공한다. (부모 타입 허용)
     *
     * ※ 참고
     *  - args 지시자는 단독으로 사용되기 보다는 뒤에서 설명할 파라미터 바인딩에서 주로 사용된다.
     */
    @Test
    void argsVsExecution() {
        //Args - 해당 파라미터의 상위 타입도 허용
        assertThat(pointcut("args(String)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("args(java.io.Serializable)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("args(Object)").matches(helloMethod, MemberServiceImpl.class)).isTrue();

        //Execution - 정확한 해당 파라미터 타입이어야 함
        assertThat(pointcut("execution(* *(String))").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        assertThat(pointcut("execution(* *(java.io.Serializable))").matches(helloMethod, MemberServiceImpl.class)).isFalse();
        assertThat(pointcut("execution(* *(Object))").matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }
}
