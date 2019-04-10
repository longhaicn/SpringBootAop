package com.aop.aspect;


import com.aop.bo.SysLogBO;
import com.google.gson.Gson;
import com.aop.service.SysLogService;
import com.aop.annotation.SysLogAnnotation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Aspect  // 使用@Aspect注解声明一个切面
@Component
public class SysLogAspect {
    private Logger logger = LoggerFactory.getLogger(SysLogAspect.class);

    @Autowired
    private SysLogService sysLogService;

    /**
     * 这里我们使用注解的形式
     * 当然，我们也可以通过切点表达式直接指定需要拦截的package,需要拦截的class 以及 method
     * 切点表达式:   execution(...)
     */
    @Pointcut("@annotation(com.aop.annotation.SysLogAnnotation)")
    public void logPointCut() {}

    /**
     * 环绕通知 @Around  ， 当然也可以使用 @Before (前置通知)  @After (后置通知)
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        try {
            long beginTime = System.currentTimeMillis();
            logger.info("【注解：Around . 环绕前】方法环绕start.....");
            Object result = point.proceed();
            logger.info("【注解：Around. 环绕后】方法环绕proceed，结果是 :" + result);
            long time = System.currentTimeMillis() - beginTime;
            saveLog(point, time);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    @Before("logPointCut()")
    public void before(JoinPoint point) throws Throwable{

        logger.info("【注解：Before】方法最先执行.....");

    }

    @After("logPointCut()")
    public void after(JoinPoint point) throws Throwable{

        logger.info("【注解：After】方法最后执行.....");

    }

    /**
     * 保存日志
     * @param joinPoint
     * @param time
     */
    private void saveLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLogBO sysLogBO = new SysLogBO();
        sysLogBO.setExeuTime(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        sysLogBO.setCreateDate(dateFormat.format(new Date()));
        SysLogAnnotation sysLog = method.getAnnotation(SysLogAnnotation.class);
        if(sysLog != null){
            //注解上的描述
            sysLogBO.setRemark(sysLog.value());
        }
        //请求的 类名、方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLogBO.setClassName(className);
        sysLogBO.setMethodName(methodName);
        //请求的参数
        Object[] args = joinPoint.getArgs();
        try{
            List<String> list = new ArrayList<String>();
            for (Object o : args) {
                list.add(new Gson().toJson(o));
            }
            sysLogBO.setParams(list.toString());
        }catch (Exception e){ }
        sysLogService.save(sysLogBO);
    }
}
