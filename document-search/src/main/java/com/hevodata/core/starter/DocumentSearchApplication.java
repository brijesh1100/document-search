
package com.hevodata.core.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.hevodata.core.controller.SearchController;

/**
 * This class will boot the Application
 * and you can hit the query
 * {@link SearchController}
 * */
@SpringBootApplication
@ComponentScan(basePackages = "com.hevodata.core")
public class DocumentSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentSearchApplication.class, args);
	}
}
