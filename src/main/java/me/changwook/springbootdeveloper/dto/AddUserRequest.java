package me.changwook.springbootdeveloper.dto;

import lombok.Getter;
import lombok.Setter;

//사용자 정보를 담고 있는 객체
@Getter
@Setter
public class AddUserRequest {
    private String email;
    private String password;
}
