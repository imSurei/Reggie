package com.sure.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.sure.reggie.common.BaseContext;
import com.sure.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 檢查用戶是否完成登陸
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        // 判斷本次請求是否需要放行
        boolean checked = checkPath(urls, requestURI);
        if (checked) {
            filterChain.doFilter(request, response);
            return;
        }

        // 本次請求需要攔截處理，判斷用戶登陸狀態
        if (request.getSession().getAttribute("employee") != null) {
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
        } else if (request.getSession().getAttribute("user") != null) {
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
        } else { // 未登陸，通過輸出流方式向客戶端頁面響應數據
            response.getWriter().write(JSON.toJSONString(Result.error("NOT LOGIN")));
        }
    }

    /**
     * 路徑匹配，檢查本次請求是否需要放行
     *
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean checkPath(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) return true;
        }
        return false;
    }
}
