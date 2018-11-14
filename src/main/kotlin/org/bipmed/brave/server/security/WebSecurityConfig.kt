package org.bipmed.brave.server.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .antMatchers("/search").permitAll()
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and().formLogin().disable()
                .logout().disable()
                .csrf().disable()
                .httpBasic()
    }

}