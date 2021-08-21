package com.yh.shiro.aop;

import org.apache.shiro.ShiroException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author opensmile
 * @version 1.0
 * @date 2020/3/25 5:58 下午
 */
@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class ShiroExceptionHandler {

    @ExceptionHandler(value = ShiroException.class)
    public void handleUnauthorizedException(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher =request.getRequestDispatcher("/unauth");
        requestDispatcher.forward(request,response);
    }
}

