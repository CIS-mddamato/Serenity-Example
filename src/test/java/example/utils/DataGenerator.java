package example.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Helper class that helps create random data like SSN's and Receipt numbers as
 * well as words and numbers. This class returns random data for use with
 * forms/entry data.
 * 
 * @author mddamato
 */
public class DataGenerator {

	/**
	 * Returns a random number
	 * 
	 * @param length
	 *            - int - the number of numbers you want returned
	 * @return - String - random number
	 */
	public static String randomNumber(int length) {
		return RandomStringUtils.randomNumeric(length);
	}

	/** @return random int between min and max */
	public static int randomIntegerBetween(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

	/**
	 * Returns a random date of birth over 12 years 9 months and under 85
	 * 
	 * @return String - Date format: mm/dd/yyyy
	 */
	public static String randomBDay12to85() {
		Random rand = new Random();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -9);
		cal.add(Calendar.YEAR, -12);
		int randomNum = rand.nextInt((71 - 0) + 1) + 0;
		cal.add(Calendar.YEAR, -randomNum);
		randomNum = rand.nextInt((11 - 0) + 1) + 0;
		cal.add(Calendar.MONTH, -randomNum);
		randomNum = rand.nextInt((28 - 0) + 1) + 0;
		cal.add(Calendar.DATE, -randomNum);
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy");
		String year = format1.format(cal.getTime());
		format1 = new SimpleDateFormat("MM");
		String month = format1.format(cal.getTime());
		format1 = new SimpleDateFormat("dd");
		String day = format1.format(cal.getTime());
		return month + "/" + day + "/" + year;
	}

	/**
	 * returns random string with number and letters
	 * 
	 * @param length
	 *            - int - number of characters
	 * @return String - random alphanumeric
	 */
	public static String randomAlphaNumeric(int length) {
		return RandomStringUtils.randomAlphanumeric(length);
	}

	public static String randomAlphabetic(int length) {
		return RandomStringUtils.randomAlphabetic(length);
	}

	/**
	 * @param length
	 *            - number of characters you want returned
	 * @param allowdSplChrs
	 *            - a string that contains the characters you would like
	 *            included in addition to alphanumeric. ie. "!@#$%^"
	 * @return String - random string
	 */
	public static String randomStringWithAllowedSplChars(int length, String allowdSplChrs) {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz" + // alphabets
				"1234567890" + // numbers
				allowdSplChrs;
		return RandomStringUtils.random(length, allowedChars);
	}

	/**
	 * Generates a random email in the correct format. test@test.org
	 * 
	 * @param length
	 *            - number of characters in the first part of the email. ie.
	 *            FirstPart@test.org
	 * @return - String - random email formatted String
	 */
	public static String randomEmailFormattedString(int length) {
		length = length - 15;
		String allowedChars = "abcdefghijklmnopqrstuvwxyz" + // alphabets
				"1234567890" + // numbers
				"_-."; // special characters
		return RandomStringUtils.random(length, allowedChars) + "@uscis.dhs.test";
	}

	/**
	 * generates a URL format string
	 * 
	 * @param length
	 *            - String
	 * @return
	 */
	public static String randomUrl(int length) {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz" + // alphabets
				"1234567890" + // numbers
				"_-."; // special characters
		String url = "";
		String temp = RandomStringUtils.random(length, allowedChars);
		url = temp.substring(0, 3) + "." + temp.substring(4, temp.length() - 4) + "."
				+ temp.substring(temp.length() - 3);
		return url;
	}

