package com.fastcampus.project.sns.controller;

import com.fastcampus.project.sns.controller.request.UserJoinRequest;
import com.fastcampus.project.sns.controller.request.UserLoginRequest;
import com.fastcampus.project.sns.controller.response.AlarmResponse;
import com.fastcampus.project.sns.controller.response.Response;
import com.fastcampus.project.sns.controller.response.UserJoinResponse;
import com.fastcampus.project.sns.controller.response.UserLoginResponse;
import com.fastcampus.project.sns.exception.ErrorCode;
import com.fastcampus.project.sns.exception.SnsApplicationException;
import com.fastcampus.project.sns.model.User;
import com.fastcampus.project.sns.service.UserService;
import com.fastcampus.project.sns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        return Response.success(
                UserJoinResponse.fromUser(userService.join(request.getName(), request.getPassword()))
        );
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(
            Pageable pageable,
            Authentication authentication
    ) {
        // authentication.getUser() 를 이용해서 가져오는 방식에서
        // 이미 가져온 authentication에 있는 userId를 가져오는 방식으로 변경
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class)
                .orElseThrow(() -> new SnsApplicationException(
                        ErrorCode.INTERNAL_SERVER_ERROR, "Casting to User class failed")
                );
        return Response.success(
                userService.alarmList(user.getId(), pageable).map(AlarmResponse::fromAlarm)
        );
    }
}
