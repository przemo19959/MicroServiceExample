package pl.dabrowski.MicroService1.controllers;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController {
	public static final String BASE_URL = "/users";
	
	@Value("${service1.requestTopic}")
	private String requestTopic;

	private final DiscoveryClient discoveryClient;
	private final RestTemplate restTemplate;
	private final ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;

	@Autowired
	public UserController(DiscoveryClient discoveryClient, RestTemplate restTemplate,
			ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate) {
		this.discoveryClient = discoveryClient;
		this.restTemplate = restTemplate;
		this.replyingKafkaTemplate = replyingKafkaTemplate;
	}

	@GetMapping("/template")
	public ResponseEntity<String> findAll() {
		String dbServiceName = "microservice2";
		List<ServiceInstance> services = discoveryClient.getInstances(dbServiceName);
		if (services.size() == 0)
			return ResponseEntity.status(HttpStatus.NOT_FOUND)//
					.body(MessageFormat.format("Service with name: {0} not found in registry!", dbServiceName));

		String url = services.get(0).getUri() + "/users";
		return ResponseEntity.ok(restTemplate.getForObject(url, String.class));
	}

	@GetMapping("/message")
	public ResponseEntity<String> findAll2() throws InterruptedException, ExecutionException {
		ProducerRecord<String, String> producerRecord = new ProducerRecord<>(requestTopic, "");
		RequestReplyFuture<String, String, String> future = replyingKafkaTemplate.sendAndReceive(producerRecord);
		ConsumerRecord<String, String> response = future.get();
		return ResponseEntity.ok(response.value());
	}
}
