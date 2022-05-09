package hello.aop.internalcall;

import hello.aop.internalcall.aop.CallLogAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Import(CallLogAspect.class)
@SpringBootTest
class CallServiceV0Test {

    @Autowired CallServiceV0 callServiceV0;

    /**
     * 1. 외부에서 external() 호출 + 내부에서 internal() 호출 -> 문제
     * 실행 결과를 보면 callServiceV0.external() 을 실행할 때는 프록시를 호출한다. 따라서 CallLogAspect 어드바이스가 호출된 것을 확인할 수 있다.
     * 그리고 AOP Proxy는 target.external() 을 호출한다.
     *
     * 그런데 여기서 문제는 callServiceV0.external() 안에서 internal() 을 호출할 때 발생한다. 이때는 CallLogAspect 어드바이스가 호출되지 않는다.
     * 자바 언어에서 메서드 앞에 별도의 참조가 없으면 this 라는 뜻으로 자기 자신의 인스턴스를 가리킨다.
     * 결과적으로 자기 자신의 내부 메서드를 호출하는 this.internal() 이 되는데, 여기서 this 는 실제 대상 객체(target)의 인스턴스를 뜻한다.
     * 결과적으로 이러한 내부 호출은 프록시를 거치지 않는다. 따라서 어드바이스도 적용할 수 없다.
     *
     * 프록시 방식의 AOP 한계
     * 스프링은 프록시 방식의 AOP를 사용한다. 프록시 방식의 AOP는 메서드 내부 호출에 프록시를 적용할 수 없다.
     * (물론 이 문제를 해결하는 방법이 여러개 있긴 함!)
     */
    @Test
    void external() {
        log.info("target={}", callServiceV0.getClass());
        callServiceV0.external();
    }

    /**
     * 2. 외부에서 internal() 호출
     */
    @Test
    void internal() {
        callServiceV0.internal();
    }
    

}