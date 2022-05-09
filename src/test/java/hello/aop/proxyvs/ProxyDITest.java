package hello.aop.proxyvs;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import hello.aop.proxyvs.code.ProxyDIAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * 코드 설명
 * 1) @SpringBootTest : 내부에 컴포넌트 스캔을 포함하고 있다.
 *  - MemberServiceImpl 에 @Component 가 붙어있으므로 스프링 빈 등록 대상이 된다.
 *  - properties = {"spring.aop.proxy-target-class=false"} : application.properties 에 설정하는 대신에 해당 테스트에서만 설정을 임시로 적용한다.
 *  - 이렇게 하면 각 테스트마다 다른 설정을 손쉽게 적용할 수 있다.
 *  - spring.aop.proxy-target-class=false : 스프링이 AOP 프록시를 생성할 때 JDK 동적 프록시를 우선 생성한다. 물론 인터페이스가 없다면 CGLIB를 사용한다.
 *
 * 2) @Import(ProxyDIAspect.class) : 앞서 만든 Aspect를 스프링 빈으로 등록한다.
 *
 * 3)
 */
@Slf4j
//@SpringBootTest(properties = {"spring.aop.proxy-target-class=false"}) // JDK 동적 프록시 적용하는 옵션
//@SpringBootTest(properties = {"spring.aop.proxy-target-class=true"}) // CGLIB 프록시, 성공
@SpringBootTest
@Import(ProxyDIAspect.class)
public class ProxyDITest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberServiceImpl memberServiceImpl;

    /**
     * 1. JDK 동적 프록시에 구체 클래스 타입 주입
     *  - JDK 동적 프록시에 구체 클래스 타입을 주입할 때 어떤 문제가 발생하는지 지금부터 확인해보자.
     *  1) 실행
     *  - 먼저 spring.aop.proxy-target-class=false 설정을 사용해서 스프링 AOP가 JDK 동적 프록시를 사용하도록 했다.
     *  - 이렇게 실행하면 다음과 같이 오류(아래 실행 결과)가 발생한다.
     *
     *  2) 실행 결과
     *   BeanNotOfRequiredTypeException: Bean named 'memberServiceImpl' is expected to
     *   be of type 'hello.aop.member.MemberServiceImpl' but was actually of type 'com.sun.proxy.$Proxy54'
     *  -> 결과 해석: 타입과 관련된 예외가 발생한 것이다.
     *      - 자세히 읽어보면 memberServiceImpl 에 주입되길 기대하는 타입은 hello.aop.member.MemberServiceImpl 이지만,
     *      - 실제 넘어온 타입은 com.sun.proxy.$Proxy54 이다. 따라서 타입 예외가 발생한다고 한다.
     *
     *  3) @Autowired MemberService memberService : 이 부분은 문제가 없다.
     *  - JDK Proxy는 MemberService 인터페이스를 기반으로 만들어진다. 따라서 해당 타입으로 캐스팅 할 수 있다.
     *  - MemberService = JDK Proxy 가 성립한다.
     *
     *  4) @Autowired MemberServiceImpl memberServiceImpl : 문제는 여기다.
     *  - JDK Proxy는 MemberService 인터페이스를 기반으로 만들어진다.
     *  - 따라서 MemberServiceImpl 타입이 뭔지 전혀 모른다. 그래서 해당 타입에 주입할 수 없다.
     *  - MemberServiceImpl = JDK Proxy 가 성립하지 않는다
     *
     *
     * 2. CGLIB 프록시에 구체 클래스 타입 주입
     *  - 이번에는 JDK 동적 프록시 대신에 CGLIB를 사용해서 프록시를 적용해보자.
     *  1) 실행
     *  - 우선 다음과 같이 옵션을 반대로 걸어보자.
     *  - @SpringBootTest(properties = {"spring.aop.proxy-target-class=true"}) // CGLIB 프록시, 성공
     *
     *  2) 실행 결과
     *  - 실행해보면 정상 동작하는 것을 확인할 수 있다.
     *
     *  3) @Autowired MemberService memberService :
     *  - CGLIB Proxy는 MemberServiceImpl 구체 클래스를 기반으로 만들어진다.
     *  - MemberServiceImpl 은 MemberService 인터페이스를 구현했기 때문에 해당 타입으로 캐스팅 할 수 있다.
     *  - MemberService = CGLIB Proxy 가 성립한다.
     *
     *  4) @Autowired MemberServiceImpl memberServiceImpl :
     *  - CGLIB Proxy는 MemberServiceImpl 구체 클래스를 기반으로 만들어진다. 따라서 해당 타입으로 캐스팅 할 수 있다.
     *  - MemberServiceImpl = CGLIB Proxy 가 성립한다.
     */
    @Test
    void go() {
        log.info("memberService class={}", memberService.getClass());
        log.info("memberServiceImpl class={}", memberServiceImpl.getClass());
        memberServiceImpl.hello("hello");
    }

}

