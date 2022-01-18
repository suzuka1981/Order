package com.example.order.rceptor;

import com.alibaba.fastjson.JSON;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.example.order.util.TokenUtils.verify;

/**
 * 自定义拦截器类
 */
public class UseInterceptor implements HandlerInterceptor {

    /**
     * 判断用户是否登录
     * 若用户 username 不存在，则为未登录
     * 若用户 username 存在，则判断 usertoken 是否存在
     * 若存在，则用户状态为已登录
     * 若不存在，则用户状态为登录超时
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果是 嗅探请求，则直接放行
        String methodname = request.getMethod();

        if ("OPTIONS".equals(methodname)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            //例如：/api/wx/getmsg
            String requrl = request.getRequestURI();

            if (
                    requrl.indexOf("/login") >= 0
                            || requrl.indexOf("/register") >= 0
                            || requrl.indexOf("/pswretrieval") >= 0
                            || requrl.indexOf("/upload") >= 0
                            || requrl.indexOf("/show") >= 0
                            || requrl.indexOf("/download") >= 0
                            || requrl.indexOf("/linktest") >= 0
                            || requrl.indexOf("/createlabelanduploadsave") >= 0
                            || requrl.indexOf("/labelstatusuploadsave") >= 0
            ) {
                return true;
            } else {
                try {
                    String toekn = request.getHeader("Authorization");
                    boolean b = true;

                    if (toekn != null) {
                        b = verify(toekn);
                    }

                    if (!b || toekn == null) {
                        Map<String, Object> result = new HashMap<String, Object>();

                        Map<String, String> info = new HashMap<String, String>();
                        info.put("status", "10010");

                        result.put("meta", info);

                        String json = JSON.toJSONString(result);//关键

                        returnErrorResponse(response, json);

                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response, String jsonstr) throws IOException {
        OutputStream outputStream = null;
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            outputStream = response.getOutputStream();
            outputStream.write(jsonstr.getBytes("UTF-8"));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}