/**
 * 
 */
package example.cucumber.pages;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.ThucydidesSystemProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThat;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.remote.RemoteWebDriver;

import example.utils.DataGenerator;
import example.utils.PageSourceParser;

/**
 * @author mddamato
 *
 */
public class BasePageObject extends PageObject {
	/**
	 * find a link on the current page that contains linkTextToClick and clicks
	 * on it
	 * 
	 * @param linkTextToClick
	 */
	public void clickLinkWithText(String linkTextToClick) {
		WebElementFacade link = find(By.partialLinkText(linkTextToClick));
		click(link);
	}

	/*
	 * public boolean linkWithTextExists(String textToLookFor){
	 * List<WebElementFacade> facades = find(By.partialLinkText(textToLookFor));
	 * return facades; WebElementFacade link
	 * =find(By.partialLinkText(textToLookFor)); return true;
	 * 
	 * }
	 */
	public boolean linkWithTextExists(String textToLookFor) {
		List<WebElementFacade> allinks = findAll(By.partialLinkText(textToLookFor));
		if (allinks.size() >= 1)
			return true;
		return false;
	}

	/**
	 * selects from dropdown the option that contains partialVisibleText
	 * 
	 * @param dropdown
	 * @param partialVisibleText
	 */
	public void selectFromDropdownVisibleTextThatContains(WebElementFacade dropdown, String partialVisibleText) {
		List<String> allDropdownOptions = dropdown.getSelectOptions();
		boolean found = false;
		for (String currentOption : allDropdownOptions) {
			if (currentOption.contains(partialVisibleText)) {
				dropdown.selectByVisibleText(currentOption);
				found = true;
				break;
			}
		}
		if (!found) {
			assertThat(true).overridingErrorMessage(
					"No option available for selection, partial text not found. Dropdown: " + dropdown.toString())
					.isFalse();
		}
	}

	/**
	 * <p>
	 * return all matching webelements
	 * <p>
	 * temporarily sets implicit wait to 20 seconds
	 * <p>
	 * throws error if list is empty
	 * 
	 * @param cssSelector
	 * @return List<WebElementFacade>
	 */
	public List<WebElementFacade> findWebElementsByCssSelectorWithError(String cssSelector) {
		setImplicitTimeout(20, TimeUnit.SECONDS);
		List<WebElementFacade> returnList = findAll(By.cssSelector(cssSelector));
		resetImplicitTimeout();
		assertThat(returnList.size())
				.overridingErrorMessage("Error: The list of elements is empty. Using CSS Selector: " + cssSelector)
				.isGreaterThan(0);
		return returnList;
	}

	/**
	 * <p>
	 * return all matching webelements
	 * <p>
	 * temporarily sets implicit wait to 20 seconds
	 * 
	 * @param cssSelector
	 * @return List<WebElementFacade>
	 */
	public List<WebElementFacade> findWebElementsByCssSelector(String cssSelector) {
		setImplicitTimeout(20, TimeUnit.SECONDS);
		List<WebElementFacade> returnList = findAll(By.cssSelector(cssSelector));
		if (returnList.size() <= 0) {
			waitForRenderedElementsToBePresent(By.cssSelector(cssSelector));
			returnList = findAll(By.cssSelector(cssSelector));
		}
		resetImplicitTimeout();
		return returnList;
	}

	/**
	 * <p>
	 * return matching webelement, wait until visible and enabled
	 * 
	 * @param cssSelector
	 * @return List<WebElementFacade>
	 */
	public WebElementFacade findWebElementByCssSelector(String cssSelector) {
		WebElementFacade element = find(By.cssSelector(cssSelector));
		element.waitUntilVisible();
		element.waitUntilEnabled();
		return element;
	}

