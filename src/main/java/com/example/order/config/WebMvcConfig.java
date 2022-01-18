package com.example.order.config;

import com.example.order.rceptor.UseInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public UseInterceptor vxInterceptor(){
        return new UseInterceptor();
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //添加映射路径
        registry.addMapping("/**")
                .allowedOrigins("*")
                //是否发送Cookie信息, allowedOrigins设置*,则allowCredentials不能设置true
                .allowCredentials(false)
                //放行哪些原始域(请求方式)
                .allowedMethods("GET","POST", "PUT", "DELETE")
                //放行哪些原始域(头部信息)
                .allowedHeaders("*")
                //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
                .exposedHeaders("username", "usertoken","wxapitoken","lan_ip","net_ip");
    }


    /**
     * 设置拦截的url路径
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        //设置要拦截的接口或目录
//        List listOfVerify = Arrays.asList( "/api/file/**",
//                "/api/wx/**"
//        );

        List listOfVerify = Arrays.asList( "/**"
        );

        //设置要拦截的接口
//        List listOfExc = Arrays.asList(
//                "/api/login/changeUserPwd"
//        );

        List listOfExc = Arrays.asList(
        );

        registry.addInterceptor(vxInterceptor())
                .addPathPatterns(listOfVerify)
                .excludePathPatterns(listOfExc);
        super.addInterceptors(registry);
    }
}