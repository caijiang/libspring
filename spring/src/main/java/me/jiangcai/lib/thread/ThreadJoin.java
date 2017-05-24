package me.jiangcai.lib.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author CJ
 */
@Aspect
public class ThreadJoin {

    private static final Log log = LogFactory.getLog(ThreadJoin.class);

    @Pointcut("@annotation(me.jiangcai.lib.thread.ThreadSafe)&&args(me.jiangcai.lib.thread.ThreadLocker,..)")
    public void threadSafe() {
    }

    @Around("threadSafe()")
    public Object aroundSave(ProceedingJoinPoint pjp) throws Throwable {
        ThreadLocker locker = (ThreadLocker) pjp.getArgs()[0];
        log.debug("prepare invoke threadSafe for locker " + locker);
        try {
            synchronized (locker.lockObject()) {
                log.debug("entering locker " + locker);
                return pjp.proceed(pjp.getArgs());
            }
        } finally {
            log.debug("releasing locker " + locker);
        }

    }

}
