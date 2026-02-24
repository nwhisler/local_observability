package com.nwhisler.local_observability;

import org.springframework.boot.SpringApplication;

public class TestLocalObservabilityApplication {

	public static void main(String[] args) {
		SpringApplication.from(LocalObservabilityApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
