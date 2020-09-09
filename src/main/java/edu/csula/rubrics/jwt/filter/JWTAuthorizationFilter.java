package edu.csula.rubrics.jwt.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;


public class JWTAuthorizationFilter extends OncePerRequestFilter {

	private final String HEADER = "access_token";
	private String PUBLICKEY = "";
	
	public JWTAuthorizationFilter() throws MalformedURLException {
		String oidcURL = "https://identity.cysun.org/.well-known/openid-configuration/jwks";
		URL urlForGetRequest = new URL(oidcURL);
		String readLine = null;
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) urlForGetRequest.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestMethod("GET");
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer response = new StringBuffer();
				while ((readLine = in.readLine()) != null) {
					response.append(readLine);
				}
				in.close();
				String result = response.toString();
				JSONParser parser = new JSONParser(); 
				JSONObject obj = (JSONObject)parser.parse(result);
				JSONArray key_arr = (JSONArray) obj.get("keys");
				PUBLICKEY = key_arr.get(0).toString();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		try {
			if (checkJWTToken(request, response)) {
				Claims claims = validateToken(request);
				if (claims.get("authorities") != null) {
					setUpSpringAuthentication(claims);
				} else {
					SecurityContextHolder.clearContext();
				}
			}
			chain.doFilter(request, response);
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return;
		}
	}	

	private Claims validateToken(HttpServletRequest request) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
		String jwtToken = request.getHeader(HEADER);
		System.out.println("token: "+jwtToken);
		System.out.println("public key: "+PUBLICKEY);
		//something wrong below
		Claims claims = Jwts.parser().setSigningKey(getPublicKey(PUBLICKEY.getBytes())).parseClaimsJws(jwtToken).getBody();
		System.out.println("show claims");
		for(String s: claims.keySet())
		{
			System.out.println(s+": "+claims.get(s));
		}
		return claims;
	}
	private PublicKey getPublicKey(byte[] encodedKey) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
	    KeyFactory factory = KeyFactory.getInstance("RSA");
	    X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(encodedKey);
	    return factory.generatePublic(encodedKeySpec);
	}

	/**
	 * Authentication method in Spring flow
	 * 
	 * @param claims
	 */
	private void setUpSpringAuthentication(Claims claims) {
		@SuppressWarnings("unchecked")
		List<String> authorities = (List<String>) claims.get("authorities");

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
				authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		SecurityContextHolder.getContext().setAuthentication(auth);

	}

	private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse res) {
		String authenticationHeader = request.getHeader(HEADER);
		System.out.println("token is "+ authenticationHeader);
		if (authenticationHeader == null)
			return false;
		return true;
	}

}