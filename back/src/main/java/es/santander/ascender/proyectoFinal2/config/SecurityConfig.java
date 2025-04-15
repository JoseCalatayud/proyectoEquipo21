package es.santander.ascender.proyectoFinal2.config;

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
import org.springframework.web.cors.CorsConfiguration;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(List.of(
                            "https://appequipo02.z16.web.core.windows.net",
                            "http://localhost:4200"));
                    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    configuration.setAllowedHeaders(List.of("*"));
                    configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
                    configuration.setAllowCredentials(true);
                    configuration.setMaxAge(3600L); // Caché de preflight por 1 hora
                    return configuration;
                }))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso a Swagger UI y sus recursos
                        .requestMatchers("/v3/api-docs/**", "/v3/api-docs.yaml",
                                "/swagger-ui/**", "/swagger-ui.html",
                                "/swagger-resources/**", "/webjars/**",
                                "/api-docs/**")
                        .permitAll()
                        // API Auth
                        .requestMatchers("/api/auth/**").permitAll()
                        // Otras rutas...
                        .requestMatchers("/api/dummy").permitAll()
                        .requestMatchers("api/articulos/**").authenticated()
                        .requestMatchers("api/compras/**").hasRole("ADMIN")
                        .requestMatchers("api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("api/ventas/**").hasAnyRole("ADMIN", "USER")

                        .anyRequest().authenticated())

                // Reemplazar formLogin con httpBasic para API REST
                .httpBasic(withDefaults())
                // Establecer la política de sesiones como STATELESS (API REST) sin sesion.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}