package example.cucumber.maps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import example.cucumber.stepLibraries.WikiMainStepLibrary;
import net.thucydides.core.annotations.Steps;

public class WiktionaryHomeMap {

	@Steps
	WikiMainStepLibrary wikiMainStepLibrary;

	@Given("I have navigated to ((?:http\\:\\/\\/|https\\:\\/\\/)?(?:[a-z0-9][a-z0-9\\-]*\\.)+[a-z0-9][a-z0-9\\-]*$)")
	public void givenTemplateMethod(String address) {

		wikiMainStepLibrary.goToPage(address);

	}

}