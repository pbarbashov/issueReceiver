package ru.peony.receiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import ru.peony.receiver.domain.IssueCreationResult;
import ru.peony.receiver.domain.IssueEvent;
import ru.peony.receiver.domain.jira.GroupMembersResponse;
import ru.peony.receiver.domain.jira.IssueInput;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.net.URLDecoder.decode;
import static java.text.MessageFormat.format;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/issue")
@RequiredArgsConstructor
@Slf4j
public class IssueController {
    private final Random random = new Random(System.nanoTime());
    private final WebClient webClient;
    private final AppConfiguration appConf;
    private final String format = """
                Произошла ошибка в {0} в компоненте {1}-{2}.
                Информация об инсталляции:
                Адрес объекта: {3}, Время инсталляции {4}, Версия ПО {5}
                Инсталлированные комоненты:
                {6}
                Описание ошибки:
                {7}
                """;
    private final String groupUri = "/group/member";

    @PostMapping(value = "/create",consumes = APPLICATION_JSON_VALUE,produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<IssueCreationResult>> createIssue(@RequestBody IssueEvent issueEvent) {
        IssueEvent.Installation installation = issueEvent.installationDescription();
        IssueInput issueInput = new IssueInput()
                .fields(new IssueInput.Fields()
                        .description(
                                format(format, issueEvent.timeStamp(),
                                        issueEvent.failedComponent().name(),
                                        issueEvent.failedComponent().softwareVersion(),
                                        installation.address(),
                                        installation.installTime(),
                                        installation.softwareVersion(),
                                        Stream.of(installation.components())
                                                .map(components -> components.toString() + ", ")
                                                .reduce(String::concat).get(),
                                        issueEvent.errorDescription())
                        )
                        .summary(format("Ошибка \"{0}-{1}\"",
                                issueEvent.failedComponent().name(), issueEvent.failedComponent().softwareVersion()))
                        .issuetype(new IssueInput.IssueType(appConf.getIssueType()))
                        .project(new IssueInput.Project(appConf.getProjectKey())));

        Mono<IssueInput> issueInputMono = webClient.get()
                .uri(groupUri, uriBuilder -> uriBuilder.queryParam("groupname", installation.supportKey()).build())
                .exchange()
                .flatMap(clientResponse -> switch (clientResponse.statusCode()) {
                    case OK -> clientResponse.bodyToMono(GroupMembersResponse.class);
                    case NOT_FOUND -> webClient.get()
                            .uri(groupUri, uriBuilder -> uriBuilder.queryParam("groupname", appConf.getReserveGroupName()).build())
                            .exchange()
                            .flatMap(cr -> switch (cr.statusCode()) {
                                case OK -> cr.bodyToMono(GroupMembersResponse.class);
                                default -> throw new WebClientResponseException(cr.statusCode().value(),
                                        format("Поиск в группе {0} - {1}", appConf.getReserveGroupName(), cr.statusCode().getReasonPhrase()),
                                        cr.headers().asHttpHeaders(), null, Charset.defaultCharset());
                            });
                    default -> throw new WebClientResponseException(clientResponse.statusCode().value(),
                            format("Поиск в группе {0} - {1}", installation.supportKey(), clientResponse.statusCode().getReasonPhrase()),
                            clientResponse.headers().asHttpHeaders(), null, Charset.defaultCharset());
                })
                .onErrorStop()
                .map(groupMembersResponse -> {
                    issueInput.fields().assignee(new IssueInput.Assignee(randomUser(groupMembersResponse)));
                    return issueInput;
                });

         return webClient.post()
                 .uri("/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(issueInputMono,IssueInput.class))
                .exchange()
                .flatMap(clientResponse -> clientResponse.toEntity(IssueCreationResult.class));
    }

    private String randomUser(GroupMembersResponse groupMembersResponse) {
        List<GroupMembersResponse.GroupMember> values = groupMembersResponse.values();
        if (values == null || values.isEmpty()) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(groupMembersResponse.self());
            MultiValueMap<String, String> queryParams = uriComponentsBuilder.build().getQueryParams();
            throw new IllegalStateException(format("Пустая группа \"{0}\"", decode(queryParams.get("groupname").get(0), StandardCharsets.UTF_8)));
        }
        return values.get(random.nextInt(values.size())).accountId();
    }
}