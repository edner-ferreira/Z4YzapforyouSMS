package br.com.Z4Yzapforyou;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class Z4YzapforyouApplication {

	@Profile("dev")
	@Bean
	public WebClient webClientZ4y(WebClient.Builder builder) {
//		String token = "\\$2b\\$10\\$qwxzu017XqFhtCkOd9cFHe_.2RJoiYWra0kQxpDWFctXd_qgmkemi";
//		baseUrl("http://localhost:21465/api/sessao1")
		return builder
//			.baseUrl("https://smsgateway.me/api/v4/message")
			//.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Z4YzapforyouApplication.class, args);
	}

}
