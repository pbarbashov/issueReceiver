package ru.peony.receiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.peony.receiver.domain.IssueEvent;
import ru.peony.receiver.domain.IssueCreationResult;
import ru.peony.receiver.domain.IssueInput;
import java.text.MessageFormat;
import java.util.stream.Stream;

import static org.springframework.util.MimeTypeUtils.*;

@RestController
@RequestMapping("/issue")
@RequiredArgsConstructor
@Slf4j
public class IssueController {
    private final WebClient webClient;
    private final AppConfiguration appConf;

    @PostMapping(value = "/create",consumes = APPLICATION_JSON_VALUE,produces = APPLICATION_JSON_VALUE)
    public Mono<IssueCreationResult> createIssue(@RequestBody IssueEvent issueEvent) {
        String format = """
                Произошла ошибка в {0} в компоненте {1}-{2}.
                Информация об инсталляции:
                Адрес объекта: {3}, Время инсталляции {4}, Версия ПО {5}
                Инсталлированные комоненты:
                {6}
                Описание ошибки:
                {7}
                """;
        IssueInput issueInput = new IssueInput()
                .fields(new IssueInput.Fields()
                        .description(
                                MessageFormat.format(format, issueEvent.timeStamp(),
                                issueEvent.failedComponent().name(),
                                issueEvent.failedComponent().softwareVersion(),
                                issueEvent.installationDescription().address(),
                                issueEvent.installationDescription().installTime(),
                                issueEvent.installationDescription().softwareVersion(),
                                Stream.of(issueEvent.installationDescription().components())
                                        .map(components -> components.toString() + ", ")
                                        .reduce(String::concat).get(),
                                issueEvent.errorDescription())
                        )
                        .summary(MessageFormat.format("Ошибка \"{0}-{1}\"",
                                issueEvent.failedComponent().name(), issueEvent.failedComponent().softwareVersion()))
                        .issuetype(new IssueInput.IssueType(appConf.getIssueType()))
                        .project(new IssueInput.Project(appConf.getProjectKey())));

        return webClient
                .post()
                .uri("/issue")
                .headers(httpHeaders -> httpHeaders.setBasicAuth("cGJhcmJhc2hvdkBnbWFpbC5jb206akE4cXJoYjI4dDYwMmI0dkRUYzJEOUZD"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(issueInput)
                .exchange()
                .doOnError(throwable -> log.error("",throwable))
                .flatMap(clientResponse -> {
                    System.out.println(clientResponse.rawStatusCode());
                    return clientResponse.bodyToMono(IssueCreationResult.class);});
    }
}