package me.changwook.springbootdeveloper.config.oauth;

import lombok.RequiredArgsConstructor;
import me.changwook.springbootdeveloper.domain.User;
import me.changwook.springbootdeveloper.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
//부모 클래스인 DefaultOAAuth2UserService에서 제공하는 OAuth 서비스에서 제공하는 정보를 기반으로 유저 객체를 만들어주는 loadUser()메서드를 사용해 사용자 객체를 불러옵니다.
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //요청을 바탕으로 유저 정보를 담은 객체 반환
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);
        return user;
    }
    //유저가 있으면 업데이트, 없으면 유저 생성
    private User saveOrUpdate(OAuth2User oAuth2User){
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name)).orElse(User.builder().email(email).nickname(name).build());
        return  userRepository.save(user);
    }
}
