package top.xuyx.seafood.common;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
@Slf4j
public class AppRequestAspect {


    /**
     *拦截controller下所有方法
     * @return void
     */
    @Pointcut("execution(* top.xuyx.seafood.controller.*.*(..))")
    public void pointcut() {

    }

    /**
     * 请求数据
     * @return void
     */
    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();
        //先判断请求路径上是否有参数，因为post请求时参数也有可能在路径上
        String param = request.getQueryString();

        if (HttpMethod.POST.name().equals(method)) {
            //post请求，并且参数在body中。此时参数的实体类要重写toString方法
            Object[] paramsArray = joinPoint.getArgs();
            for(Object object : paramsArray) {
            	if(!(object instanceof HttpServletRequest || object instanceof HttpServletResponse)) {
            		//不是request对象和response对象
            		param = JSON.toJSONString(object);
            	}
            }
        }
        log.info(
                "请求地址：{}, 参数：{}",
                uri,
                param);
    }

    /**
     * @Author panhongqiang
     * @Description  响应数据
     * @Date 2018/7/24 10:40
     * @Param [result]
     * @return void
     */
    @AfterReturning(returning = "result", pointcut = "pointcut()")
    public void doAfterReturning(Object result) {
        String data = JSON.toJSONString(result);
        log.info("响应数据：" + data);
    }

}