	/**
	 * <p>
	 * searches page source for given string, asserts visible
	 * 
	 * @param textToSearchFor
	 */
	public void assertVisibleTextIsPresentOnEntirePage(String textToSearchFor) {
		PageSourceParser parser = new PageSourceParser("html body", getDriver());
		assertThat(parser.listContainsText(textToSearchFor)).overridingErrorMessage(
				"Searching entire page for given string failed. Text was not found when it should. Search String: "
						+ textToSearchFor)
				.isTrue();
	}

	/**
	 * <p>
	 * searches page source for given string, asserts not visible
	 * 
	 * @param textToSearchFor
	 */
	public void assertVisibleTextIsNotPresentOnEntirePage(String textToSearchFor) {
		PageSourceParser parser = new PageSourceParser("html body", getDriver());
		assertThat(parser.listContainsText(textToSearchFor)).overridingErrorMessage(
				"Searching entire page for string failed. Text was found when it shouldn't. Search String: "
						+ textToSearchFor)
				.isFalse();
	}

	public static Object executeJavaScript(String script, WebDriver driver) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return js.executeScript(script);

	}

	/**
	 * <p>
	 * Send the button that when clicked will open the PDF. This will only work
	 * in Chrome/Firefox
	 * </p>
	 * 
	 * @param buttonThatGeneratesPDF
	 * @return
	 */
	public String getPDFTextFromLink(WebElementFacade buttonThatGeneratesPDF) {
		String winHandleBefore = getDriver().getWindowHandle();
		Actions action = new Actions(getDriver());
		action.keyDown(Keys.CONTROL);
		action.click(buttonThatGeneratesPDF.getWrappedElement());
		action.keyUp(Keys.CONTROL);
		action.perform();
		for (String winHandle : getDriver().getWindowHandles()) {
			getDriver().switchTo().window(winHandle);
		}
		waitABit(3000);
		action = new Actions(getDriver());
		action.keyDown(Keys.CONTROL);
		action.sendKeys("a");
		action.keyUp(Keys.CONTROL);
		action.perform();
		waitABit(500);
		action = new Actions(getDriver());
		action.keyDown(Keys.CONTROL);
		action.sendKeys("c");
		action.keyUp(Keys.CONTROL);
		action.perform();
		waitABit(500);
		getDriver().switchTo().window(winHandleBefore);
		waitABit(500);
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result = (String) contents.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public void verifyWebElementByCssSelectorContainsText(String cssSelector, String textToVerify) {
		WebElementFacade element = findWebElementByCssSelector(cssSelector);
		String textFromPage = element.getText();
		assertThat(textFromPage).overridingErrorMessage("Verification Failed: " + "\n CSS Selector: " + cssSelector
				+ " \n The web element form the page returned: " + textFromPage + " \n The text to verify was: "
				+ textToVerify).containsIgnoringCase(textToVerify);

	}

	/**
	 * <p>
	 * This will select a date from the calendar.
	 * </p>
	 * <p>
	 * The calendar button must be in view but not yet selected.
	 * </p>
	 * <p>
	 * All we need is to set the set the entire table CSS Selector, in this case
	 * "entireTableCssSelector"
	 * </p>
	 * <p>
	 * If there are more than one calendar on the screen you need to specify
	 * which index of calendar you are referring to, if there are 2 calendars
	 * and you only need to manipulate the first then use index 0
	 * </p>
	 * 
	 * @param entireTableCssSelector
	 *            - the CSS Selector that selects the "table" tag element
	 * @param date
	 *            - the date in format MM/DD/YYYY
	 * @param whichTableIndex
	 *            - in case there are more than one table/calendar appearing on
	 *            the same screen
	 */
	public void selectDateFromCalendar(String entireTableCssSelector, String date, int whichTableIndex) {
		// break the date into its parts:
		String[] splitDate = date.split("/");
		String month = splitDate[0];
		String day = splitDate[1];
		String year = splitDate[2];

		// click on the top date+year button == - 0-left button 1-month+year
		// 2-right button, its the second button
		List<WebElementFacade> entireTableList = findWebElementsByCssSelector(entireTableCssSelector);
		waitABit(500);
		WebElement entireTable = entireTableList.get(whichTableIndex);
		click(entireTable.findElements(By.cssSelector("thead tr th button")).get(1));

		waitABit(100);
		// then click on the year button, same spot as the previous lookup, its
		// the second button
		entireTableList = findWebElementsByCssSelector(entireTableCssSelector);
		entireTable = entireTableList.get(whichTableIndex);
		click(entireTable.findElements(By.cssSelector("thead tr th button")).get(1));

		// grab all of the year buttons
		entireTableList = findWebElementsByCssSelector(entireTableCssSelector);
		entireTable = entireTableList.get(whichTableIndex);
		List<WebElement> allyears = entireTable.findElements(By.cssSelector("tbody>tr>td>button"));
		waitABit(100);
		// go through all the years listed and click on the one that matches the
		// year given
		Iterator<WebElement> it = allyears.iterator();
		boolean foundYear = false;
		while (it.hasNext()) {
			WebElement currentYearButton = it.next();
			if (currentYearButton.findElement(By.tagName("span")).getText().equalsIgnoreCase(year)) {
				waitABit(100);
				click(currentYearButton);
				foundYear = true;
				break;
			}
		}
		assertThat(foundYear).overridingErrorMessage("Error: No year was selected from the calendar").isTrue();

		// convert month number to month text
		switch (month) {
		case "01":
			month = "January";
			break;
		case "02":
			month = "February";
			break;
		case "03":
			month = "March";
			break;
		case "04":
			month = "April";
			break;
		case "05":
			month = "May";
			break;
		case "06":
			month = "June";
			break;
		case "07":
			month = "July";
			break;
		case "08":
			month = "August";
			break;
		case "09":
			month = "September";
			break;
		case "10":
			month = "October";
			break;
		case "11":
			month = "November";
			break;
		case "12":
			month = "December";
			break;
		default:
			assertThat(true).overridingErrorMessage("Error: Not a valid month selection. ReportsDailyRosterPage")
					.isFalse();
			break;
		}

		// grab all the month buttons
		entireTableList = findWebElementsByCssSelector(entireTableCssSelector);
		entireTable = entireTableList.get(whichTableIndex);
		List<WebElement> allMonths = entireTable.findElements(By.cssSelector("tbody>tr>td>button"));
		waitABit(100);
		// go through all the months listed and click on the one that matches
		// the month given
		it = allMonths.iterator();
		boolean foundMonth = false;
		while (it.hasNext()) {
			WebElement currentMonthButton = it.next();
			if (currentMonthButton.findElement(By.tagName("span")).getText().equalsIgnoreCase(month)) {
				waitABit(100);
				click(currentMonthButton);
				foundMonth = true;
				break;
			}
		}
		assertThat(foundMonth).overridingErrorMessage("Error: No month was selected from the calendar").isTrue();

		// grab all the day buttons, keeping only the ones for THIS month, as in
		// day 01 thorugh 30
		entireTableList = findWebElementsByCssSelector(entireTableCssSelector);
		entireTable = entireTableList.get(whichTableIndex);
		List<WebElement> allDays = entireTable.findElements(By.cssSelector("tbody>tr>td>button"));
		waitABit(100);
		List<WebElement> allValidDays = new ArrayList<WebElement>();
		it = allDays.iterator();
		boolean reachedEnd = false;
		while (it.hasNext()) {
			WebElement currentDayButton = it.next();
			if (currentDayButton.findElement(By.tagName("span")).getText().equalsIgnoreCase("01")) {
				while (it.hasNext()) {
					allValidDays.add(currentDayButton);
					currentDayButton = it.next();
					if (currentDayButton.findElement(By.tagName("span")).getText().equalsIgnoreCase("01")) {
						reachedEnd = true;
						break;
					}
				}
			}
			if (reachedEnd) {
				break;
			}
		}

		// go through all valid days and click on the one that matches our input
		it = allValidDays.iterator();
		boolean foundDay = false;
		while (it.hasNext()) {
			WebElement currentDayButton = it.next();
			if (currentDayButton.findElement(By.tagName("span")).getText().equalsIgnoreCase(day)) {
				waitABit(100);
				click(currentDayButton);
				foundDay = true;
				break;
			}
		}
		assertThat(foundDay).overridingErrorMessage("Error: No day was selected from the calendar").isTrue();
		WebElementFacade entireTableFacade = (WebElementFacade) entireTable;
		// entireTableFacade.waitUntilNotVisible();
	}

	/**
	 * send this method a dropdown with values and it will select one randomly
	 * it does not select the first option
	 * 
	 * @param dropDownElement
	 *            - WebElementFacade that is a select-dropdown
	 */
	public void selectARandomOptionFromDropdown(String optionsCssSelector, String selectObjectCssSelector) {
		WebElementFacade dropdown = findWebElementByCssSelector(selectObjectCssSelector);
		waitABit(300);
		dropdown.waitUntilEnabled();
		System.out.println("Selecting random value from dropdown.\n" + "Options CSS Selector: " + optionsCssSelector
				+ ", Select CSS Selector: " + selectObjectCssSelector);
		PageSourceParser parser = new PageSourceParser(optionsCssSelector, getDriver());
		dropdown.selectByValue(parser.getRandomValue());
	}

	/**
	 * wait until the element with cssSelector is present, visible, and enabled
	 * 
	 * @param cssSelector
	 */
	public void waitForElement(String cssSelector) {
		waitForRenderedElementsToBePresent(By.cssSelector(cssSelector));
		WebElementFacade a = findWebElementByCssSelector(cssSelector);
		a.waitUntilVisible();
		a.waitUntilEnabled();
	}

	/**
	 * Wait for the element that would contain the text
	 * 
	 * @param text
	 */
	public void waitForElementThatContainsText(String text) {
		waitForRenderedElementsToBePresent(By.partialLinkText(text));
		WebElementFacade a = find(By.linkText(text));
		a.waitUntilVisible();
		a.waitUntilEnabled();
	}

	/**
	 * For up to ten seconds, wait until 'loading' image goes away
	 */
	public void waitForSplashScreenToFinish() {
		waitABit(250);
		String bodyTag = "body";
		WebElement element = getDriver().findElement(By.tagName(bodyTag));
		String classAtt = element.getAttribute("class");
		for (int i = 0; i < 40; i++) {
			if (classAtt.contains("block-ui-active")) {
				if (i % 4 == 0)
					System.out.println("Waiting for splash screen...");
				waitABit(250);
				classAtt = getDriver().findElement(By.tagName(bodyTag)).getAttribute("class");
			} else
				break;
		}
	}

	protected void click(String cssSelector, int index) {
		List<WebElementFacade> allElements = findWebElementsByCssSelector(cssSelector);
		WebElementFacade element = allElements.get(index);
		click(element);
	}

	protected void click(String cssSelector) {
		click(findWebElementByCssSelector(cssSelector));
	}

	public void click(List<WebElementFacade> calendarButtons2, int index) {
		click(calendarButtons2.get(index));
	}

	protected void click(WebElementFacade webElement) {
		try {
			webElement.waitUntilClickable();
			webElement.click();
		} catch (org.openqa.selenium.WebDriverException exception) {
			System.out.println(exception.getMessage());
			System.out.println("Clicking failed.. re-attempting to click..");
			waitABit(2500);
			Actions actions = new Actions(getDriver());
			actions.moveToElement(webElement).click().perform();
		}
		waitForSplashScreenToFinish();
	}

	public void click(WebElement element) {
		try {
			element.click();
		} catch (org.openqa.selenium.WebDriverException exception) {
			System.out.println("Clicking failed.. re-attempting to click..");
			waitABit(250);
			Actions actions = new Actions(getDriver());
			actions.moveToElement(element).click().perform();
		}

		waitForSplashScreenToFinish();
	}

}
