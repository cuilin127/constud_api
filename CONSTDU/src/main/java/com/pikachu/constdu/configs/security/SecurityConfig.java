package com.pikachu.constdu.configs.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    private final RsaKeyProperties rsaKeys;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final AccessDeniedHandler hd;
    private final UserDetailedServiceImpl userDetailedService;
    private final BCryptPasswordEncoder passwordEncoder;
    public SecurityConfig(RsaKeyProperties _rsaKeys, JwtAuthenticationConverter _jwtAuthenticationConverter,
                          AccessDeniedHandler _hd, UserDetailedServiceImpl _userDetailedService,
                          BCryptPasswordEncoder _passwordEncoder) {
        this.rsaKeys = _rsaKeys;
        this.jwtAuthenticationConverter = _jwtAuthenticationConverter;
        this.hd = _hd;
        this.userDetailedService = _userDetailedService;
        this.passwordEncoder = _passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors().disable()
                .authorizeRequests( auth -> auth
                                .antMatchers("/","/test/**","/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html","/ocr/getPassportInfo").permitAll()
                                .antMatchers("/user/signup","/user/sendTempVerificationCode","/user/updatePasswordWithoutLoggedIn","/user/validateCode").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // (3)
                .httpBasic(Customizer.withDefaults()) // (4)
                .build();
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailedService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }
}
