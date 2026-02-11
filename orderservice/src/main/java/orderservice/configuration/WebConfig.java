package orderservice.configuration;

import orderservice.log.LogRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LogRequest logRequest;
    @Lazy
    public WebConfig(LogRequest logRequest) {
        this.logRequest = logRequest;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logRequest).addPathPatterns("/order/**");
    }
}