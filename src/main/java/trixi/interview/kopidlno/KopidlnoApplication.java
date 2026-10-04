package trixi.interview.kopidlno;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import trixi.interview.kopidlno.facade.SaveTownDataFacade;

@SpringBootApplication
public class KopidlnoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KopidlnoApplication.class, args);
	}

	@Bean
	CommandLineRunner saveTownDataOnStartup(SaveTownDataFacade facade) {
		return args -> facade.saveTownData();
	}

}
