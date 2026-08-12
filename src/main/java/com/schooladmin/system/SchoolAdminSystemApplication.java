package com.schooladmin.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point. {@code @SpringBootApplication} triggers component scanning (finds every
 * {@code @Component}/{@code @Service}/{@code @Repository} under this package) and
 * auto-configuration (wires up the embedded server, JPA, etc. based on what's on the
 * classpath) — covered properly in Module 5 (Spring Fundamentals: IoC & DI). No
 * hand-written beans exist yet, so running this currently just starts an empty web server.
 */
@SpringBootApplication
public class SchoolAdminSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolAdminSystemApplication.class, args);
	}

}
