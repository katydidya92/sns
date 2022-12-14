package com.fastcampus.project.sns.service;

import com.fastcampus.project.sns.exception.ErrorCode;
import com.fastcampus.project.sns.exception.SnsApplicationException;
import com.fastcampus.project.sns.model.entity.PostEntity;
import com.fastcampus.project.sns.model.entity.UserEntity;
import com.fastcampus.project.sns.repository.PostEntityRepository;
import com.fastcampus.project.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Transactional
    public void create(String title, String body, String userName) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(
                        ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName))
                );
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }
}
