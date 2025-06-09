package com.example.kulkita.config;

import com.example.kulkita.security.jwt.AuthTokenFilter;
import com.example.kulkita.security.oauth.CustomOAuth2UserService;
import com.example.kulkita.security.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final CustomOAuth2UserService oAuth2UserService;
    private final AuthTokenFilter authTokenFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          CustomOAuth2UserService oAuth2UserService,
                          AuthTokenFilter authTokenFilter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.userDetailsService = userDetailsService;
        this.oAuth2UserService = oAuth2UserService;
        this.authTokenFilter = authTokenFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/test/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/health").permitAll()

                                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                                .requestMatchers("/error").permitAll()

                                .requestMatchers("OPTIONS", "/**").permitAll()

                                .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 ->
                        oauth2
                                .loginPage("/login")
                                .defaultSuccessUrl("/dashboard", true)
                                .userInfoEndpoint(userInfo ->
                                        userInfo.userService(oAuth2UserService)
                                )
                                .authorizationEndpoint(authorization ->
                                        authorization.baseUri("/oauth2/authorize")
                                )
                )

                .formLogin(form -> form.disable())

                .exceptionHandling(exceptions ->
                        exceptions
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.addHeader("Access-Control-Allow-Origin", "*");
                                    response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                                    response.addHeader("Access-Control-Allow-Headers", "*");

                                    if (request.getRequestURI().startsWith("/api/")) {
                                        response.sendError(401, "Unauthorized");
                                    } else {
                                        response.sendRedirect("/login");
                                    }
                                })
                );

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}