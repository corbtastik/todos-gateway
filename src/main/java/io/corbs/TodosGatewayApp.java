package io.corbs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@EnableZuulProxy
@EnableFeignClients
@SpringBootApplication
public class TodosGatewayApp {


    @Bean
    ApiPreFilter apiPreFilter() {
        return new ApiPreFilter();
    }

    @Bean
    DefaultModePreFilter defaultModePreFilter(@Autowired Environment env) {
        return new DefaultModePreFilter(env);
    }

    @Bean
    CqrsModePreFilter cqrsModePreFilter(@Autowired Environment env) {
        return new CqrsModePreFilter(env);
    }

    @Bean
    ApiPostFilter apiPostFilter() {
        return new ApiPostFilter();
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

	public static void main(String[] args) {
		SpringApplication.run(TodosGatewayApp.class, args);
	}
}
