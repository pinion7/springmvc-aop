package hello.aop.internalcall;

import hello.aop.internalcall.aop.CallLogAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
@Import(CallLogAspect.class)
@SpringBootTest
class CallServiceV1Test {

    @Autowired CallServiceV1 callServiceV1;

    /**
     * 프록시와 내부 호출 문제 -> 대안1: 자기 자신 주입 테스트
     *
     * 실행 결과를 보면 이제는 internal() 을 호출할 때 자기 자신의 인스턴스를 호출하는 것이 아니라 프록시 인스턴스를 통해서 호출하는 것을 확인할 수 있다.
     * 당연히 AOP도 잘 적용된다.
     */
    @Test
    void external() {
        log.info("target={}", callServiceV1.getClass());
        callServiceV1.external();
    }
}