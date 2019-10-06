package pl.tmatloch.eventworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableDiscoveryClient
public class EventWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventWorkerApplication.class, args);
	}

}
