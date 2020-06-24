package edu.csula.rubrics.canvas;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/canvasRest")
public class CanvasRestController {

	@GetMapping("/access_token")
	public String canvasToken(@RequestParam String code) {
		return "I'm token";
//		//get developer key id and secret
//		String dkID = "";
//		String dkKey = "";
//		try (InputStream input = new FileInputStream("src/main/resources/developer_key.properties")) {
//			Properties prop = new Properties();
//			prop.load(input);
//			dkID = prop.getProperty("id");
//			dkKey = prop.getProperty("key");
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//		if (dkID.length() == 0 || dkKey.length() == 0)
//			return "";
//		
//		//call Canvas POST API to get Token
//		String token = "";
//		try {
//			URL url = new URL("https://calstatela.instructure.com:443/login/oauth2/token");
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//			conn.setRequestMethod("POST");
//			conn.setRequestProperty("Content-Type", "application/json");
//			conn.setDoOutput(true);
//			
//			String jsonInputString = "{ \"grant_type\": \"authorization_code\"," + " \"client_id\":" + dkID + ","
//					+ " \"client_secret\": \"" + dkKey + "\","
//					+ " \"redirect_uri\": \"http://ecst-csproj2.calstatela.edu:6350/alice-rubrics/canvas/oauth_callback\","
//					+ " \"code\": \"" + code + "\"" + " } ";
//			
//			OutputStream os = conn.getOutputStream();
//			os.write(jsonInputString.getBytes());
//			os.flush();
//			
//			//if failed, (like some parameter value is invalid)
//			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
//				throw new RuntimeException("Failed : HTTP error code : "
//					+ conn.getResponseCode());
//			}
//			
//			BufferedReader br = new BufferedReader(new InputStreamReader(
//					(conn.getInputStream())));
//
//			//in the future, we should edit this one, found "access_token" and assign the value into token
//			String output;
//			while ((output = br.readLine()) != null) {
//				System.out.println(output); 
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return token;
	}

}
