package com.paint.paint;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;

@SpringBootApplication
public class PaintApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaintApplication.class, args);
	}

	public void customize(ConfigurableWebServerFactory factory) {
		// Set the desired port
		factory.setPort(8000);
	}

}
