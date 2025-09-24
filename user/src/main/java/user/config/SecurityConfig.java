package user.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import user.domain.service.BannedUserFilter;
import user.infrastructure.security.CustomAccessDeniedHandler;
import user.infrastructure.security.JwtAuthenticationFilter;
import user.infrastructure.security.OAuth2LoginSuccessHandler;
import user.infrastructure.security.RestAuthenticationEntryPoint;


@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final BannedUserFilter bannedUserFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).exceptionHandling(exception -> exception
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                ).authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()

                        .requestMatchers("/api/users/register-client").permitAll()
                        .requestMatchers("/api/users/first-login").permitAll()
                        .requestMatchers("/api/users/change-password").permitAll()
                        .requestMatchers("/index.html", "/static/**", "/js/**", "/css/**", "/images/**","/generate-week.html",
                                "/shift-planning.html","/shifts.html","/manager.html","clients.html",
                                "/firebaseNotif.html","/firebase-messaging-sw.js","/notifwebsocketclient.html").permitAll()

                        .requestMatchers("/tracking.html").permitAll()
                        .requestMatchers("/api/v1/livreurs/**").permitAll()
                        .requestMatchers("/api/users/enable-2fa", "/api/users/validate-2fa").permitAll() // Allow without auth
                        .requestMatchers("/api/users/verify-email").permitAll()
                        .requestMatchers("/api/auth/forgot-password").permitAll()
                        .requestMatchers("/api/auth/reset-password").permitAll()
                        .requestMatchers("/api/auth/request-reset").permitAll()
                        .requestMatchers("/api/auth/verify-reset-code").permitAll()
                        .requestMatchers("/api/users/register-livreur").permitAll()
                        .requestMatchers("/api/users/register-restaurant-manager").permitAll()
                        .requestMatchers("/api/users/register-moderateur").permitAll()
                        .requestMatchers("/api/users/register-financier").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()

                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers("/api/auth/refresh").permitAll()
                        .requestMatchers("/api/auth/logout").permitAll()
                        .requestMatchers("/financier/**").hasRole("FINANCIER")
                        .requestMatchers("/delivery-costs/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/oauth2/authorization/**").permitAll()
                        .requestMatchers("/login/oauth2/code/**").permitAll()
                        .requestMatchers("/api/auth/oauth2/success").permitAll()
                        .requestMatchers("/api/v1/products/guest/**").permitAll()
                        .requestMatchers("/api/v1/cart/guest/add").permitAll()
                        .requestMatchers("/api/v1/cart/guest").permitAll()
                        .requestMatchers("/kafka/**").permitAll()
                        .requestMatchers("/restaurant/**").permitAll()
                        .requestMatchers("/api/orders/popular-products").permitAll()
                        .requestMatchers("/ingredients/**").permitAll()
                        .requestMatchers("/supplements/**").permitAll()
                        .requestMatchers("/menus/**").permitAll()
                        .requestMatchers("/categories/**").permitAll()
                        .requestMatchers("api/orders/most-sold-categories").permitAll()
                        .requestMatchers("/categories/top").permitAll()
                        .requestMatchers("/products/**").permitAll()
                        .requestMatchers("/search").permitAll()
                        .requestMatchers("/api/orders/test-update-status").permitAll()


                        .requestMatchers("/orders/**").permitAll()
                        .requestMatchers("/historique-refus/**").permitAll()


                        .requestMatchers("/ws/**", "/ws").permitAll()
                        .requestMatchers("/app/**").permitAll()
                        .requestMatchers("/topic/**").permitAll()

                        .requestMatchers("/kafka/**").permitAll()
                        .requestMatchers("/api/moderateur/**").permitAll()
                        .requestMatchers("/api/shifts/livreur/**").hasRole("LIVREUR")
                        .requestMatchers("/restaurant/**").permitAll()
                        .requestMatchers("/ingredients/**").permitAll()
                        .requestMatchers("/supplements/**").permitAll()
                        .requestMatchers( "/menus/**").permitAll()
                        .requestMatchers( "/categories/**").permitAll()
                        .requestMatchers( "/products/**").permitAll()
                        .requestMatchers( "/orders/**").permitAll()
                        .requestMatchers( "/historique-refus/**").permitAll()
                        .requestMatchers( "/api/orders/**").permitAll()
                        .requestMatchers( "/api/delivery/**").permitAll()
                        .requestMatchers( "/api/promotions/**").permitAll()
                        .requestMatchers( "/api/livreurs/**").permitAll()
                        .requestMatchers( "/api/**").permitAll()
                        .requestMatchers("/api/auth/firebase").permitAll()


                        .requestMatchers(
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/guest/cart/add").permitAll()
                        .requestMatchers("/api/guest/cart").permitAll()
                        .requestMatchers("/api/guest/cart/decrease/**").permitAll()
                        .requestMatchers("/api/guest/cart/remove/**").permitAll()
                        .requestMatchers("/api/guest/cart/clear").permitAll()

                        .anyRequest().authenticated()
                ) .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2LoginSuccessHandler));

        // Add JWT and banned user filters in correct order
        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(bannedUserFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}