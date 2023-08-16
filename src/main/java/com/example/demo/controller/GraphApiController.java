package com.example.demo.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.DeviceCodeInfo;
import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Location;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;

import okhttp3.Request;

@RestController
public class GraphApiController {

	private static Properties _properties;
	private static DeviceCodeCredential _deviceCodeCredential;
	private static UsernamePasswordCredential credential;
	private static GraphServiceClient<Request> _userClient;
	
	@PostMapping("/create")
	public void createGraphApi() {
		System.out.println("Create Graph Api");
		System.out.println();
		
		final Properties oAuthProperties = new Properties();
	    try {
	        
	    	FileInputStream fileInputStream = new FileInputStream(Thread.currentThread().getContextClassLoader().getResource("")
	    			.getPath() + "graphapi.properties");
	    	
	    	oAuthProperties.load(fileInputStream);
	    	
	    	
	    } catch (IOException e) {
	        System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
	        return;
	    }

	    initializeGraph(oAuthProperties);
	    
	    greetUser();
	    
	    displayAccessToken();
	    
	    createCld();

	}
	
	private static void initializeGraphForUserAuth(Properties properties, Consumer<DeviceCodeInfo> challenge) throws Exception {
		// Ensure properties isn't null
	    if (properties == null) {
	        throw new Exception("Properties cannot be null");
	    }

	    _properties = properties;

	    final String clientId = _properties.getProperty("graph.clientId");
	    final String tenantId = _properties.getProperty("graph.tenantId");
	    final List<String> graphUserScopes = Arrays
	        .asList(_properties.getProperty("graph.graphUserScopes").split(","));
	    String clientSecret = "YOUR_CLIENT_SECRET";

	    System.out.println(clientId);
	    System.out.println(tenantId);
	    System.out.println(graphUserScopes);
	    final String userName = "kang.in@rci-a.com";
	    final String password = "dlsdlwkd12.";
	    
	    System.out.println("11111111111111111111111");
	    
	    _deviceCodeCredential = new DeviceCodeCredentialBuilder()
	        .clientId(clientId)
	        .tenantId(tenantId)
	        .challengeConsumer(challenge)
	        .build();

	    System.out.println("2222222222222222222222222222222");
	    
	    credential = new UsernamePasswordCredentialBuilder()
	    	    .clientId(clientId).tenantId(tenantId).username(userName).password(password)
	    	    .build();
	    
	    System.out.println("333333333333333333333333333333");
	    
	    final TokenCredentialAuthProvider authProvider =
	        new TokenCredentialAuthProvider(graphUserScopes, credential);

	    System.out.println("444444444444444444444444444444444");
	    
	    final GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
	    	    .authenticationProvider(authProvider).buildClient();
	    
	    
	    System.out.println("5555555555555555555555555555555");
	    
	    final User user = graphClient.me()
	            .buildRequest()
	            .select("displayName")
	            .get();
	    
	    System.out.println(user);
	    
//	    GraphServiceClient<Request> userClient = GraphServiceClient.builder()
//		        .authenticationProvider(authProvider)
//		        .buildClient();
	    
	    _userClient = GraphServiceClient.builder()
	        .authenticationProvider(authProvider)
	        .buildClient();
	    
	    System.out.println("666666666");
	}
	
	private static void initializeGraph(Properties properties) {
	    try {
	        initializeGraphForUserAuth(properties,
	            challenge -> System.out.println(challenge.getMessage()));
	    } catch (Exception e)
	    {
	        System.out.println("Error initializing Graph for user auth");
	        System.out.println(e.getMessage());
	    }
	}
	
	public static String getUserToken() throws Exception {
	    // Ensure credential isn't null
	    if (_deviceCodeCredential == null) {
	        throw new Exception("Graph has not been initialized for user auth");
	    }

	    final String[] graphUserScopes = _properties.getProperty("graph.graphUserScopes").split(",");

	    final TokenRequestContext context = new TokenRequestContext();
	    context.addScopes(graphUserScopes);

	    final AccessToken token = credential.getToken(context).block();
	    
	    return token.getToken();
	}
	
	private static void displayAccessToken() {
	    try {
	        final String accessToken = getUserToken();
	        System.out.println("Access token: " + accessToken);
	    } catch (Exception e) {
	        System.out.println("Error getting access token");
	        System.out.println(e.getMessage());
	    }
	}
	
	public static User getUser() throws Exception {
	    // Ensure client isn't null
	    if (_userClient == null) {
	        throw new Exception("Graph has not been initialized for user auth");
	    }

	    return _userClient.me()
	        .buildRequest()
	        .select("displayName")
	        .get();
	}
	
	private static void greetUser() {
	    try {
	        final User user = getUser();
	        // For Work/school accounts, email is in mail property
	        // Personal accounts, email is in userPrincipalName
	        final String email = user.mail == null ? user.userPrincipalName : user.mail;
	        System.out.println("Hello, " + user.displayName + "!");
	        System.out.println("Email: " + email);
	    } catch (Exception e) {
	        System.out.println("Error getting user");
	        System.out.println(e.getMessage());
	    }
	}
	
	private static void createCld() {
		
		final List<String> graphUserScopes = Arrays
		        .asList(_properties.getProperty("graph.graphUserScopes").split(","));
		
		final TokenCredentialAuthProvider authProvider =
		        new TokenCredentialAuthProvider(graphUserScopes, credential);

	    System.out.println("createCld 444444444444444444444444444444444");
	    
	    final GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
	    	    .authenticationProvider(authProvider).buildClient();
	    
	    
	    Event event = new Event();
	    event.subject = "Let's go for lunch";
	    ItemBody body = new ItemBody();
	    body.contentType = BodyType.HTML;
	    body.content = "Does late morning work for you?";
	    event.body = body;
	    DateTimeTimeZone start = new DateTimeTimeZone();
	    start.dateTime = "2023-08-16T12:00:00";
	    start.timeZone = "Tokyo Standard Time";
	    event.start = start;
	    DateTimeTimeZone end = new DateTimeTimeZone();
	    end.dateTime = "2023-08-16T13:00:00";
	    end.timeZone = "Tokyo Standard Time";
	    event.end = end;
	    Location location = new Location();
	    location.displayName = "";
	    event.location = location;
	    
	    graphClient.me().events().buildRequest().post(event);
	}
	
}
