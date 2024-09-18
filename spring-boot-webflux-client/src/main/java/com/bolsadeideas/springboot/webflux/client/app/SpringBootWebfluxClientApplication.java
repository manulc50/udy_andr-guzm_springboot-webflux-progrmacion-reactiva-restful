package com.bolsadeideas.springboot.webflux.client.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

//Habilitamos el cliente Eureka de Netflix para que el servidor Eureka pueda descubrir y registrar este microservicio  
@EnableEurekaClient
@SpringBootApplication
public class SpringBootWebfluxClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxClientApplication.class, args);
	}

}
