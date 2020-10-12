package pl.dabrowski.MicroService1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MicroService1Application {
	public static void main(String[] args) {
		SpringApplication.run(MicroService1Application.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	
	@Value("${service1.groupId}")
	private String groupId;
	@Value("${service1.replyTopic}")
	private String replyTopic;

	@Bean
	public ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate(ProducerFactory<String, String> pf,
			ConcurrentKafkaListenerContainerFactory<String, String> factory) {
		ConcurrentMessageListenerContainer<String, String> replyContainer = factory.createContainer(replyTopic);
		replyContainer.getContainerProperties().setMissingTopicsFatal(false);
		replyContainer.getContainerProperties().setGroupId(groupId);
		return new ReplyingKafkaTemplate<>(pf, replyContainer);
	}	
}