	/**
	 * @return random Street Address formatted string. Format: 123 Fake St.
	 * @throws IOException
	 */
	public static String randomStreetAddress() {
		String returnValue = randomNumber(4);
		Random rand = new Random();
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(
					System.getProperty("user.dir") + "/src/test/java/infopass/utils/DCStreetNames.txt", "r");
			final long randomLocation = (long) rand.nextInt((((int) (raf.length()) - 20) - 0) + 1) + 0;
			raf.seek(randomLocation);
			raf.readLine();
			returnValue = returnValue + " " + raf.readLine().trim();
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] StreetTypes = { "Ave", "Rd", "St", "Dr", "Ln", "Way", "Pl", "Ct", "Terr", "Hwy", "Pkwy", "Blvd" };
		rand = new Random();
		int randomNum = rand.nextInt((11 - 0) + 1) + 0;
		returnValue = returnValue + " " + StreetTypes[randomNum];
		return returnValue;
	}

	/**
	 * @return random first name from file
	 * @throws IOException
	 */
	public static String randomFirstName() {
		Random rand = new Random();
		String returnValue = "";
		try {
			RandomAccessFile raf = new RandomAccessFile(
					System.getProperty("user.dir") + "/src/test/java/infopass/utils/FirstNames.txt", "r");
			final long randomLocation = (long) rand.nextInt((((int) (raf.length()) - 20) - 0) + 1) + 0;
			raf.seek(randomLocation);
			raf.readLine();
			returnValue = raf.readLine().trim();
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return returnValue.toUpperCase();
	}

	/**
	 * @return random middle name (same as first name)
	 * @throws IOException
	 */
	public static String randomMiddleName() {
		return randomFirstName();
	}

	/**
	 * @return random last name form file
	 * @throws IOException
	 */
	public static String randomLastName() {
		Random rand = new Random();
		String returnValue = "null";
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(System.getProperty("user.dir") + "/src/test/java/infopass/utils/LastNames.txt",
					"r");
			final long randomLocation = (long) rand.nextInt((((int) (raf.length()) - 20) - 0) + 1) + 0;
			raf.seek(randomLocation);
			raf.readLine();
			returnValue = raf.readLine().trim();
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnValue.toUpperCase();
	}

	/**
	 * @return random last name form file
	 * @throws IOException
	 */
	public static String randomPhoneNumber() {
		Random rand = new Random();
		String returnValue = "null";
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(System.getProperty("user.dir") + "/src/test/java/infopass/utils/PhoneNumber.txt",
					"r");
			final long randomLocation = (long) rand.nextInt((((int) (raf.length()) - 20) - 0) + 1) + 0;
			raf.seek(randomLocation);
			raf.readLine();
			returnValue = raf.readLine();
			returnValue = returnValue.substring(1, 4) + returnValue.substring(6, 9) + returnValue.substring(10, 14);

			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnValue;
	}



	/**
	 * <li>Creates a start and end date/time 1 hour apart</li>
	 * <li>Returns a string array 4 wide</li>
	 * <li>{"StartDate", "StartTime", "EndDate", "EndTime"}</li>
	 * 
	 * @return - a new string array to use for schedules. Example:
	 *         {"01/15/2016", "09:00 PM", "01/15/2016", "10:00 PM"}
	 */
	public static String[] GetNewUniqueDateAndTimeForSchedules() {
		String returnArray[] = new String[4];
		DateTime scheduleDate = new DateTime().withMinuteOfHour(0)
				.plusHours(DataGenerator.randomIntegerBetween(1, 288));
		returnArray[0] = scheduleDate.toString("MM/dd/yyyy", Locale.US);
		returnArray[1] = scheduleDate.toString("hh:mm a", Locale.US);
		returnArray[2] = scheduleDate.toString("MM/dd/yyyy", Locale.US);
		returnArray[3] = scheduleDate.plusHours(1).toString("hh:mm a", Locale.US);
		return returnArray;
	}

	/**
	 * Get a date string. Format examples: MM_dd_yyyy, MM/dd/yyyy, MM-dd-yyyy.
	 * Can supply an integer number of days in the future
	 * 
	 * @param numberOfDaysInTheFuture
	 * @param format
	 * @return
	 */
	public static String getDate(int numberOfDaysInTheFuture, String format) {
		DateTime scheduleDate = new DateTime();
		return scheduleDate.plusDays(numberOfDaysInTheFuture).toString(format, Locale.US);
	}

	/** see {@link #getDate(int, String)} */
	public static String getDate(String format) {
		return getDate(0, format);
	}

	/**
	 * Re-formats date from originalFormat to newFormat
	 * 
	 * @param date
	 * @param originalFormat
	 * @param newFormat
	 * @return - new date
	 */
	public static String reFormatDate(String date, String originalFormat, String newFormat) {
		String outputDate = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(originalFormat);
			Date tempDate = sdf.parse(date);
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(newFormat);
			outputDate = outputDateFormat.format(tempDate);
		} catch (ParseException ex) {
			System.out.println("Parse Exception");
		}
		return outputDate;

	}

	/**
	 * Return a list that contains the capture groups of the regex expression
	 * when compiled against the string Starts @ 1
	 * 
	 * @param string
	 * @param regularExpression
	 * @return
	 */
	public static List<String> getValuesFromRegEx(String string, String regularExpression) {
		List<String> returnList = new ArrayList<String>();
		Matcher m = Pattern.compile(regularExpression).matcher(string);
		if (m.find()) {
			for (int i = 0; i <= m.groupCount(); i++) {
				returnList.add(m.group(i));
			}
		}
		return returnList;
	}

	/**
	 * true if the given string and regular expression returns at least 1 match
	 * 
	 * @param string
	 * @param regularExpression
	 * @return
	 */
	public static boolean isRegExMatch(String string, String regularExpression) {
		return Pattern.compile(regularExpression).matcher(string).find();
	}

	public static <T> List<T> getDuplicate(Collection<T> list) {
		final List<T> duplicatedObjects = new ArrayList<T>();
		Set<T> set = new HashSet<T>() {
			private static final long serialVersionUID = 4181680731180054062L;

			@Override
			public boolean add(T e) {
				if (contains(e))
					duplicatedObjects.add(e);
				return super.add(e);
			}
		};
		for (T t : list)
			set.add(t);
		return duplicatedObjects;
	}

	public static <T> boolean hasDuplicate(Collection<T> list) {
		if (getDuplicate(list).isEmpty())
			return false;
		return true;
	}

	public static String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date1 = new Date();
		String date = dateFormat.format(date1);
		return date;

	}
}
