package com.library_management_system.LibraryCRUD;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableCaching
public class LibraryCrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryCrudApplication.class, args);
	}

}
