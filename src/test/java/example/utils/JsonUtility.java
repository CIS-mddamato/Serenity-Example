package example.utils;

import static example.utils.Verify.verifyThat;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jayway.restassured.path.json.JsonPath;

import net.thucydides.core.annotations.Step;

public class JsonUtility {

	/** Get the value associated to this key
	 * 
	 * @param key
	 *            - the key to get the values of
	 * @return - String value */
	public static String getValueFromResponseMatchingKey(String jsonString, String key) {
		JsonPath jp = new JsonPath(jsonString);
		System.out.println("JSON: " + jsonString);
		String value = jp.getString(key);
		System.out.println("VALUE FROM JSON: " + value);
		return value;
	}

	/** Get the value associated to this subkey/key
	 * 
	 * @param key
	 *            - the key to get the values of
	 * @return - List<String> of all values */
	public static String getValueFromResponseMatchingKey(String jsonString, String subkey, String key) {
		String value = "";
		try {
			value = new JsonPath(jsonString).setRoot(subkey).getString(key);
		} catch (java.lang.IllegalArgumentException e) {
			System.out.println("Could not locate key: " + subkey + "." + key);
			System.out.println(e.getMessage());
			return null;
		}
		return value;
	}

	/** Get all of the values associated to this key
	 * 
	 * @param key
	 *            - the key to get the values of
	 * @return - List<String> of all values */
	public static List<String> getValuesFromResponseMatchingKey(String jsonString, String key) {
		List<String> returnList = new ArrayList<String>();
		returnList = new JsonPath(jsonString).getList(key);
		return returnList;
	}

	/** get all of the values associated to this subkey.key Example:
	 * 
	 * <pre>
	 *  "data": [
	        {
	            "id": 0,
	            "location": "locationOne",
	            "code": null,
	        }
	    ],
	 * </pre>
	 * 
	 * subkey = data; key = location; value = locationOne
	 * 
	 * @param subkey
	 *            - the parent Json object
	 * @param key
	 *            - the key to get the values of
	 * @return List<String> of all values */
	public static List<String> getValuesFromResponseMatchingKey(String jsonString, String subkey, String key) {
		List<String> lists = new ArrayList<String>();
		try {
			lists = new JsonPath(jsonString).setRoot(subkey).getList(key, String.class);
		} catch (java.lang.IllegalArgumentException e) {
			verifyThat("Could not locate key: " + subkey + "." + key + "  \n", true, is(false));
			return lists;
		} catch (java.lang.NullPointerException a) {
			verifyThat("Could not locate key: " + subkey + "." + key + "  \n", true, is(false));
			return lists;
		}
		return lists;
	}

	/** Example JSON response:
	 * 
	 * <pre>
	{
	    "sites": [
	        {
	            "addrId": "2a0eaab7-2224-4c4c-b0d5-68a72f4f55a4",
	            "district": "01",
	            "region": "011",
	            "siteCode": "BBB",
	            "siteName": "asda",
	            "status": null,
	            "lastLoginDate": 1452784661442,
	            "internationalFlag": "N"
	        },
	        {
	            "addrId": "2a0eaab7-2224-4c4c-b0d5-68a72f4f55a4",
	            "district": "02",
	            "region": "022",
	            "siteCode": "AAA",
	            "siteName": "asda",
	            "status": null,
	            "lastLoginDate": 1452784661442,
	            "internationalFlag": "N"
	        }
	    ],
	    "timeZones": [
	        {
	            "timeZone": "Etc/GMT+12",
	            "displayName": "(GMT -12:00) International Date Line West",
	            "standardDisplayName": "Dateline Standard Time"
	        },
	        {
	            "timeZone": "Etc/GMT+11",
	            "displayName": "(GMT -11:00) Coordinated Universal Time-11",
	            "standardDisplayName": "GMT -11"
	        }
	        
	    ]
	}
	 * </pre>
	 * 
	 * @param pathToRoot
	 *            - The path to the list. Example: "sites"
	 * @param firstKeyToMatch
	 *            - The key for the value you want to match. Example: "siteCode"
	 * @param firstValueToMatch
	 *            - The value of the firstKeyToMatch you want additonal values
	 *            for. Example: "AAA"
	 * @param additionalKeysToReturn
	 *            - list of keys you want matching values for. Exmaple:
	 *            "district", "region"
	 * @return - an array of all of the values including the given
	 *         firstValueToMatch. Example: {"AAA","02","022"} */
	@Step
	public static String[] getValuesFromResponseMatchingKeys(String jsonString, String pathToRoot,
			String firstKeyToMatch, String firstValueToMatch, String... additionalKeysToReturn) {
		List<String> listOfValuesFromPath = new ArrayList<String>();
		String[] returnList = new String[additionalKeysToReturn.length + 1];
		try {
			listOfValuesFromPath = new JsonPath(jsonString).setRoot(pathToRoot).getList(firstKeyToMatch, String.class);
		} catch (java.lang.IllegalArgumentException e) {
			System.out.println(e.getMessage() + "\nCould not locate key: " + pathToRoot + "." + firstKeyToMatch);
			return null;
		}
		Iterator<String> it = listOfValuesFromPath.iterator();
		int i = 0;
		while (it.hasNext()) {
			if (it.next().equalsIgnoreCase(firstValueToMatch)) {
				returnList[0] = firstValueToMatch;
				break;
			}
			i++;
		}
		for (int j = 0; j <= additionalKeysToReturn.length - 1; j++) {
			returnList[j + 1] = new JsonPath(jsonString).setRoot(pathToRoot)
					.getString(additionalKeysToReturn[j] + "[" + Integer.toString(i) + "]");
		}
		return returnList;
	}

	public static List<String> getValuesFromResponseMatchingKey(String jsonString, String subkey, String nextSubkey,
			String key) {
		List<String> lists = new ArrayList<String>();
		try {
			lists = new JsonPath(jsonString).setRoot(subkey + "." + nextSubkey).getList(key, String.class);
		} catch (java.lang.IllegalArgumentException e) {
			System.out.println("Could not locate key: " + subkey + "." + nextSubkey + "." + key);
			System.out.println(e.getMessage());
			return null;
		}
		return lists;
	}
}
