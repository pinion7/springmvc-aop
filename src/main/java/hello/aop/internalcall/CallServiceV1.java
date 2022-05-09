package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallServiceV1 {

    // 자기 자신을 멤버변수로 사용!
    private CallServiceV1 callServiceV1;

    // 자기 자신을 멤버변수로 사용할 때 생성자 방식은 안됨. 아직 생성되기 전인데 자기 자신을 주입하게되면서 닭이 먼저냐 달걀이 먼저냐 하는 순환참조 문제가 발생하기 때문!
    // 즉, 자기 자신을 멤버변수로 사용할거면 setter함수를 따로 만들어줘야 함!
//    @Autowired
//    public CallServiceV1(CallServiceV1 callServiceV1) {
//        this.callServiceV1 = callServiceV1;
//    }

    /**
     * setter함수 생성으로 순환 참조 해결!
     *
     * 주의
     * 스프링 부트 2.6부터는 순환 참조를 기본적으로 금지하도록 정책이 변경되었다.
     * 따라서 이번 예제를 스프링 부트 2.6 이상의 버전에서 실행하면 다음과 같은 오류 메시지가 나오면서 정상 실행되지 않는다.
     * 이 문제를 해결하려면 application.properties 에 다음을 추가해야 한다.
     * -> spring.main.allow-circular-references=true
     */
    @Autowired
    public void setCallServiceV1(CallServiceV1 callServiceV1) {
        log.info("callServiceV1 setter={}", callServiceV1.getClass()); // 이 로그를 통해 외부로부터 프록시 객체가 주입됨을 확인할 수 있음
        this.callServiceV1 = callServiceV1;
    }

    public void external() {
        log.info("call external");
        callServiceV1.internal(); // 외부 메서드 호출
    }

    public void internal() {
        log.info("call internal");
    }
}
