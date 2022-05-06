package hello.aop.exam;


import hello.aop.exam.aop.RetryAspect;
import hello.aop.exam.aop.TraceAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * 참고
 * 스프링이 제공하는 @Transactional 은 가장 대표적인 AOP이다.
 */
@Slf4j
//@Import(TraceAspect.class)
@Import({TraceAspect.class, RetryAspect.class})
@SpringBootTest
public class ExamTest {

    @Autowired ExamService examService;

    /**
     * @Trace: 실행해보면 @Trace 가 붙은 request() , save() 호출시 로그가 잘 남는 것을 확인할 수 있다. (단, 에러처리로 테스트가 실패한다)
     * @Retry: 실행 결과를 보면 5번째 문제가 발생했을 때 재시도 덕분에 문제가 복구되고, 정상 응답되는 것을 확인할 수 있다.
     */
    @Test
    void test() {
        for (int i = 0; i < 5; i++) {
            log.info("client request i={}", i);
            examService.request("data" + i);
        }
    }

}
