package ar.utn.ba.ddsi.GestionMetaMapa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Hacemos públicas las rutas de login y los recursos estáticos
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                        // Todas las demás rutas requieren que el usuario esté autenticado
                        .anyRequest().authenticated()
                )
                // Ahora Spring ya no interceptará el POST a /login.
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                );

        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}





/*package ar.utn.ba.ddsi.GestionMetaMapa.config;

import ar.utn.ba.ddsi.GestionMetaMapa.providers.CustomAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

//@EnableMethodSecurity(prePostEnabled = true) Al comentarla, el SecurityConfig seguirá manejando la autenticación (saber si estás logueado o no), pero ignorará por completo las reglas de
//     @PreAuthorize("hasAnyRole(...)") en los controladores.
@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, CustomAuthProvider provider) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider)
                .build();
    }

         Comentar provisoriamente para probar sin autenticacion
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Recursos estáticos y login público
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                        // Ejemplo: Acceso a alumnos: ADMIN y DOCENTE
                        //.requestMatchers("/alumnos/**").hasAnyRole("ADMIN", "DOCENTE")
                        // Lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")    // tu template de login
                        .permitAll()
                        .defaultSuccessUrl("/alumnos", true) // redirigir tras login exitoso
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // redirigir tras logout
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        // Usuario no autenticado → redirigir a login
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login?unauthorized")
                        )
                        // Usuario autenticado pero sin permisos → redirigir a página de error
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect("/403")
                        )
                );

        return http.build();
    }

}*/