/**
 * 1. 프록시 기술과 한계 - 타입 캐스팅: 총정리
 *  - JDK 동적 프록시는 대상 객체인 MemberServiceImpl 타입에 의존관계를 주입할 수 없다.
 *  - CGLIB 프록시는 대상 객체인 MemberServiceImpl 타입에 의존관계 주입을 할 수 있다.
 *  - 지금까지 JDK 동적 프록시가 가지는 한계점을 알아보았다. 실제로 개발할 때는 인터페이스가 있으면 인터페이스를 기반으로 의존관계 주입을 받는 것이 맞다.
 *  - DI의 장점이 무엇인가? DI 받는 클라이언트 코드의 변경 없이 구현 클래스를 변경할 수 있는 것이다.
 *  - 이렇게 하려면 인터페이스를 기반으로 의존관계를 주입 받아야 한다.
 *  - MemberServiceImpl 타입으로 의존관계 주입을 받는 것처럼 구현 클래스에 의존관계를 주입하면,
 *  - 향후 구현 클래스를 변경할 때 의존관계 주입을 받는 클라이언트의 코드도 함께 변경해야 한다.
 *  - 따라서 올바르게 잘 설계된 애플리케이션이라면 이런 문제가 자주 발생하지는 않는다.
 *  - 그럼에도 불구하고 테스트, 또는 여러가지 이유로 AOP 프록시가 적용된 구체 클래스를 직접 의존관계 주입 받아야 하는 경우가 있을 수 있다.
 *  - 이때는 CGLIB를 통해 구체 클래스 기반으로 AOP 프록시를 적용하면 된다.
 *
 *  - 여기까지 듣고보면 CGLIB를 사용하는 것이 좋아보인다. CGLIB를 사용하면 사실 이런 고민 자체를 하지 않아도 된다.
 *  - 2번째 챕터에서 CGLIB의 단점을 알아보자.
 */


/**
 * 2. 프록시 기술과 한계 - CGLIB
 *  - 스프링에서 CGLIB는 구체 클래스를 상속 받아서 AOP 프록시를 생성할 때 사용한다.
 *  - CGLIB는 구체 클래스를 상속 받기 때문에 다음과 같은 문제가 있다.
 *  - CGLIB 구체 클래스 기반 프록시 문제점
 *      - 대상 클래스에 기본 생성자 필수
 *      - 생성자 2번 호출 문제
 *      - final 키워드 클래스, 메서드 사용 불가
 *      - 아래에서 하나씩 자세히 알아보자.
 *
 * 1) 대상 클래스에 기본 생성자 필수
 *  - CGLIB는 구체 클래스를 상속 받는다.
 *  - 자바 언어에서 상속을 받으면 자식 클래스의 생성자를 호출할 때 자식 클래스의 생성자에서 부모 클래스의 생성자도 호출해야 한다.
 *  (이 부분이 생략되어 있다면 자식 클래스의 생성자 첫줄에 부모 클래스의 기본 생성자를 호출하는 super() 가 자동으로 들어간다.) 이 부분은 자바 문법 규약이다.
 *  - CGLIB를 사용할 때 CGLIB가 만드는 프록시의 생성자는 우리가 호출하는 것이 아니다.
 *  - CGLIB 프록시는 대상 클래스를 상속 받고, 생성자에서 대상 클래스의 기본 생성자를 호출한다.
 *  - 따라서 대상 클래스에 기본 생성자를 만들어야 한다. (기본 생성자는 파라미터가 하나도 없는 생성자를 뜻한다. 생성자가 하나도 없으면 자동으로 만들어진다.)
 *
 * 2) 생성자 2번 호출 문제
 *  - CGLIB는 구체 클래스를 상속 받는다. 자바 언어에서 상속을 받으면 자식 클래스의 생성자를 호출할 때 부모 클래스의 생성자도 호출해야 한다. 그런데 왜 2번일까?
 *  (1) 실제 target의 객체를 생성할 때
 *  (2) 프록시 객체를 생성할 때 부모 클래스의 생성자 호출
 *
 * 3) final 키워드 클래스, 메서드 사용 불가
 *  - final 키워드가 클래스에 있으면 상속이 불가능하고, 메서드에 있으면 오버라이딩이 불가능하다.
 *  - CGLIB는 상속을 기반으로 하기 때문에 두 경우 프록시가 생성되지 않거나 정상 동작하지 않는다.
 *  - 프레임워크 같은 개발이 아니라 일반적인 웹 애플리케이션을 개발할 때는 final 키워드를 잘 사용하지 않는다.
 *  - 따라서 이 부분이 특별히 문제가 되지는 않는다.
 *
 * 4) 정리
 *  - JDK 동적 프록시는 대상 클래스 타입으로 주입할 때 문제가 있고,
 *  - CGLIB는 대상 클래스에 기본 생성자 필수, 생성자 2번 호출 문제가 있다.
 *  - 그렇다면 스프링은 어떤 방법을 권장할까? 3번째 챕터에서 이를 살펴보자.
 */


