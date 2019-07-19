package top.xuyx.seafood.interceptor;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.xuyx.seafood.common.Response;
import top.xuyx.seafood.common.TokenHelper;
import top.xuyx.seafood.common.enums.StatusEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 验证token
 */
@Component
public class BackTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenHelper tokenHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
        response.setCharacterEncoding("utf-8");
        String name = request.getHeader("name");
        String token = request.getHeader("Authorization");

        if (StringUtils.isAnyBlank(name, token)) {
            response.getWriter().write(JSON.toJSONString(Response.fail(StatusEnum.code_301)));
            return false;
        }

        try {
            String userName = tokenHelper.getSubject(token);
            if (!name.equals(userName)) {
                response.getWriter()
                        .write(JSON.toJSONString(Response.fail(StatusEnum.code_301)));
                return false;
            }
        } catch (Exception e) {
            response.getWriter()
                    .write(JSON.toJSONString(Response.fail(StatusEnum.code_301)));
            return false;
        }
        return true;
    }
}
