package ru.peony.receiver.domain.jira;


/*
*
* "self": "https://peony.atlassian.net/rest/api/3/group/member?includeInactiveUsers=false&maxResults=50&groupname=%D0%A1%D0%97%D0%90%D0%9E&startAt=0",
    "maxResults": 50,
    "startAt": 0,
    "total": 2,
    "isLast": true,
    "values": [
        {
            "self": "https://peony.atlassian.net/rest/api/3/user?accountId=5ebd20cd9756e40b84d144fe",
            "accountId": "5ebd20cd9756e40b84d144fe",
            "avatarUrls": {
                "48x48": "https://secure.gravatar.com/avatar/e1f32a914bfab6b1203749d40abef73b?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Fdefault-avatar-6.png&size=48&s=48",
                "24x24": "https://secure.gravatar.com/avatar/e1f32a914bfab6b1203749d40abef73b?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Fdefault-avatar-6.png&size=24&s=24",
                "16x16": "https://secure.gravatar.com/avatar/e1f32a914bfab6b1203749d40abef73b?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Fdefault-avatar-6.png&size=16&s=16",
                "32x32": "https://secure.gravatar.com/avatar/e1f32a914bfab6b1203749d40abef73b?d=https%3A%2F%2Favatar-management--avatars.us-west-2.prod.public.atl-paas.net%2Fdefault-avatar-6.png&size=32&s=32"
            },
            "displayName": "Александр",
            "active": true,
            "timeZone": "Europe/Moscow",
            "accountType": "atlassian"
        },
*
*
* */

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@RequiredArgsConstructor
@Data
@Accessors(fluent = true,chain = true)
public class GroupMembersResponse {
    private String self;
    private Integer maxResults;
    private Integer startAt;
    private Integer total;
    private Boolean isLast;
    private List<GroupMember> values;

    @RequiredArgsConstructor
    @Data
    @Accessors(fluent = true,chain = true)
    public static class GroupMember {
        private String accountId;
        private Boolean active;
    }
}
