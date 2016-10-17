package example.cucumber.jsonBuilders;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import example.utils.JsonUtility;

import java.util.List;

/** @author mddamato */
public class ManageZipCodesControllerJsonBuilder extends BaseJsonBuilder {

	private static final long serialVersionUID = 6866325171933248515L;

	/* Endpoints */
	private static final String getZipCodesEndpoint = "/paginatezipcodes";
	private static final String addZipCodeEndpoint = "/addzipcode";
	private static final String deleteZipCodeEndpoint = "/deletezipcode";
	private static final String moveZipCodeEndpoint = "/movezipcode";

	/** Gets a list of all the zipcodes for given site. First this gets the
	 * total number of items then PUTs again all remaining
	 * <p>
	 * 
	 * <pre>
	 * {site: "{siteCode}", pageNum: 1, pageSize: 10, zipCode: "", city: "", state: ""}
	 * </pre>
	 * <p>
	 * {@link #getZipCodesEndpoint}
	 *
	 * @param siteCode
	 * @return a List<String> of zipcodes */
	public List<String> getZipCodes(String siteCode) {
		JsonObjectBuilder mainjsonbuilder = Json.createObjectBuilder();
		add(mainjsonbuilder, "site", siteCode);
		add(mainjsonbuilder, "pageNum", 1);
		add(mainjsonbuilder, "pageSize", 10);
		add(mainjsonbuilder, "zipCode", "");
		add(mainjsonbuilder, "city", "");
		add(mainjsonbuilder, "state", "");
		Integer totalNumberOfZipcodes = Integer.parseInt(JsonUtility.getValueFromResponseMatchingKey(
				performPutInternalRequest(getZipCodesEndpoint, mainjsonbuilder.build()), "totalItems"));
		mainjsonbuilder = Json.createObjectBuilder();
		add(mainjsonbuilder, "site", siteCode);
		add(mainjsonbuilder, "pageNum", 1);
		add(mainjsonbuilder, "pageSize", totalNumberOfZipcodes);
		add(mainjsonbuilder, "zipCode", "");
		add(mainjsonbuilder, "city", "");
		add(mainjsonbuilder, "state", "");
		String response = performPutInternalRequest(getZipCodesEndpoint, mainjsonbuilder.build());
		return JsonUtility.getValuesFromResponseMatchingKey(response, "itemsOnPage", "zipCode");
	}

	/** Add a zipcode with the following information
	 *
	 * @param siteCode
	 * @param zipcode
	 * @param city1
	 * @param state1
	 *            applicationCode = "INFPS"; createdById =
	 *            "admin.user@test.com";
	 * @return */
	public String addZipCode(String siteCode, String zipcode, String state1, String city1) {
		JsonObjectBuilder mainjsonbuilder = Json.createObjectBuilder();
		add(mainjsonbuilder, "city", city1);
		add(mainjsonbuilder, "applicationCode", "INFPS");
		add(mainjsonbuilder, "state", state1);
		add(mainjsonbuilder, "uscisLctnCd", siteCode);
		add(mainjsonbuilder, "createdById", "admin.user@test.com");
		add(mainjsonbuilder, "zipCode", zipcode);
		return performPutInternalRequest(addZipCodeEndpoint, mainjsonbuilder.build());
	}

	/** Delete a zipcode
	 * 
	 * @param siteCode
	 * @param zipcode
	 * @param state1
	 * @param city1
	 *            apptCatgCd = "GI";
	 * @return the response from the PUT method */
	public String deleteZipCode(String siteCode, String zipcode, String state1, String city1) {
		JsonObjectBuilder mainjsonbuilder = Json.createObjectBuilder();
		add(mainjsonbuilder, "applicationCode", "INFPS");
		add(mainjsonbuilder, "uscisLctnCd", siteCode);
		add(mainjsonbuilder, "zipCode", zipcode);
		add(mainjsonbuilder, "state", state1);
		add(mainjsonbuilder, "city", city1);
		add(mainjsonbuilder, "apptCatgCd", "GI");// Adjud. Needs this field
													// hard-coded from infopass
		return performPutInternalRequest(deleteZipCodeEndpoint, mainjsonbuilder.build());
	}

	/** @param sitecode
	 * @param zipCodesToMove
	 * @return */
	public String moveZipCode(String sitecode, String... zipCodesToMove) {
		JsonArrayBuilder zipcodeArraybuilder = Json.createArrayBuilder();
		for (String zipcode : zipCodesToMove)
			zipcodeArraybuilder.add(zipcode);
		JsonObjectBuilder mainjsonbuilder = Json.createObjectBuilder();
		add(mainjsonbuilder, "applicationCode", "INFPS");
		add(mainjsonbuilder, "uscisLctnCd", sitecode);
		mainjsonbuilder.add("zipCodes", zipcodeArraybuilder.build());
		return performPutInternalRequest(moveZipCodeEndpoint, mainjsonbuilder.build());
	}
}
