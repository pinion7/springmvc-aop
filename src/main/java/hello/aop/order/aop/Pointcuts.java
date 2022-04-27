package hello.aop.order.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    // 포인트컷과 어드바이스를 분리할 수도 있음.
    @Pointcut("execution(* hello.aop.order..*(..))")
    public void allOrder() {} // 포인트컷 시그니쳐

    // 클래스 이름 패턴이 *Service인거에 적용하는 것
    @Pointcut("execution(* *..*Service.*(..))")
    public void allService() {}

    @Pointcut("allOrder() && allService()")
    public void orderAndService() {}
}
