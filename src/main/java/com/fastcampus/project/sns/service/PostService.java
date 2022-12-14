package com.fastcampus.project.sns.service;

import com.fastcampus.project.sns.exception.ErrorCode;
import com.fastcampus.project.sns.exception.SnsApplicationException;
import com.fastcampus.project.sns.model.AlarmArgs;
import com.fastcampus.project.sns.model.AlarmType;
import com.fastcampus.project.sns.model.Comment;
import com.fastcampus.project.sns.model.Post;
import com.fastcampus.project.sns.model.entity.*;
import com.fastcampus.project.sns.model.event.AlarmEvent;
import com.fastcampus.project.sns.producer.AlarmProducer;
import com.fastcampus.project.sns.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final AlarmService alarmService;
    private final AlarmProducer alarmProducer;

    @Transactional
    public void create(String title, String body, String userName) {
        UserEntity userEntity = getUserEntityOrException(userName);
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId) {

        // user exist
        UserEntity userEntity = getUserEntityOrException(userName);

        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    public void delete(String userName, Integer postId) {
        // user exist
        UserEntity userEntity = getUserEntityOrException(userName);

        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        postEntityRepository.delete(postEntity);

    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName, Pageable pageable) {

        UserEntity userEntity = getUserEntityOrException(userName);

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {

        // user exist
        UserEntity userEntity = getUserEntityOrException(userName);

        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

        // check liked -> throw
        likeEntityRepository.findByUserAndPost(userEntity, postEntity)
                .ifPresent(it -> {
                    throw new SnsApplicationException(ErrorCode.ALREADY_LIKED,
                            String.format("userName %s already like post %d", userName, postId));
                });
        // like save
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));

        // kafka??? ???????????? ????????? ????????? ????????? ??? ????????? ??????.
//        AlarmEntity alarmEntity = alarmEntityRepository.save(
//                AlarmEntity.of(
//                        postEntity.getUser(),
//                        AlarmType.NEW_LIKE_ON_POST,
//                        new AlarmArgs(userEntity.getId(), postEntity.getId())
//                )
//        );
//
//        alarmService.send(alarmEntity.getId(), postEntity.getUser().getId());
        alarmProducer.send(new AlarmEvent(
                postEntity.getUser().getId(),
                AlarmType.NEW_LIKE_ON_POST,
                new AlarmArgs(userEntity.getId(), postEntity.getId())
        ));
    }

    public long likeCount(Integer postId) {

        // post exist
        PostEntity postEntity = getPostEntityOrException(postId);

        // ??? ????????? ????????? ????????? ???????????? ???????????? ????????? ????????? 
        // ????????? ?????? ????????? ???????????? ???????????? ??????
        // count like
        // List<LikeEntity> likeEntities = likeEntityRepository.findAllByPost(postEntity);
        // return likeEntities.size();

        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Integer postId, String userName, String comment) {

        UserEntity userEntity = getUserEntityOrException(userName);
        PostEntity postEntity = getPostEntityOrException(postId);

        // comment save
        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));

        // comment??? ?????? post ???????????? postEntity.getUser()??????,
        // ?????? ???????????? comment ???????????? userEntity.getId()??????.
//        AlarmEntity alarmEntity = alarmEntityRepository.save(
//                AlarmEntity.of(
//                        postEntity.getUser(),
//                        AlarmType.NEW_COMMENT_ON_POST,
//                        new AlarmArgs(userEntity.getId(), postEntity.getId())
//                )
//        );

//        alarmService.send(alarmEntity.getId(), postEntity.getUser().getId());

        // kafka ?????? ???????????? ??????
        alarmProducer.send(new AlarmEvent(
                postEntity.getUser().getId(),
                AlarmType.NEW_COMMENT_ON_POST,
                new AlarmArgs(userEntity.getId(), postEntity.getId())
        ));
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity postEntity = getPostEntityOrException(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable)
                .map(Comment::fromEntity);
    }

    private PostEntity getPostEntityOrException(Integer postId) {
        return postEntityRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
    }

    private UserEntity getUserEntityOrException(String userName) {
        return userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }
}