/**
 * 3. 프록시 기술과 한계 - 스프링의 해결책
 *  - 스프링은 AOP 프록시 생성을 편리하게 제공하기 위해 오랜 시간 고민하고 문제들을 해결해왔다.
 *
 * 1) 스프링의 기술 선택 변화
 *  - 스프링 3.2, CGLIB를 스프링 내부에 함께 패키징
 *  - 과거 CGLIB를 사용하려면 CGLIB 라이브러리가 별도로 필요했다.
 *  - 현재 스프링은 CGLIB 라이브러리를 스프링 내부에 함께 패키징해서 별도의 라이브러리 추가 없이 CGLIB를 사용할 수 있게 되었다.
 *  - CGLIB spring-core org.springframework
 *
 * 2) CGLIB 기본 생성자 필수 문제 해결
 *  - 스프링 4.0부터 CGLIB의 기본 생성자가 필수인 문제가 해결되었다.
 *  - objenesis 라는 특별한 라이브러리를 사용해서 기본 생성자 없이 객체 생성이 가능하다.
 *  - 참고로 이 라이브러리는 생성자 호출 없이 객체를 생성할 수 있게 해준다.
 *
 * 3) 생성자 2번 호출 문제
 *  - 스프링 4.0부터 CGLIB의 생성자 2번 호출 문제가 해결되었다.
 *  - 이것도 역시 objenesis 라는 특별한 라이브러리 덕분에 가능해졌다.
 *  - 이제 생성자가 1번만 호출된다.
 *
 * 4) 스프링 부트 2.0 - CGLIB 기본 사용
 *  - 스프링 부트 2.0 버전부터 CGLIB를 기본으로 사용하도록 했다.
 *  - 이렇게 해서 구체 클래스 타입으로 의존관계를 주입하는 문제를 해결했다.
 *  - 스프링 부트는 별도의 설정이 없다면 AOP를 적용할 때 기본적으로 proxyTargetClass=true 로 설정해서 사용한다.
 *  - 따라서 인터페이스가 있어도 JDK 동적 프록시를 사용하는 것이 아니라 항상 CGLIB를 사용해서 구체클래스를 기반으로 프록시를 생성한다.
 *  - 물론 스프링은 우리에게 선택권을 열어주기 때문에 아래와 깉이 설정하면 JDK 동적 프록시도 사용할 수 있다.
 *  - application.properties 파일 내에서 spring.aop.proxy-target-class=false 로 설정!
 *
 * 5) 정리
 *  - 스프링은 최종적으로 스프링 부트 2.0에서 CGLIB를 기본으로 사용하도록 결정했다.
 *  - CGLIB를 사용하면 JDK 동적 프록시에서 동작하지 않는 구체 클래스 주입이 가능하다.
 *  - 여기에 추가로 CGLIB의 단점들이 이제는 많이 해결되었다.
 *  - CGLIB의 남은 문제라면 final 클래스나 final 메서드가 있는데,
 *  - AOP를 적용할 대상에는 final 클래스나 final 메서드를 잘 사용하지는 않으므로 이 부분은 크게 문제가 되지는 않는다.
 *  - 개발자 입장에서 보면 사실 어떤 프록시 기술을 사용하든 상관이 없다. JDK 동적 프록시든 CGLIB든 또는 어떤 새로운 프록시 기술을 사용해도 된다.
 *  - 심지어 클라이언트 입장에서 어떤 프록시 기술을 사용하는지 모르고 잘 동작하는 것이 가장 좋다. 단지 문제 없고, 개발하기에 편리하면 되는 것이다.
 *
 * 6) 마지막으로 ProxyDITest 를 다음과 같이 변경해서 아무런 설정 없이 실행해보면 CGLIB가 기본으로 사용되는 것을 확인할 수 있다.
 *  - 단, @SpringBootTest를 옵션없이 추가하고, application.properties 에 spring.aop.proxy-target-class 관련 설정이 없어야 한다.
 *
 *  - 실행
 *  @Slf4j
 *  //@SpringBootTest(properties = {"spring.aop.proxy-target-class=false"}) //JDK 동적 프록시, DI 예외 발생
 *  //@SpringBootTest(properties = {"spring.aop.proxy-target-class=true"}) //CGLIB 프록시, 성공
 *  @SpringBootTest // 위에 두개 주석 처리하고 이걸 추가
 *  @Import(ProxyDIAspect.class)
 *  public class ProxyDITest {...}
 *
 *  - 실행 결과
 *  memberService class=class hello.aop.member.MemberServiceImpl$
 *  $EnhancerBySpringCGLIB$$83e257b3
 *  memberServiceImpl class=class hello.aop.member.MemberServiceImpl$
 *  $EnhancerBySpringCGLIB$$83e257b3
 */