package com.doremi.booking.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.doremi.booking.Jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http
            .csrf(csrf -> 
                csrf
                .disable())
            .authorizeHttpRequests((authz) -> authz
                .requestMatchers("/auth/**",
                "/categoria/listar" , "/instrumentos/listaraleatorios", 
                "/instrumentos/buscarPorKeyWord/**","/instrumentos/buscarPorNombre/**",
                 "/instrumentos/buscarPorId/**", "reservas/listar", "/usuario/buscarPorId/**").permitAll()
                .requestMatchers("/accion/**","/categoria/agregar","/categoria/eliminar/**",
                 "/instrumentos/agregar", "/instrumentos/eliminar/**", "/instrumentos/modificar",
                 "/instrumentos/listar","/usuario/listar","/usuario/cambioRole").hasAuthority("ADMIN")
                 .requestMatchers("/usuario/buscarPorUsername/**").hasAnyAuthority("ADMIN", "USER")
                            .requestMatchers("/reservas/agregar").hasAuthority("USER")
                .anyRequest().authenticated()
                )
            .sessionManagement(sessionManager->
                sessionManager 
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
            
            
    }

}
