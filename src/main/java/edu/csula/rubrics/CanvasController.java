package edu.csula.rubrics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Controller
@RequestMapping("/canvas")
public class CanvasController {
	
	@GetMapping("/hello")
	public String hello(Model model, @RequestParam(value = "name", required = false, defaultValue = "World") String name) {
		model.addAttribute("name", name);
		return "hello";
	}
	
	//return userid if already login
	@GetMapping("/loginStatus")
	public String loginStatus(Model model, @RequestParam(value = "token", defaultValue="" )String jwt) {
		//jwt is the token generated by RubricService
		System.out.println("get token: "+jwt);

		//call canvas API.
		if(jwt.length()==0)
			return "nothing";
		
		try {
			Jws<Claims> claims = Jwts.parser()
			  .setSigningKey("secret".getBytes("UTF-8"))
			  .parseClaimsJws(jwt);
			String username = claims.getBody().get("username").toString();
			return claims.getBody().getId();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		

		
		
		return "nothing";
		
	}
}