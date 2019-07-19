package top.xuyx.seafood.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.xuyx.seafood.interceptor.BackTokenInterceptor;
import top.xuyx.seafood.interceptor.WxInterceptor;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private BackTokenInterceptor backTokenInterceptor;

    @Autowired
    private WxInterceptor wxInterceptor;

    //    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
//                .maxAge(3600)
//                .allowCredentials(true);
//    }
    private CorsConfiguration corsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 请求常用的三种配置，*代表允许所有，当时你也可以自定义属性（比如header只能带什么，只能是post方式等等）

        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig());
        return new CorsFilter(source);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(backTokenInterceptor)
                .addPathPatterns("/back/**")
                .excludePathPatterns("/back/login");

        //addPathPatterns 用于添加拦截规则
        //excludePathPatterns 用于排除拦截
        registry.addInterceptor(wxInterceptor)
                .addPathPatterns("/wx/*")
                .excludePathPatterns("/wx/MP_verify_CgbIT6BPlst4GYlg.txt")
                .excludePathPatterns("/wx/orderNotify")
                .excludePathPatterns("/wx/QrNotify")
                .excludePathPatterns("/wx/orderPay");
    }
}