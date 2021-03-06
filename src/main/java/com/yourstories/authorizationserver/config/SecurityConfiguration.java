/*
package com.yourstories.authorizationserver.config;

import com.yourstories.authorizationserver.services.DefaultUserService;
import com.yourstories.authorizationserver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true, order = 0, mode = AdviceMode.PROXY,
        proxyTargetClass = false
)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
    @Autowired
    DefaultUserService defaultUserService;

    @Bean
    protected SessionRegistry sessionRegistryImpl()
    {
        return new SessionRegistryImpl();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder)
            throws Exception
    {
        builder
                .userDetailsService(this.defaultUserService)
                        .passwordEncoder(new BCryptPasswordEncoder())
                .and()
                .eraseCredentials(true);
    }

    @Override
    public void configure(WebSecurity security)
    {
        security.ignoring().antMatchers("/resource*/
/**", "/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception
    {
        security
                .authorizeRequests().antMatchers("/oauth/token/revokeById*").permitAll()
                .antMatchers("/tokens*").permitAll()
                .antMatchers("/swagger*").permitAll()
                .antMatchers("/api/v1/user/register").permitAll()
                .anyRequest().authenticated()
                .and().formLogin().permitAll()
                .and().csrf().disable();;
                    */
/*.antMatchers("/session/list")
                        .hasAuthority("VIEW_USER_SESSIONS")
                    .anyRequest().authenticated()
                .and().formLogin()
                    .loginPage("/login").failureUrl("/login?loginFailed")
                    .defaultSuccessUrl("/ticket/list")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .permitAll()
                .and().logout()
                    .logoutUrl("/logout").logoutSuccessUrl("/login?loggedOut")
                    .invalidateHttpSession(true).deleteCookies("JSESSIONID")
                    .permitAll()
                .and().sessionManagement()
                    .sessionFixation().changeSessionId()
                    .maximumSessions(1).maxSessionsPreventsLogin(true)
                    .sessionRegistry(this.sessionRegistryImpl())
                .and().and().csrf()
                    .requireCsrfProtectionMatcher((r) -> {
                        String m = r.getMethod();
                        return !r.getServletPath().startsWith("/services/") &&
                                ("POST".equals(m) || "PUT".equals(m) ||
                                        "DELETE".equals(m) || "PATCH".equals(m));
                    });*//*

    }
}
*/
