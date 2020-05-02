package com.example.reactivewebsocketserver;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@Slf4j
public class ReactiveWebsocketServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveWebsocketServerApplication.class, args);
	}

}

@Controller
class EchoController {

    @Autowired
    UserCrudRepository repository;

//    @MessageMapping("echo")
//    public Publisher<String> echo(String input) {
//        return Flux.range(0, 100).map(el -> String.valueOf(el)).delayElements(Duration.ofSeconds(1));
//    }

    @MessageMapping("echo")
    public Publisher<User> echo(String input) {
        return repository.findAll();
    }
}


@Repository
interface UserCrudRepository extends ReactiveCrudRepository<User, String> {


}

@RestController
class FluxController {

    @Autowired
    UserCrudRepository repository;

    @GetMapping(value = "/users/{id}")
    public Mono<User> getUserById(@PathVariable String id) {
        return repository.findById(id);
    }

    @GetMapping(value = "/users", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> getAll() {
        Flux<User> flux = repository.findAll();
        return flux.delayElements(Duration.ofSeconds(1));
    }

    @PostMapping("users")
    public Mono<User> saveUser(@RequestBody User user) {
        return repository.save(user);
    }

    @DeleteMapping("/users/{id}")
    public Mono<Void> deleteUser(@PathVariable String id) {
        return repository.deleteById(id);
    }

    @DeleteMapping("/users")
    public Mono<Void> deleteAllUser() {
        return repository.deleteAll();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class User {

    @Id
    private String id;
    private String fName;
    private String lName;
    private int age;

}