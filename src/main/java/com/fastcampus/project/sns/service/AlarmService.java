package com.fastcampus.project.sns.service;

import com.fastcampus.project.sns.exception.ErrorCode;
import com.fastcampus.project.sns.exception.SnsApplicationException;
import com.fastcampus.project.sns.model.AlarmArgs;
import com.fastcampus.project.sns.model.AlarmType;
import com.fastcampus.project.sns.model.entity.AlarmEntity;
import com.fastcampus.project.sns.model.entity.UserEntity;
import com.fastcampus.project.sns.repository.AlarmEntityRepository;
import com.fastcampus.project.sns.repository.EmitterRepository;
import com.fastcampus.project.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmService {

    private final static Long DEFAULT_TIME_OUT = 60L * 1000 * 60;
    private final static String ALARM_NAME = "alarm";
    private final EmitterRepository emitterRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final UserEntityRepository userEntityRepository;

    //    public void send(Integer userId, Integer alarmId) {
    public void send(AlarmType type, AlarmArgs args, Integer receiverUserId) {
        UserEntity user = userEntityRepository.findById(receiverUserId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND));

        AlarmEntity alarmEntity = alarmEntityRepository.save(AlarmEntity.of(user, type, args));

        // alarmService

        emitterRepository.get(receiverUserId).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(alarmEntity.getId().toString()).name(ALARM_NAME).data("new alarm"));
            } catch (IOException e) {
                emitterRepository.delete(receiverUserId);
                throw new SnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }, () -> log.info("No emitter founded"));

    }

    public SseEmitter connectAlarm(Integer userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIME_OUT);
        emitterRepository.save(userId, sseEmitter);
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        try {
            sseEmitter.send(SseEmitter.event().id("id").name(ALARM_NAME).data("connect completed"));
        } catch (IOException e) {
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }

}
