package com.taru.eventmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/about", "/login", "/register/**", "/event", "/css/**", "/js/**")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .userDetailsService(customUserDetailsService)
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/event", true) // `true` force toujours la redirection vers /event
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error=true")
                        .permitAll()
                ).logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }
}
