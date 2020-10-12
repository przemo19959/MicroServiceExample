package pl.dabrowski.MicroService2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import pl.dabrowski.MicroService2.entities.User;
import pl.dabrowski.MicroService2.repositories.UserRepository;

@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController {
	public static final String BASE_URL = "/users";

	private final UserRepository userRepository;
	private final String requestTopic="requests";
	private final String groupId="users";

	@Autowired
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping
	public ResponseEntity<Iterable<User>> findAll() {
		return ResponseEntity.ok(userRepository.findAll());
	}

	@KafkaListener(topics = requestTopic, groupId = groupId)
	@SendTo
	public String handle(String data) throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		return ow.writeValueAsString(userRepository.findAll());
	}
}
