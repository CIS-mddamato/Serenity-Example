package example.cucumber.apiStepLibraries;

import example.cucumber.jsonBuilders.ManageZipCodesControllerJsonBuilder;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.steps.ScenarioSteps;

/**
 * @author mddamato
 */
public class WikiMainApiSteps extends ScenarioSteps {

	private static final long serialVersionUID = -2085246313179443065L;

	private ManageZipCodesControllerJsonBuilder manageZipCodesControllerJsonBuilder = new ManageZipCodesControllerJsonBuilder();

}