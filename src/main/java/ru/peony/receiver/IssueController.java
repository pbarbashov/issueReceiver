package ru.peony.receiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.peony.receiver.domain.Issue;
import ru.peony.receiver.domain.IssueCreationResult;
import ru.peony.receiver.domain.IssueInput;

import static org.springframework.util.MimeTypeUtils.*;

@RestController
@RequestMapping("/issue")
@RequiredArgsConstructor
@Slf4j
public class IssueController {
    private final WebClient webClient;

    @PostMapping(value = "/create",consumes = APPLICATION_JSON_VALUE,produces = APPLICATION_JSON_VALUE)
    public Mono<IssueCreationResult> createIssue(@RequestBody Issue issue) {
        IssueInput issueInput = new IssueInput()
                .fields(new IssueInput.Fields()
                        .description("from spring")
                        .summary("From Spring")
                        .issuetype(new IssueInput.IssueType("Bug"))
                        .project(new IssueInput.Project("PEONY")));

        return webClient
                .post()
                .uri("/issue")
                .header("Authorization","Basic cGJhcmJhc2hvdkBnbWFpbC5jb206akE4cXJoYjI4dDYwMmI0dkRUYzJEOUZD")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(issueInput)
                .exchange()
                .doOnError(throwable -> log.error("",throwable))
                .flatMap(clientResponse -> {
                    System.out.println(clientResponse.rawStatusCode());
                    return clientResponse.bodyToMono(IssueCreationResult.class);});

     //   return Mono.just(new IssueCreationResult("PEONY-1"));
    }
}
