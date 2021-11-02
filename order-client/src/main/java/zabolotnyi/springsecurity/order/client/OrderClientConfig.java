package zabolotnyi.springsecurity.order.client;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true,jsr250Enabled = true)
public class OrderClientConfig {

    @Bean
    public OrderClient orderClient() {
        return Feign
                .builder()
                .logger(new Slf4jLogger())
                .logLevel(Logger.Level.FULL)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(OrderClient.class, "http://localhost:8080");
    }
}
