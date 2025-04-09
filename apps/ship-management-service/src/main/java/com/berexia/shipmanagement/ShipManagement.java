package com.berexia.shipmanagement;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class ShipManagement {
	public static void main(String[] args) {
		SpringApplication.run(ShipManagement.class, args);
	}
}
