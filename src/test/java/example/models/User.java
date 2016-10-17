/**
 * 
 */
package example.models;


import example.utils.DataGenerator;

/**
 * @author mddamato
 *
 */
public class User {

	private String userTypeCode;
	private String userId;
	private String emailAddress;
	private String firstName;
	private String middleName;
	private String lastName;
	private String addrId;
	private String region;
	private String district;
	private String USCISSiteCode;
	private boolean checkInOut;
	private boolean glblreport;
	private boolean mngCustomerAppt;
	private boolean overbook;
	private boolean report;
	private boolean schedule;



	public User() {
		super();
		this.firstName = DataGenerator.randomFirstName();
		this.middleName = DataGenerator.randomFirstName();
		this.lastName = DataGenerator.randomLastName();
		this.emailAddress = firstName+"."+middleName+"."+lastName+DataGenerator.randomNumber(2)+"@uscis.test.gov";
		this.userTypeCode = "GA";
		this.checkInOut = true;
		this.glblreport = true;
		this.mngCustomerAppt = true;
		this.overbook = true;
		this.report = true;
		this.schedule = true;
		this.addrId = "noValueSet";
		this.district = "noValueSet";
		this.USCISSiteCode = "noValueSet";
		this.region = "noValueSet";
	}

	
	/**
	 * @param userTypeCode the userTypeCode to set
	 */
	public void setUserTypeCode(String userTypeCode) {
		this.userTypeCode = userTypeCode;
	}

	/**
	 * @param addrId the addrId to set
	 */
	public void setAddrId(String addrId) {
		this.addrId = addrId;
	}

	/**
	 * @param district the district to set
	 */
	public void setDistrict(String district) {
		this.district = district;
	}
	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress
	 *            the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @param middleName
	 *            the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the uSCISSiteCode
	 */
	public String getUSCISSiteCode() {
		return USCISSiteCode;
	}

	/**
	 * @param uSCISSiteCode
	 *            the uSCISSiteCode to set
	 */
	public void setUSCISSiteCode(String uSCISSiteCode) {
		USCISSiteCode = uSCISSiteCode;
	}

	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userTypeCode;
	}

	/**
	 * @param userTypeCode
	 *            the userType to set
	 */
	public void setUserType(String newValue) {
		userTypeCode = newValue;
	}

	/**
	 * @return the apptwtype
	 */
	
	/**
	 * @return the checkin
	 */
	public boolean isCheckInOut() {
		return checkInOut;
	}

	/**
	 * @param checkin
	 *            the checkin to set
	 */
	public void setCheckInOut(boolean newValue) {
		checkInOut = newValue;
	}



	/**
	 * @return the glblreport
	 */
	public boolean isGlblreport() {
		return glblreport;
	}

	/**
	 * @param glblreport
	 *            the glblreport to set
	 */
	public void setGlblreport(boolean newValue) {
		glblreport = newValue;
	}

	/**
	 * @return the multipleappt
	 */
	public boolean isMngCustomerAppt() {
		return mngCustomerAppt;
	}

	/**
	 * @param multipleappt
	 *            the multipleappt to set
	 */
	public void setMngCustomerAppt(boolean newValue) {
		mngCustomerAppt = newValue;
	}

	/**
	 * @return the overbook
	 */
	public boolean isOverbook() {
		return overbook;
	}

	/**
	 * @param overbook
	 *            the overbook to set
	 */
	public void setOverbook(boolean newValue) {
		overbook = newValue;
	}

	/**
	 * @return the report
	 */
	public boolean isReport() {
		return report;
	}

	/**
	 * @param report
	 *            the report to set
	 */
	public void setReport(boolean newValue) {
		report = newValue;
	}

	/**
	 * @return the schedule
	 */
	public boolean isSchedule() {
		return schedule;
	}

	/**
	 * @param schedule
	 *            the schedule to set
	 */
	public void setSchedule(boolean newValue) {
		schedule = newValue;
	}

	/**
	 * set all access to false
	 */
	public void resetAllUserAccess() {
		this.checkInOut = false;
		this.glblreport = false;
		this.mngCustomerAppt = false;
		this.overbook = false;
		this.report = false;
		this.schedule = false;
		
	}

	public String getFullName() {
		return this.getFirstName()+" "+this.getMiddleName()+" "+this.getLastName();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String returnString = "";
		returnString ="User information: "
				+ "\nEmail Address = " + emailAddress 
				+ "\nFirst Name = "+ getFullName()
				+ "\nSite Code=" + USCISSiteCode 
				+ "\nUser Type="+ userTypeCode 
				+ "\nUser Access: ";
		
		if(this.checkInOut)
			returnString = returnString + "CheckInOut ";
		if(this.glblreport)
			returnString = returnString + "GlobalReport ";
		if(this.mngCustomerAppt)
			returnString = returnString + "ManageCustomerAppointment ";
		if(this.overbook)
			returnString = returnString + "Overbook ";
		if(this.report)
			returnString = returnString + "Report ";
		if(this.schedule)
			returnString = returnString + "Schedule ";
				return returnString;
	}


	/**
	 * @return the userTypeCode
	 */
	public String getUserTypeCode() {
		return userTypeCode;
	}


	/**
	 * @return the addrId
	 */
	public String getAddrId() {
		return addrId;
	}


	/**
	 * @return the district
	 */
	public String getDistrict() {
		return district;
	}


	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}


	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}



}