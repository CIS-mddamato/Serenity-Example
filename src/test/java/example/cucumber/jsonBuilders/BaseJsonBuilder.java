package example.cucumber.jsonBuilders;

import groovy.json.JsonBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.hamcrest.text.IsEqualIgnoringCase;
import com.jayway.restassured.RestAssured;
import static com.jayway.restassured.RestAssured.baseURI;
import static example.utils.Verify.verifyThat;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import example.utils.JsonUtility;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import wslite.json.JSONException;
import wslite.json.JSONObject;
import static net.serenitybdd.rest.SerenityRest.rest;
import static org.hamcrest.Matchers.*;

/** @author mddamato */
public class BaseJsonBuilder extends ScenarioSteps {
	protected static final long serialVersionUID = -4511681877439841279L;
	protected static final String privUserForExternalAuthorization = "Basic bXl1c2Npc19zeXNfcHJpdl91c3I6MTIzQjBja0Rvb3JSdWxleiEhIQ==";
	protected static final String regUserForExternalAuthorization = "Basic bXl1c2Npc19zeXNfcmVnX3VzcjoxMjNCMGNrRG9vclJ1bGV6ISEh";
	protected static final String springSecurityCheckEndpoint = "/j_spring_security_check";
	protected static final String internalUsername = "admin";
	protected static final String internalPassword = "admin";
	protected static final String requestContentType = "application/json";
	public String currentUser = privUserForExternalAuthorization;
	protected static Response response;
	protected static String sessionId;
	protected static RequestSpecification requestSpecification;
	private Properties serenityProperties;
	private static boolean externallyConnected = false;

	public String getInternalUsername() {
		return internalUsername;
	}

	public String getInternalPassword() {
		return internalPassword;
	}

	public String getSpringSecurityCheckEndpoint() {
		return springSecurityCheckEndpoint;
	}

	/* Internal Requests */
	@Step
	public void createInternalConnectionToInfopass() {
		loadSerenityProperties();
		RestAssured.baseURI = getInfopassUrl();
		requestSpecification = rest().contentType("application/x-www-form-urlencoded")
				.param("username", internalUsername).param("password", internalPassword);
		response = requestSpecification.post(springSecurityCheckEndpoint);
		sessionId = response.getSessionId();
		RestAssured.sessionId = sessionId;
		sessionId(sessionId);
		Serenity.setSessionVariable("sessionId").to(sessionId);
		assertThat(response.getStatusCode())
				.overridingErrorMessage("Status code for creating the connection was incorrect ").isEqualTo(302);
	}

	@Step
	public void createInternalConnectionToInfopass(String username) {
		loadSerenityProperties();
		RestAssured.baseURI = getInfopassUrl();
		requestSpecification = rest().contentType("application/x-www-form-urlencoded").param("username", username)
				.param("password", username);
		response = requestSpecification.post(springSecurityCheckEndpoint);
		sessionId = response.getSessionId();
		RestAssured.sessionId = sessionId;
		sessionId(sessionId);
		Serenity.setSessionVariable("sessionId").to(sessionId);
		assertThat(response.getStatusCode())
				.overridingErrorMessage("Status code for creating the connection was incorrect ").isEqualTo(302);
	}

	@Step
	public void createExternalConnectionToInfopass() {
		if (!externallyConnected) {
			loadSerenityProperties();
			RestAssured.baseURI = getInfopassUrl();
			currentUser = regUserForExternalAuthorization;
			externallyConnected = true;
		}

	}

	@Step
	public void createExternalConnectionToInfopass(String userType) {
		if (!externallyConnected) {
			loadSerenityProperties();
			RestAssured.baseURI = getInfopassUrl();
			switch (userType) {
			case "priv":
				currentUser = privUserForExternalAuthorization;
				break;
			case "privileged":
				currentUser = privUserForExternalAuthorization;
				break;
			case "reg":
				currentUser = regUserForExternalAuthorization;
				break;
			case "regular":
				currentUser = regUserForExternalAuthorization;
				break;
			default:
				System.out.println("User not specific correctly: " + userType);
				verifyThat("User not specific correctly: " + userType, true, is(false));
				break;
			}
			externallyConnected = true;
		}
	}

	@Step
	public void logOutFromInfopass() {
		System.out.println("Logging out: " + RestAssured.baseURI + "/logout");
		requestSpecification = rest().header("Accept-Encoding", requestContentType).cookie("JSESSIONID",
				Serenity.sessionVariableCalled("sessionId").toString());
		response = requestSpecification.get("/logout");
		verifyThat("Status code for logging out was incorrect ", response.getStatusCode(), is(200));
		sessionId = "";
	}

