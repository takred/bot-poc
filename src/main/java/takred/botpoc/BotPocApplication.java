package takred.botpoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BotPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotPocApplication.class, args);
	}

}
