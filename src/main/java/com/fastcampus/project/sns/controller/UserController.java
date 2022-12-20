package com.fastcampus.project.sns.controller;

import com.fastcampus.project.sns.controller.request.UserJoinRequest;
import com.fastcampus.project.sns.controller.request.UserLoginRequest;
import com.fastcampus.project.sns.controller.response.Response;
import com.fastcampus.project.sns.controller.response.UserJoinResponse;
import com.fastcampus.project.sns.controller.response.UserLoginResponse;
import com.fastcampus.project.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
