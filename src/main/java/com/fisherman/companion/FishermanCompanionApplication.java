package com.fisherman.companion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.fisherman.companion.config.FishermanCompanionConfig;

@Import(FishermanCompanionConfig.class)
@SpringBootApplication
public class FishermanCompanionApplication {

	public static void main(String[] args) {
		SpringApplication.run(FishermanCompanionApplication.class, args);
	}

}