	public String performGetInternalRequest(String endpointUrl) {
	   
		if (!internallyConnected())
			createInternalConnectionToInfopass();
		System.out.println("Performing internal GET with endpoint: " + endpointUrl);
		sessionId = Serenity.sessionVariableCalled("sessionId").toString();
		printIncomingJSONConsoleBorder();
		requestSpecification = rest().header("Accept-Encoding", requestContentType).cookie("JSESSIONID", sessionId);
		response = requestSpecification.get(endpointUrl);
		printLowerBorder();
		verifyThat("Status code for internal GET request was incorrect ", response.getStatusCode(), is(200));
		return response.getBody().asString();
	}

	/** if you do not want the output to be printed to console !print
	 * 
	 * @param endpointUrl
	 * @param print */
	public void performGetInternalRequest(String endpointUrl, boolean print) {
		if (!internallyConnected())
			createInternalConnectionToInfopass();
		if (print) {
			System.out.println("Performing internal GET with endpoint: " + endpointUrl);
			sessionId = Serenity.sessionVariableCalled("sessionId").toString();
			printIncomingJSONConsoleBorder();
			requestSpecification = rest().header("Accept-Encoding", requestContentType).cookie("JSESSIONID", sessionId);
			response = requestSpecification.get(endpointUrl);
			printLowerBorder();
			verifyThat("Status code for internal GET request was incorrect ", response.getStatusCode(), is(200));
		} else if (!print) {
			sessionId = Serenity.sessionVariableCalled("sessionId").toString();
			requestSpecification = rest().header("Accept-Encoding", requestContentType).cookie("JSESSIONID", sessionId)
					.log().path();
			response = requestSpecification.get(endpointUrl);
			verifyThat("Status code for internal GET request was incorrect ", response.getStatusCode(), is(200));
		}

	}

	public void performDeleteInternalRequest(String endpointUrl) {
		if (!internallyConnected())
			createInternalConnectionToInfopass();
		System.out.println("Performing internal DELETE with endpoint: " + endpointUrl);
		sessionId = Serenity.sessionVariableCalled("sessionId").toString();
		printIncomingJSONConsoleBorder();
		requestSpecification = rest().cookie("JSESSIONID", sessionId).contentType(requestContentType);
		response = requestSpecification.delete(endpointUrl);
		printLowerBorder();
		verifyThat("Status code for internal DELETE request was incorrect ", response.getStatusCode(), is(200));
	}

	public void performPostInternalRequest(String endpointUrl, String requestBody) {
		if (!internallyConnected())
			createInternalConnectionToInfopass();
		System.out.println("Performing internal POST with endpoint: " + endpointUrl);
		sessionId = Serenity.sessionVariableCalled("sessionId").toString();
		response = rest().cookie("JSESSIONID", sessionId).contentType(requestContentType).content(requestBody)
				.post(endpointUrl);
		verifyThat("Status code for internal POST request was incorrect ", response.getStatusCode(), is(200));
	}

	/** Checks for status code 200
	 * 
	 * @param endpointUrl
	 * @param jsonObject */
	public String performPutInternalRequest(String endpointUrl, JsonObject jsonObject) {
		if (!internallyConnected())
			try {
				createInternalConnectionToInfopass();
			} catch (Exception e) {
				System.out.println("Unable to connect to Info pass trying again /n" + e.getMessage());
				createInternalConnectionToInfopass();
			}
		System.out.println("Performing internal PUT with endpoint: " + endpointUrl);
		sessionId = Serenity.sessionVariableCalled("sessionId").toString();
		requestSpecification = rest().cookie("JSESSIONID", sessionId).contentType(requestContentType)
				.content(jsonObject.toString());
		printOutgoingAndIncomingJSONConsoleBorder(jsonObject);
		response = requestSpecification.put(endpointUrl);
		printLowerBorder();
		verifyThat("Status code for internal PUT request was incorrect. ***Endpoint URL: " + endpointUrl
				+ " ***Request Body:\n" + jsonObject.toString(), response.getStatusCode(), is(200));
		return response.getBody().asString();
	}
	
	
	/** Checks for status code 200
	 * 
	 * @param endpointUrl
	 * @param jsonObject */
	/*public String performPutExternalRequest(String endpointUrl, JsonObject jsonObject) {
		if (!externallyConnected())
			try {
				createExternalConnectionToInfopass();
			} catch (Exception e) {
				System.out.println("Unable to connect to Info pass trying again /n" + e.getMessage());
				createInternalConnectionToInfopass();
			}
		System.out.println("Performing external PUT with endpoint: " + endpointUrl);
		sessionId = Serenity.sessionVariableCalled("sessionId").toString();
		requestSpecification = rest().cookie("JSESSIONID", sessionId).contentType(requestContentType)
				.content(jsonObject.toString());
		printOutgoingAndIncomingJSONConsoleBorder(jsonObject);
		response = requestSpecification.put(endpointUrl);
		printLowerBorder();
		verifyThat("Status code for external PUT request was incorrect. ***Endpoint URL: " + endpointUrl
				+ " ***Request Body:\n" + jsonObject.toString(), response.getStatusCode(), is(200));
		return response.getBody().asString();
	}*/
	
	
	public String performPutExternalRequest(String endpointUrl, JsonObject jsonObject) {
		System.out.println("Performing external PUT with endpoint: " + endpointUrl);
		printOutgoingAndIncomingJSONConsoleBorder(jsonObject);
		response = rest().header("Accept-Encoding", requestContentType).header("Authorization", currentUser)
				.contentType(requestContentType).content(jsonObject.toString()).put(endpointUrl);
		verifyThat("Status code for external PUT request was incorrect ", response.getStatusCode(), is(200));
		return response.getBody().asString();
	}

