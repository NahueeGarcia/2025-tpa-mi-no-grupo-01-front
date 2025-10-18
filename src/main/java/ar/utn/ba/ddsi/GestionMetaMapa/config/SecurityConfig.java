package ar.utn.ba.ddsi.GestionMetaMapa.config;

import ar.utn.ba.ddsi.GestionMetaMapa.providers.CustomAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración central de Spring Security.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true) // Habilita el uso de @PreAuthorize en los controladores
public class SecurityConfig {

    /**
     * Expone el AuthenticationManager como un Bean para que pueda ser inyectado en otros
     componentes,
     * como el LoginService para el login programático.
     * Conecta nuestro CustomAuthProvider con el mecanismo de autenticación de Spring.
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http, CustomAuthProvider
            customAuthProvider) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(customAuthProvider)
                .build();
    }

    /**
     * Define la cadena de filtros de seguridad que protege las rutas de la aplicación.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // --- RUTAS PÚBLICAS ---
                        // Se definen las URLs que no requieren ningún tipo de autenticación.
                        .requestMatchers("/", "/login", "/css/**", "/js/**",
                                "/metamapa/hechos", "/metamapa/colecciones", "/dashboard").permitAll()
                        // Todas las demás rutas (ej. /dashboard, /admin/panel) requieren autenticación.
                        .anyRequest().authenticated()
                )
                // --- FORMULARIO DE LOGIN ---
                // Se le delega a Spring Security el manejo del formulario de login.
                .formLogin(form -> form
                        .loginPage("/login") // URL de nuestra página de login personalizada.
                        .permitAll()
                        .defaultSuccessUrl("/dashboard", true) // A dónde ir tras un login exitoso.
                        .failureUrl("/login?error=true") // A dónde ir si el login falla.
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para activar el cierre de sesión.
                        .logoutSuccessUrl("/") // Al cerrar sesión, redirigir a la landing page.
                        .invalidateHttpSession(true) // Invalida la sesión HTTP.
                        .deleteCookies("JSESSIONID") // Borra la cookie de sesión del navegador.
                );

        // Se deshabilita CSRF para simplificar la interacción con la API.
        // En un entorno de producción, se requeriría una estrategia de manejo de CSRF.
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
