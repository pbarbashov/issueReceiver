package ru.peony.receiver.domain;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Data
@Accessors(fluent = true,chain = true)
public class IssueCreationResult {
    private String key;
    private Long id;
}
