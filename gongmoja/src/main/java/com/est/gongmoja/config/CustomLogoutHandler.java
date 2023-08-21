package com.est.gongmoja.config;

import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.jwt.JwtTokenUtil;
import com.est.gongmoja.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutSuccessHandler {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Cookie temp = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("gongMoAccessToken"))
                .findAny()
                .get();

        String username = jwtTokenUtil.getUsername(temp.getValue());
        userService.logOut(username);

        // 덮어 쓸 쿠키 객체 작성
        Cookie accessTokenCookie = new Cookie("gongMoAccessToken","destroyed");
        Cookie refreshTokenCookie = new Cookie("gongMoRefreshToken","destroyed");
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        //로그아웃 시 메인페이지로 가게끔
        response.sendRedirect("/");
    }
}
