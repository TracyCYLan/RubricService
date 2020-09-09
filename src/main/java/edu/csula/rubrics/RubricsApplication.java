package edu.csula.rubrics;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class RubricsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RubricsApplication.class, args);
	}

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("edu.csula.rubrics")).build();
	}

	@EnableWebSecurity
	@Configuration
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.cors().and().authorizeRequests()
			.antMatchers(HttpMethod.GET, "/rubric/**").permitAll()
			.antMatchers(HttpMethod.GET, "/**").hasAuthority("rubric-service")
			.antMatchers(HttpMethod.POST, "/**").hasAuthority("rubric-service")
			.antMatchers(HttpMethod.PUT, "/**").hasAuthority("rubric-service")
			.antMatchers(HttpMethod.PATCH, "/**").hasAuthority("rubric-service")
			.antMatchers(HttpMethod.DELETE, "/**").hasAuthority("rubric-service")
			.anyRequest().authenticated()
			.and().oauth2ResourceServer().jwt()
			.jwtAuthenticationConverter(new JwtAuthenticationConverter()
            {
                @Override
                protected Collection<GrantedAuthority> extractAuthorities(final Jwt jwt)
                {
                    Collection<GrantedAuthority> authorities = super.extractAuthorities(jwt);
                    List<String> scopes= jwt.getClaimAsStringList("scope");
                    if(scopes!=null && scopes.contains("rubric-service"))//rubric-service
                    {
                    	authorities.add(new SimpleGrantedAuthority("rubric-service" ));
                    }
                    return authorities;
                }
            });

		}
	
		

		// solve the error: has been blocked by CORS policy
		@Bean
		CorsConfigurationSource corsConfigurationSource() {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.setAllowCredentials(true);
			configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin",
					"Access-Control-Request-Method", "Access-Control-Request-Headers", "Origin", "Cache-Control",
					"Content-Type", "Authorization"));
//			configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000","https://alice.cysun.org"));
			configuration.setAllowedOrigins(Arrays.asList("*"));
			configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", configuration);
			return source;
		}

	}
}