	/** Checks for status code 200
	 * 
	 * @param endpointUrl
	 * @param jsonObject */
	public void performPutInternalRequest(String endpointUrl, JsonObject jsonObject, boolean print) {
		if (!internallyConnected())
			createInternalConnectionToInfopass();
		if (print) {
			System.out.println("Performing internal PUT with endpoint: " + endpointUrl);
			sessionId = Serenity.sessionVariableCalled("sessionId").toString();
			requestSpecification = rest().cookie("JSESSIONID", sessionId).contentType(requestContentType)
					.content(jsonObject.toString());
			printOutgoingAndIncomingJSONConsoleBorder(jsonObject);
			response = requestSpecification.put(endpointUrl);
			printLowerBorder();
			verifyThat("Status code for internal PUT request was incorrect ", response.getStatusCode(), is(200));
		}
		if (!print) {
			sessionId = Serenity.sessionVariableCalled("sessionId").toString();
			requestSpecification = rest().cookie("JSESSIONID", sessionId).contentType(requestContentType)
					.content(jsonObject.toString()).log().path();
			;
			response = requestSpecification.put(endpointUrl);
			verifyThat("Status code for internal PUT request was incorrect ", response.getStatusCode(), is(200));
		}
	}

	/** Checks for status code 200
	 * 
	 * @param endpointUrl
	 * @param jsonBodyAsString */
	public void performPutInternalRequest(String endpointUrl, String jsonBodyAsString) {
		if (!internallyConnected())
			createInternalConnectionToInfopass();
		System.out.println("Performing internal PUT with endpoint: " + endpointUrl);
		sessionId = Serenity.sessionVariableCalled("sessionId").toString();
		requestSpecification = rest().cookie("JSESSIONID", sessionId).contentType(requestContentType)
				.content(jsonBodyAsString);
		printOutgoingAndIncomingJSONConsoleBorder(jsonBodyAsString);
		response = requestSpecification.put(endpointUrl);
		printLowerBorder();

		verifyThat("Status code for internal PUT request was incorrect ", response.getStatusCode(), is(200));
	}

	/* External requests */

	
	
	/** @param endpointUrl */
	public String performGetExternalRequest(String endpointUrl) {
		if(!externallyConnected())
			createExternalConnectionToInfopass();
		System.out.println("Performing external GET with endpoint: " + endpointUrl);
		sessionId = Serenity.sessionVariableCalled("sessionId").toString();
		printIncomingJSONConsoleBorder();
		response = rest().header("Content-Type", requestContentType).header("Authorization", currentUser)
				.get(endpointUrl);
		printLowerBorder();
		verifyThat("Status code for external GET request was incorrect ", response.getStatusCode(), is(200));
		return response.getBody().asString();
	}
	
	public boolean externallyConnected() {
		if (Serenity.sessionVariableCalled("sessionId") == null) {
			return false;
		} else
			return true;
	}
	

	/* Misc. Utilities: */

	@Step
	public void sessionId(String sessionId) {
	}

