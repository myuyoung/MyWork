package me.changwook.springbootdeveloper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//로그인,회원 가입 경로로 접근하면 뷰 파일을 연결하는 컨틀롤러를 생성
// /login 경로로 접근하면 login() 메서드가 login.html을, /singup 경로에 접근하면 signup()메서드는 signup.html을 반환합니다.
@Controller
public class UserViewController {
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/signup")
    public String signup(){
        return "signup";
    }
}
