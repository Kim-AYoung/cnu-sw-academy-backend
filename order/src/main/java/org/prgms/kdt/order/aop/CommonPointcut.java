package org.prgms.kdt.order.aop;

import org.aspectj.lang.annotation.Pointcut;

public class CommonPointcut {

    @Pointcut("execution(public * org.prgms.kdt.order..*Service.*(..))")
    public void servicePublicMethodPointcut() {}

    @Pointcut("execution(* org.prgms.kdt.order..*Repository.*(..))")
    public void repositoryMethodPointcut() {}

    @Pointcut("execution(* org.prgms.kdt.order..*Repository.insert(..))")
    public void repositoryInsertMethodPointcut() {}
}
