package ru.peony.receiver;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.peony.receiver.domain.Issue;
import ru.peony.receiver.domain.IssueCreationResult;

import static org.springframework.util.MimeTypeUtils.*;

@RestController
@RequestMapping("/issue")
@RequiredArgsConstructor
public class IssueController {
    private final WebClient webClient;

    @PostMapping(value = "/create",consumes = APPLICATION_JSON_VALUE,produces = APPLICATION_JSON_VALUE)
    public Mono<IssueCreationResult> createIssue(@RequestBody Issue issue) {
        return Mono.just(new IssueCreationResult("PEONY-1"));
    }
}
