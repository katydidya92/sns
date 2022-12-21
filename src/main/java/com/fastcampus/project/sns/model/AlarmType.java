package com.fastcampus.project.sns.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AlarmType {
    NEW_COMMENT_ON_POST("new Comment!"),
    NEW_LIKE_ON_POST("new Like!"),
    ;

    private final String alarmText;
}
