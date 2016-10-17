package example.cucumber.stepLibraries;

import java.util.List;

import example.cucumber.pages.WikiMainPage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.steps.ScenarioSteps;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author mddamato - Performs the actions associated with the check in and
 *         check out page
 */
public class WikiMainStepLibrary extends ScenarioSteps {

	private static final long serialVersionUID = 7239881750129025650L;

	WikiMainPage wikiMainPage;

	@Step
	public void goToPage(String address) {
		wikiMainPage.openAt(address);

	}

}