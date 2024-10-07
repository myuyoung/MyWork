package me.changwook.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.changwook.springbootdeveloper.config.jwt.TokenProvider;
import me.changwook.springbootdeveloper.config.oauth.OAuth2UserCustomService;
import me.changwook.springbootdeveloper.repository.RefreshTokenRepository;
import me.changwook.springbootdeveloper.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure(){   //스프링 시큐리티 기능 비활성화
        return (web) -> web.ignoring().requestMatchers(toH2Console()).requestMatchers(
                new AntPathRequestMatcher("/img/**"),
                new AntPathRequestMatcher("/css/**"),
                new AntPathRequestMatcher("/js/**")
        );
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //토큰 방식으로 인증을 하기 때문에 기존에 사용하던 폼 로그인, 세션 비활성화 1
        //토큰방식으로 인증을 하므로 기존 폼 로그인, 세션 기능을 비활성화 합니다.
        return http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(management ->management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //헤더를 확인할 커스텀 필터 추가 2
                //헤더값을 확인할 커스텀 필터를 추가합니다.
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                //토큰 재발급 URL은 인증 없이 접근 가능하도록 설정. 나머지 API URL은 인증 필요 3
                //토큰 재발급 URL은 인증없이 접근하도록 설정하고 나머지 API들은 인증을 해야 접근하도록 설정합니다
                .authorizeRequests(auth-> auth
                        .requestMatchers(new AntPathRequestMatcher("/api/token")).
                        permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).
                        authenticated()
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        //Authorization 요청과 관련된 상태 저장 4
                        //OAuth2에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸 수 있또록 인증 요청과 관련된 상태를 저장할 저장소를 설정합니다. 인증 성공 시 실행할 핸들러도 설정합니다.
                        .authorizationEndpoint(authorizationEndPoint ->
                                authorizationEndPoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuth2UserCustomService))
                        //인증 성공 시 실행할 핸들러 5
                        //OAuth2에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸 수 있또록 인증 요청과 관련된 상태를 저장할 저장소를 설정합니다. 인증 성공 시 실행할 핸들러도 설정합니다.
                        .successHandler(oAuth2SuccessHandler())
                )
                // /api로 시작하는 url인 경우 401 상태 코드를 반환하도록 예외 처리 6
                .exceptionHandling(exceptionHandling -> exceptionHandling.defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")
                ))
                .build();
    }
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
