package ru.peony.receiver.domain.jira;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Accessors(fluent = true,chain = true)
public class IssueInput {
    private Fields fields;

    @AllArgsConstructor
    @RequiredArgsConstructor
    @Data
    @Accessors(fluent = true,chain = true)
    public static class Fields {
        private Project project;
        private String summary;
        private String description;
        private IssueType issuetype;
        private Assignee assignee;
    }

    @AllArgsConstructor
    @RequiredArgsConstructor
    @Data
    @Accessors(fluent = true,chain = true)
    public static class Project {
        private String key;
    }

    @AllArgsConstructor
    @RequiredArgsConstructor
    @Data
    @Accessors(fluent = true,chain = true)
    public static class IssueType {
        private String name;
    }

    @AllArgsConstructor
    @RequiredArgsConstructor
    @Data
    @Accessors(fluent = true,chain = true)
    public static class Assignee {
        private String id;
    }

}
