package com.nageoffer.speraph.core;


import com.nageoffer.speraph.api.exception.RepeatConsumptionException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import com.nageoffer.speraph.api.annotation.Idempotent;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

/**
 * 幂等注解 AOP 拦截类
 */
@Aspect
public class IdempotentAspect {

//    @Pointcut("@annotation(com.nageoffer.speraph.api.annotation.Idempotent)")
//    public void aopPoint() {
//    }

    @Around("@annotation(idempotent)")
    public Object idempotentHandler(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        IdempotentExecuteHandler instance = IdempotentExecuteHandlerFactory.getInstance(idempotent.scene(), idempotent.type());
        Object resultObj;
        try {
            instance.execute(joinPoint, idempotent);
            resultObj = joinPoint.proceed();
            instance.postProcessing();
        } catch (RepeatConsumptionException ex) {
            /**
             * 触发幂等逻辑时可能有两种情况：
             *    * 1. 消息还在处理，但是不确定是否执行成功，那么需要返回错误，方便 RocketMQ 再次通过重试队列投递
             *    * 2. 消息处理成功了，该消息直接返回成功即可
             */
            if (!ex.getError()) {
                return null;
            }
            throw ex;
        } catch (Throwable ex) {
            // 客户端消费存在异常，需要删除幂等标识方便下次 RocketMQ 再次通过队列投递
            instance.exceptionProcessing();
            throw ex;
        } finally {
            IdempotentContext.clean();
        }
        return resultObj;
    }

    /**
     * 通过连接点 {@link ProceedingJoinPoint} 获取幂等注解
     */
    private Idempotent getIdempotent(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        return targetMethod.getAnnotation(Idempotent.class);
    }
}
