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
//		try {
//			Flux<Integer> range = Flux.range(1, 10);
//			range
//					.log()
//					.subscribe(System.out::println,
//							System.err::println,
//							() -> System.out.println("Done"));
//			range.log().subscribe(new BaseSubscriber<Integer>() {
//				int am = 0;
//				@Override
//				protected void hookOnNext(Integer value) {
//					System.out.println("Value " + value);
//					am++;
//					if (am % 2 == 0)
//						request(2);
//				}
//
//				@Override
//				protected void hookOnSubscribe(Subscription subscription) {
//					System.out.println("Subscription");
//					request(2);
//				}
//			});
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		SpringApplication.run(IssueReceiverApplication.class, args);
	}

}