	public String getInfopassUrl() {
		URL infopassUrl = null;
		try {
			infopassUrl = new URL(serenityProperties.getProperty("app.base.protocol"),
					serenityProperties.getProperty("app.base.host"),
					Integer.parseInt(serenityProperties.getProperty("app.base.port")),
					serenityProperties.getProperty("app.base.basePath"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("There was a problem creating the URL for infopass");
		}
		return infopassUrl.toString();
	}

	public void loadSerenityProperties() {
		serenityProperties = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("serenity.properties");
			serenityProperties.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading serenity.properties file");
		}
	}

	public Properties getSerenityProperties() {
		return serenityProperties;
	}

	private void printIncomingJSONConsoleBorder() {
		System.out.println("**********************************\n" + "**      Incoming JSON Body:     **\n"
				+ "**********************************\n");
	}

	private void printOutgoingAndIncomingJSONConsoleBorder(JsonObject jsonObject) {
		String output = "**********************************\n" + "**     Outgoing JSON Body:      **\n"
				+ "**********************************\n";
		try {
			output = output + new JSONObject(jsonObject.toString()).toString(3);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		output += "\n**********************************\n" + "**      Incoming JSON Body:     **\n"
				+ "**********************************";
		System.out.println(output);
	}

	private void printOutgoingAndIncomingJSONConsoleBorder(String jsonAsString) {
		String output = "**********************************\n" + "**     Outgoing JSON Body:      **\n"
				+ "**********************************\n";
		try {
			output = output + new JSONObject(jsonAsString).toString(3);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		output += "\n**********************************\n" + "**      Incoming JSON Body:     **\n"
				+ "**********************************";
		System.out.println(output);
	}

	private void printLowerBorder() {
		System.out.println("**********************************\n");
	}

	public boolean internallyConnected() {
		if (Serenity.sessionVariableCalled("sessionId") == null) {
			return false;
		} else
			return true;
	}

	@Step
	public void verifyMessageTypeIs(String expectedMessagetype) {
		verifyThat("The message type was incorrect ",
				JsonUtility.getValueFromResponseMatchingKey(response.getBody().asString(), "messageType"),
				equalToIgnoringCase(expectedMessagetype));
	}

	@Step
	public void verifyResponseMessageContains(String expectedMessage) {
		verifyThat("The message from the response was incorrect ",
				JsonUtility.getValueFromResponseMatchingKey(response.getBody().asString(), "message"),
				containsString(expectedMessage));
	}

	public String getResponseBody() {
		return response.getBody().prettyPrint();
	}

	public List<String> removeQuotesFromAllStrings(List<String> allStrings) {
		List<String> returnList = new ArrayList<String>();
		for (String a : allStrings) {
			int firstQuote = a.indexOf("\"") + 1;
			int secondQuote = a.indexOf("\"", firstQuote);
			if (firstQuote != -1 && secondQuote != -1)
				returnList.add(a.substring(firstQuote, secondQuote));
			else
				returnList.add(a);
			firstQuote = 0;
			secondQuote = 0;
		}
		return returnList;
	}

	public void verifyResponseContains(String stringToVerify) {
		assertThat(response.asString())
				.overridingErrorMessage("The response body did not contain the specified string: " + stringToVerify)
				.containsIgnoringCase(stringToVerify);
	}

	public String getResponseProperty(String propertyKey) {
		JsonBuilder json = new JsonBuilder(response.getBody().asString());
		return (String) json.getProperty(propertyKey);
	}

	public void asUserType(String userType) {
		if (userType.equalsIgnoreCase("privileged user"))
			currentUser = privUserForExternalAuthorization;
		else
			currentUser = regUserForExternalAuthorization;

	}

	public void printResponse() {
		System.out.println(getResponseBody());
	}

	public JsonObject getResponseJson() {
		JsonReader jsonReader = Json.createReader(new StringReader(response.getBody().asString()));
		JsonObject object = jsonReader.readObject();
		jsonReader.close();
		return object;
	}

	public void verifyResponseLists(String key, String value) {
		JsonPath jp = new JsonPath(response.asString());
		jp = jp.setRoot("data");
		List<String> lists = jp.getList(key);
		verifyThat("List has the value", lists, hasItems(value));
		verifyThat("List has the size", lists, hasSize(lists.size()));
	}

	/* add methods */
	/** if value is 'noValueSet' then do nto add to JSON */
	public static void add(JsonObjectBuilder builder, String key, String value) {
		if (value == null || value.equalsIgnoreCase("null"))
			builder.add(key, JsonValue.NULL);
		else if (value.equalsIgnoreCase("noValueSet")) {
		} else
			builder.add(key, value);
	}

	/** if NULL or NOT SET then do not add to JSON */
	public static void add(JsonObjectBuilder builder, String key, Boolean value) {
		if (value == null) {
		} else
			builder.add(key, value);
	}

	/** if '-1' do not add to JSON, if NULL add NULL to JSON */
	public static void add(JsonObjectBuilder builder, String key, Integer value) {
		if (value == null)
			builder.add(key, JsonValue.NULL);
		else if (value == -1) {
		} else
			builder.add(key, value);
	}

	/** if '-1L' do not add to JSON, if NULL add NULL to JSON */
	public static void add(JsonObjectBuilder builder, String key, Long value) {
		if (value == null)
			builder.add(key, JsonValue.NULL);
		else if (value == -1L) {
		} else
			builder.add(key, value);
	}

}
