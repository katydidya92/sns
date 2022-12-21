package com.fastcampus.project.sns.repository;

import com.fastcampus.project.sns.model.entity.AlarmEntity;
import com.fastcampus.project.sns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmEntityRepository extends JpaRepository<AlarmEntity, Integer> {

    // user 호출을 위해 DB로 부터 2번 가져오기 때문에 변경
    // Page<AlarmEntity> findAllByUser(UserEntity user, Pageable pageable);
    Page<AlarmEntity> findAllByUserId(Integer userId, Pageable pageable);
}
