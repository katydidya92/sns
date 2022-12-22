package com.fastcampus.project.sns.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AlarmArgs {

    // 알람을 발생시킨 사람
    private Integer fromUserId;

    // 알람을 받는 사람
    private Integer targetId;
}
