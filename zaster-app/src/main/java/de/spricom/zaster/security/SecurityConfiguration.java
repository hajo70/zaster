package de.spricom.zaster.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    // The secret is stored in /config/secrets/application.properties by default.
    // Never commit the secret into version control; each environment should have
    // its own secret.
    @Value("${de.spricom.zaster.auth.secret}")
    private String authSecret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Icons from the line-awesome addon
        http.authorizeHttpRequests(reg ->
                reg.requestMatchers(
                        new AntPathRequestMatcher("/images/*.png"),
                        new AntPathRequestMatcher("/line-awesome/**.svg"),
                        new AntPathRequestMatcher("/h2-console/**")
                ).permitAll())
                .authorizeHttpRequests(reg ->
                        reg.requestMatchers(new AntPathRequestMatcher("/api/**"))
                                .hasRole("ADMIN"));

        http.csrf(reg ->
                reg.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")));
        http.headers(reg -> reg.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        super.configure(http);

        http.sessionManagement(mgmt -> mgmt.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        setLoginView(http, "/login");
        setStatelessAuthentication(http,
                new SecretKeySpec(Base64.getDecoder().decode(authSecret), JwsAlgorithms.HS256),
                "de.spricom.zaster");
    }

}
