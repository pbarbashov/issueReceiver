package ru.peony.receiver.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Data
@Accessors(fluent = true,chain = true)
public class IssueEvent {
    private UUID id;
    private OffsetDateTime timeStamp;
    private Installation installationDescription;
    private String errorDescription;
    private Component failedComponent;

    @RequiredArgsConstructor
    @Data
    @Accessors(fluent = true,chain = true)
    public static class Installation {
        private String supportKey;
        private String address;
        private OffsetDateTime installTime;
        private String softwareVersion;
        private List<Component> components;
    }

    @RequiredArgsConstructor
    @Data
    @Accessors(fluent = true,chain = true)
    public static class Component {
        private String name;
        private String softwareVersion;

        @Override
        public String toString() {
            return name + "-" + softwareVersion;
        }
    }

}
