package it.uniroma3.siw.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import it.uniroma3.siw.security.CustomOAuth2UserService;
import it.uniroma3.siw.security.CustomOidcUserService;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;
import static it.uniroma3.siw.model.Credentials.GIOCATORE_ROLE;
import static it.uniroma3.siw.model.Credentials.MASTER_ROLE;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
            .dataSource(dataSource)
            // PostgreSQL: concatena 'ROLE_' al valore di role in tabella
            .authoritiesByUsernameQuery(
                "SELECT username, 'ROLE_' || role AS authority " +
                "FROM credentials WHERE username = ?"
            )
            .usersByUsernameQuery(
                "SELECT username, password, 1 AS enabled " +
                "FROM credentials WHERE username = ?"
            );
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http,
                                            CustomOAuth2UserService oauth2Svc,
                                            CustomOidcUserService oidcSvc) throws Exception {
        http
          .cors(cors -> cors.disable())
          .authorizeHttpRequests(req -> req
              .requestMatchers(HttpMethod.GET, "/", "/index", "/register", "/login",
                               "/css/**", "/images/**", "/js/**", "/webjars/**", "/uploads/**")
                .permitAll()
              .requestMatchers(HttpMethod.POST, "/register", "/login")
                .permitAll()
              .requestMatchers("/admin/**")
                .hasRole(ADMIN_ROLE)
              .requestMatchers("/logged/giocatore/**")
                .hasRole(GIOCATORE_ROLE)
              .requestMatchers("/logged/master/**")
                .hasRole(MASTER_ROLE)
              .requestMatchers("/logged/**")
                .authenticated()
              .anyRequest()
                .authenticated()
          )
          .formLogin(form -> form
              .loginPage("/login")
              .defaultSuccessUrl("/success", true)
              .failureUrl("/login?error=true")
              .permitAll()
          )
          .logout(logout -> logout
              .logoutUrl("/logout")
              .logoutSuccessUrl("/")
              .invalidateHttpSession(true)
              .clearAuthentication(true)
              .deleteCookies("JSESSIONID", "remember-me")
              .permitAll()
          )
          .oauth2Login(oauth2 -> oauth2
              .loginPage("/login")
              .userInfoEndpoint(endpoints -> endpoints
                  .userService(oauth2Svc)
                  .oidcUserService(oidcSvc)
              )
              .defaultSuccessUrl("/success", true)
          )
          .sessionManagement(session -> session
              .sessionFixation(fix -> fix.migrateSession())
          );

        return http.build();
    }
}
