
package user_registration_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;    // ← import
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity    // ← force Spring to use this class as THE security configuration
public class SecurityConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          // 1) turn off CSRF (so curl/Postman can POST without a token)
          .csrf(AbstractHttpConfigurer::disable)

          // 2) make sure this is the *first* rule evaluated
          .authorizeHttpRequests(auth -> auth
              // open ANY method on /api/register
              .requestMatchers("/api/register").permitAll()
              // everything else needs a login
              .anyRequest().authenticated()
          )

          // 3) leave HTTP Basic on for anything else
          .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
