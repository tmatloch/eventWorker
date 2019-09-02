package pl.tmatloch.permutationworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PermutationWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PermutationWorkerApplication.class, args);
	}

}
