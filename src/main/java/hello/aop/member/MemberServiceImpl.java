package hello.aop.member;

import hello.aop.member.annotation.ClassAop;
import hello.aop.member.annotation.MemberAop;
import org.springframework.stereotype.Component;

@ClassAop
@Component
public class MemberServiceImpl implements MemberService {

    @Override
    @MemberAop("test value")
    public String hello(String param) {
        return "ok";
    }

    public String internal(String param) {
        return "ok";
    }
}
