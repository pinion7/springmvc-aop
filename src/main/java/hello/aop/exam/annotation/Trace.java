package hello.aop.exam.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 먼저 로그 출력용 AOP를 만들어보자.
 * @Trace 가 메서드에 붙어 있으면 호출 정보가 출력되는 편리한 기능이다.
 */
@Target(ElementType.METHOD) // 메서드에 걸게끔
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 살아있게끔
public @interface Trace {
}
