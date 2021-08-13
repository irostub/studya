package com.irostub.studya.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uuid = UUID.randomUUID().toString();
        log.info("[preHandle] [{}] [{}] [{}]", uuid, request.getRequestURI(), handler);
        request.setAttribute("logUuid", uuid);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if(modelAndView != null)
            log.info("[postHandle] [{}] [{}] [{}] [{}]",request.getAttribute("logUuid"), request.getRequestURI(), modelAndView.getModel() , modelAndView.getViewName());
        else
            log.info("[postHandle] [{}] [{}]",request.getAttribute("logUuid"), request.getRequestURI());

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("[afterCompletion] [{}] [{}] [{}] [{}]", request.getAttribute("logUuid"), request.getRequestURI(), handler, ex);
    }
}
