package com.rukevwe.invoicegenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class InvoiceGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceGeneratorApplication.class, args);
	}

}
