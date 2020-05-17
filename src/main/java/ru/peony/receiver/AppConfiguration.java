package ru.peony.receiver;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Configuration
@ConfigurationProperties(prefix = "jira")
@Data
@Slf4j
public class AppConfiguration {
    private String baseUrl;
    private String projectKey;
    private String issueType;
    private String reserveGroupName;

    @Bean
    public ObjectMapper objectMapper() {
        val objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, true);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    Request enhance(Request request) {
        StringBuilder group = new StringBuilder();
        request.onRequestBegin(theRequest -> {
            // append request url and method to group
            group.append(theRequest.getMethod())
                    .append(request.getURI());
        });
        request.onRequestHeaders(theRequest -> {
            for (HttpField header : theRequest.getHeaders()) {
                group.append(System.lineSeparator()).append(header.getHeader());
            }
        });
        request.onRequestContent((theRequest, content) -> {
            group.append(System.lineSeparator()).append(StandardCharsets.UTF_8.decode(content).toString());

        });
        request.onRequestSuccess(theRequest -> {
            log.info(group.toString());
            group.delete(0, group.length());
        });
        group.append("\n");
        request.onResponseBegin(theResponse -> {
            // append response status to group
        });
        request.onResponseHeaders(theResponse -> {
            for (HttpField header : theResponse.getHeaders()) {
                // append response headers to group
            }
        });
        request.onResponseContent((theResponse, content) -> {
            // append content to group
        });
        request.onResponseSuccess(theResponse -> {
            log.debug(group.toString());
        });
        return request;
    }

    @Bean
    public WebClient webClient(@Autowired ObjectMapper objectMapper) {
        HttpClient httpClient = new HttpClient(new SslContextFactory.Client(true)){
            @Override
            public Request newRequest(URI uri) {
                Request request = super.newRequest(uri);
                return enhance(request);
            }
        };
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter((request, next) ->
                        next.exchange(ClientRequest.from(request)
                                .headers(headers -> headers.setBasicAuth("cGJhcmJhc2hvdkBnbWFpbC5jb206akE4cXJoYjI4dDYwMmI0dkRUYzJEOUZD"))
                                .build()))
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientCodecConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .clientConnector(new JettyClientHttpConnector(httpClient))
                .build();
    }
}
