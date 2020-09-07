package edu.csula.rubrics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

@Component("OIDCSecurity")
public class OIDCSecurity {

	private String readProp(String name) {
		String url = "";
		try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
			Properties prop = new Properties();
			prop.load(input);
			url = prop.getProperty(name);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return url;
	}
	public boolean verify(HttpServletRequest request) throws MalformedURLException {
		String token = request.getHeader("access_token");
		if (token == null || token.length() == 0)
			return false;
		List<String> res = new ArrayList<>();
		String oidcURL = readProp("oidc-server.url");
		URL urlForGetRequest = new URL(oidcURL);
		String readLine = null;
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) urlForGetRequest.openConnection();
			connection.setRequestProperty("Authorization", "Bearer " + token);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestMethod("GET");
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer response = new StringBuffer();
				while ((readLine = in.readLine()) != null) {
					response.append(readLine);
				}
				in.close();
				res.add(response.toString());
				System.out.println("oidc-client get: " + response.toString());//get a JSONObject
			}
			else
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;

	}
}