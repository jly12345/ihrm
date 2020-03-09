package com.ihrm.common.interceptor;

import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Think
 */
public class JwtInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        if(!StringUtils.isEmpty(authorization) && !authorization.startsWith("Bearer")){
            throw new CommonException(ResultCode.UNAUTHENTICATED);
        }

        String token = authorization.replace("Bearer ","");
        Claims claims = jwtUtils.parseJwt(token);
        if(claims!=null){
            String apis = (String)claims.get("apis");

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequestMapping annotation = handlerMethod.getMethodAnnotation(RequestMapping.class);
            String name = annotation.name();
            //当前用户是否具有相应的请求权限
            if(apis.contains(name)){
                request.setAttribute("user_claims",claims);
                return true;
            }else {
                throw new CommonException(ResultCode.UNAUTHORISE);
            }

        }
        throw new CommonException(ResultCode.UNAUTHENTICATED);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.afterConcurrentHandlingStarted(request, response, handler);
    }
}
