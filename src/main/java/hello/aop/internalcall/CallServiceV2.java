package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallServiceV2 {

    // private final ApplicationContext applicationContext;
//    public CallServiceV2(ApplicationContext applicationContext) {
//        this.applicationContext = applicationContext;
//    }

    /**
     * 앞서 생성자 주입이 실패하는 이유는 자기 자신을 생성하면서 주입해야 하기 때문이다.
     * 이 경우 수정자 주입을 사용하거나, 아니면 V2버전처럼 지연 조회를 사용하면 된다.
     * 스프링 빈을 지연해서 조회하면 되는데, ObjectProvider(Provider) , ApplicationContext 를 사용하면 된다.
     * 단, 위에 주석처리한 ApplicationContext의 경우는 너무 많은 기능을 제공하니, 되도록 ObjectProvider(Provider)를 사용하는 게 좋다.
     */
    private final ObjectProvider<CallServiceV2> callServiceProvider;

    /**
     * ObjectProvider는 객체를 스프링 컨테이너에서 조회하는 것을 스프링 빈 생성 시점이 아니라, 실제 객체를 사용하는 시점으로 지연할 수 있다.
     * callServiceProvider.getObject() 를 호출하는 시점에 스프링 컨테이너에서 빈을 조회한다.
     * 여기서는 자기 자신을 주입 받는 것이 아니기 때문에 순환 사이클이 발생하지 않는다.
     */
    public CallServiceV2(ObjectProvider<CallServiceV2> callServiceProvider) {
        this.callServiceProvider = callServiceProvider;
    }

    public void external() {
        log.info("call external");
//        CallServiceV2 callServiceV2 = applicationContext.getBean(CallServiceV2.class);
        CallServiceV2 callServiceV2 = callServiceProvider.getObject();
        callServiceV2.internal(); // 외부 메서드 호출
    }

    public void internal() {
        log.info("call internal");
    }
}
