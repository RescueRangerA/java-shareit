package ru.practicum.shareit.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.practicum.shareit.user.repository.UserRepository;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true
)
public class SecurityConfig {
    private final UserRepository userRepository;

    private final HandlerExceptionResolver resolver;

    @Autowired
    public SecurityConfig(UserRepository userRepository, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.userRepository = userRepository;
        this.resolver = resolver;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()

                .authorizeRequests()
                .antMatchers("/users", "/users/*").permitAll()

                .and()

                .addFilterAt(new TokenAuthenticationFilter(userRepository, resolver), BasicAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Required for filter excluding. Matchers do not do the job.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/users", "/users/*");
    }
}


