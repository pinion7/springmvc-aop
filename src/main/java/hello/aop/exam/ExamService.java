package hello.aop.exam;

import hello.aop.exam.annotation.Trace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;

    @Trace // 제작한 로그출력용 AOP 삽입: 이제 메서드 호출 정보를 AOP를 사용해서 로그로 남길 수 있다.
    public void request(String itemId) {
        examRepository.save(itemId);
    }
}
