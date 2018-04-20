package io.corbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class TodosGatewayApp {

	public static void main(String[] args) {
		SpringApplication.run(TodosGatewayApp.class, args);
	}
}
