package edu.csula.rubrics;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

//import edu.csula.rubrics.jwt.filter.JWTAuthorizationFilter;
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

//	@EnableWebSecurity
//	@Configuration
//	class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//		// so far, allow user login, view rubric and criterion under it.
//		@Override
//		protected void configure(HttpSecurity http) throws Exception {
//			http.cors().and().csrf().disable()
//					.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
//					.authorizeRequests()
//					.antMatchers(HttpMethod.GET,"/**").permitAll()
//					.antMatchers(HttpMethod.POST,"/**").permitAll()
//					.antMatchers(HttpMethod.PUT,"/**").permitAll()
//					.antMatchers(HttpMethod.PATCH,"/rubric/**").permitAll()
//					.antMatchers(HttpMethod.DELETE,"/**").permitAll()
////					.antMatchers(HttpMethod.POST, "/user/login").permitAll()
////					.antMatchers(HttpMethod.POST, "/user/register").permitAll()
////					.antMatchers(HttpMethod.GET, "/user/**").permitAll()
////					.antMatchers(HttpMethod.GET, "/rubric/**").permitAll()
//					.anyRequest().authenticated();
//		}
//
//		// solve the error: has been blocked by CORS policy
//		@Bean
//		CorsConfigurationSource corsConfigurationSource() {
//			CorsConfiguration configuration = new CorsConfiguration();
//			configuration.setAllowCredentials(true);
//			configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers","Access-Control-Allow-Origin","Access-Control-Request-Method", "Access-Control-Request-Headers","Origin","Cache-Control", "Content-Type", "Authorization"));
//			configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000","https://alice.cysun.org"));
//			configuration.setAllowedMethods(Arrays.asList("GET", "POST","PUT","PATCH","DELETE"));
//			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//			source.registerCorsConfiguration("/**", configuration);
//			return source;
//		}
//
//	}
}
