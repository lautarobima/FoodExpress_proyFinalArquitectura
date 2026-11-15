package com.foodexpress.restaurantes_menu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RestaurantesMenuApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantesMenuApplication.class, args);
	}

}
