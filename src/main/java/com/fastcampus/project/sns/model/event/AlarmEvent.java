package com.fastcampus.project.sns.model.event;

import com.fastcampus.project.sns.model.AlarmArgs;
import com.fastcampus.project.sns.model.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmEvent {
    private Integer receiveUserId;
    private AlarmType alarmType;
    private AlarmArgs args;
}
