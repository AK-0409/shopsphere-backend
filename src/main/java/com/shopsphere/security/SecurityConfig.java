
package com.shopsphere.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthenticationEntryPoint authenticationEntryPoint) {

        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                authenticationEntryPoint
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/users/register",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()

                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/products/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/products/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/products/**",
                                "/api/categories/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reviews/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/reviews"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/reviews/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/reviews/**"
                        ).authenticated()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {

        return new ProviderManager(authenticationProvider());
    }
}
