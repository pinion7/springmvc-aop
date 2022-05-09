package hello.aop.internalcall;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * 구조를 분리 변경 - internal()를 가지고 있는 객체를 따로 생성
 *
 * 앞선 방법들은 자기 자신을 주입하거나 또는 Provider 를 사용해야 하는 것 처럼 조금 어색한 모습을 만들었다.
 * 가장 나은 대안은 내부 호출이 발생하지 않도록 구조를 변경하는 것이다.
 * 실제 이 방법을 가장 권장한다.
 *
 * 내부 호출 자체가 사라지고, callService -> internalService 를 호출하는 구조로 변경되었다. 덕분에 자연스럽게 AOP가 적용된다.
 * 여기서 구조를 변경한다는 것은 이렇게 단순하게 분리하는 것 뿐만 아니라 다양한 방법들이 있을 수 있다.
 * 예를 들어서 다음과 같이 클라이언트에서 둘다 호출하는 것이다.
 * 클라이언트 external()
 * 클라이언트 internal()
 * 물론 이 경우 external() 에서 internal() 을 내부 호출하지 않도록 코드를 변경해야 한다.
 * 그리고 클라이언트 external() , internal() 을 모두 호출하도록 구조를 변경하면 된다. (물론 가능한 경우에 한해서)
 *
 * 참고
 *  - AOP는 주로 트랜잭션 적용이나 주요 컴포넌트의 로그 출력 기능에 사용된다.
 *  - 쉽게 이야기해서 인터페이스에 메서드가 나올 정도의 규모에 AOP를 적용하는 것이 적당하다.
 *  - 더 풀어서 이야기하면 AOP는 public 메서드에만 적용한다. private 메서드처럼 작은 단위에는 AOP를 적용하지 않는다.
 *  - AOP 적용을 위해 private 메서드를 외부 클래스로 변경하고 public 으로 변경하는 일은 거의 없다.
 *  - 그러나 위 예제와 같이 public 메서드에서 public 메서드를 내부 호출하는 경우에는 문제가 발생한다.
 *  - 실무에서 꼭 한번은 만나는 문제이다. AOP가 잘 적용되지 않으면 내부 호출을 의심해보자.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallServiceV3 {

    private final InternalService internalService;

    public void external() {
        log.info("call external");
        internalService.internal(); // internal 메서드를 분리하여 가지고 있는 외부 객체를 활용하여 internal() 호출
    }

}
