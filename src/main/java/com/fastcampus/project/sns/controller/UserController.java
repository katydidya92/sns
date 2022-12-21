package com.fastcampus.project.sns.controller;

import com.fastcampus.project.sns.controller.request.UserJoinRequest;
import com.fastcampus.project.sns.controller.request.UserLoginRequest;
import com.fastcampus.project.sns.controller.response.AlarmResponse;
import com.fastcampus.project.sns.controller.response.Response;
import com.fastcampus.project.sns.controller.response.UserJoinResponse;
import com.fastcampus.project.sns.controller.response.UserLoginResponse;
import com.fastcampus.project.sns.service.UserService;
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
        return Response.success(
                userService.alarmList(authentication.getName(), pageable)
                        .map(AlarmResponse::fromAlarm)
        );
    }
}
