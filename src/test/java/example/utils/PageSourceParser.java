package example.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.serenitybdd.core.annotations.findby.By;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * Helper class that helps with parsing through table elements, only need to
 * send the css selector that matches all of the elements
 * 
 * @author mddamato
 */
public class PageSourceParser {
	private List<String> textFromAllElements;
	private List<String> valuesFromAllElements;
	private String cssSelector;
	private Elements allJSoupElements;
	private Document entireSource;

	public PageSourceParser(WebDriver driver) {
		entireSource = Jsoup.parse(driver.getPageSource());
	}

	/**
	 * Parses through page source that matches the specified css selector and
	 * pulls text/values from them that match the given CSS Selector and stores
	 * the list in this object
	 * 
	 * @param cssSelectorForElements
	 *            - String - matches ALL elements in table. ie: td tags
	 * @param driver
	 *            - WebDriver - ie: getDriver()
	 */
	public PageSourceParser(String cssSelectorForElements, WebDriver driver) {
		valuesFromAllElements = new ArrayList<String>();
		textFromAllElements = new ArrayList<String>();
		cssSelector = cssSelectorForElements;
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(cssSelector)));
		entireSource = Jsoup.parse(driver.getPageSource());
		allJSoupElements = entireSource.select(cssSelector);
	}

	/**
	 * Returns true if the list contains the given string
	 * 
	 * @param searchString
	 * @return boolean
	 */
	public boolean listContainsText(String searchString) {
		Iterator<String> it = textFromAllElements.iterator();
		while (it.hasNext()) {
			if (it.next().contains(searchString)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get all the visible text from the objects that match the given CSS
	 * Selector, list form
	 * 
	 * @return
	 */
	public List<String> getListOfTextFromAllElements() {
		populateTextFromAllElements();
		return textFromAllElements;
	}

	/**
	 * Get all the visible text from the objects that match the given CSS
	 * Selector, one single string
	 * 
	 * @return
	 */
	public String getAllTextFromMatchingElements() {
		populateTextFromAllElements();
		String allText = "";
		for (String a : textFromAllElements)
			allText = allText + a + "\n";
		return allText;
	}

	/**
	 * get all the values from the objects that match the given CSS Selector
	 * 
	 * @return
	 */
	public List<String> getListOfValuesFromAllElements() {
		populateValuesFromAllElements();
		return valuesFromAllElements;
	}

	public void populateValuesFromAllElements() {
		Iterator<Element> it = allJSoupElements.iterator();
		textFromAllElements = new ArrayList<String>();
		while (it.hasNext()) {
			Element current = it.next();
			if (current.attr("value") != null)
				valuesFromAllElements.add(current.attr("value"));
			else
				valuesFromAllElements.add("noValueInElement");
		}
	}

	public void populateTextFromAllElements() {
		Iterator<Element> it = allJSoupElements.iterator();
		textFromAllElements = new ArrayList<String>();
		while (it.hasNext()) {
			Element current = it.next();
			if (current.text() != null)
				textFromAllElements.add(current.text());
			else
				textFromAllElements.add("noTextInElement");
		}
	}

	public String getCssSelector() {
		return cssSelector;
	}

	public Elements getAllJSoupElements() {
		return allJSoupElements;
	}

	/**
	 * does not select the first option
	 * 
	 * @return
	 */
	public String getRandomValue() {
		populateValuesFromAllElements();
		assertThat(valuesFromAllElements.size())
				.overridingErrorMessage(
						"There were not enough values matching the CSS selector: " + "CSS Selector:" + getCssSelector())
				.isGreaterThan(0);
		return valuesFromAllElements.get(DataGenerator.randomIntegerBetween(1, valuesFromAllElements.size() - 1));
	}

	/**
	 * does not select the first option
	 * 
	 * @return
	 */
	public String getRandomText() {
		assertThat(textFromAllElements.size())
				.overridingErrorMessage(
						"There were not enough values matching the CSS selector: CSS Selector:" + getCssSelector())
				.isGreaterThan(0);
		return textFromAllElements.get(DataGenerator.randomIntegerBetween(1, textFromAllElements.size() - 1));
	}

	/**
	 * return the entire source
	 * 
	 * @return
	 */
	public String getAllTextFromSource() {
		return entireSource.text();
	}

	/**
	 * if the source has changed since the last time it was loaded, you may need
	 * to refresh
	 * 
	 * @param driver
	 */
	public void refreshEntireSource(WebDriver driver) {
		entireSource = Jsoup.parse(driver.getPageSource());
	}

}
