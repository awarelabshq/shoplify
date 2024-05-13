package org.shoplify.user;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("**/*", "/user/*", "/datagen/*", "/data/*", "/guards/*", "/admin/*", "/collectors/*", "/sessions/*", "/trackedtests/*", "/user/get_info", "/user/get_user_project_config", "/admin/get_project_config", "/admin/list_users_for_project", "/admin/list_projects", "/capacity/*", "/health/check", "/admin/add_user", "/admin/get_organization_by_domain")
                .permitAll();
    }

}
