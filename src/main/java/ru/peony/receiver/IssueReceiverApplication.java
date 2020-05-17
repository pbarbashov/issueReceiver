package ru.peony.receiver;

import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class IssueReceiverApplication {

	public static void main(String[] args) {
		SpringApplication.run(IssueReceiverApplication.class, args);
	}

}
