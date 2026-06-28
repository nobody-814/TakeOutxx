package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 公开接口
                        .requestMatchers("/**/login", "/**/register", "/**/login/**", "/**/register/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**/merchant/validList").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**/merchant/detail/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**/merchant/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**/merchant/page").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**/category/list/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**/product/list/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**/product/listByCategory").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**/product/detail/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**/order/rider/wait").permitAll()
                        .requestMatchers(HttpMethod.GET, "/**/rider/online/list").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 其余全部需要认证
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setContentType("application/json;charset=UTF-8");
                            res.setStatus(401);
                            res.getWriter().write("{\"code\":401,\"msg\":\"未登录\",\"data\":null}");
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "token", "Authorization", "*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}