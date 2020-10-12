package pl.dabrowski.MicroService2;

import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@SpringBootApplication
@EnableKafka
public class MicroService2Application {
	public static void main(String[] args) {
		SpringApplication.run(MicroService2Application.class, args);
	}

	@Bean
	public ApplicationRunner runner() {
		return args -> {
			Flyway flyway = Flyway.configure()//
					.dataSource("jdbc:h2:mem:gameApp;DB_CLOSE_DELAY=-1", "mydb", null).load();
			flyway.migrate();
		};
	}

	@Bean
	public KafkaTemplate<String, String> replyTemplate(ProducerFactory<String, String> pf,
			ConcurrentKafkaListenerContainerFactory<String, String> factory) {
		KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(pf);
		factory.getContainerProperties().setMissingTopicsFatal(false);
		factory.setReplyTemplate(kafkaTemplate);
		return kafkaTemplate;
	}
}