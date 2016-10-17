package example.cucumber;

import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;

@RunWith(CucumberWithSerenity.class)

@CucumberOptions(

		features = { "src/test/resources/features/FeatureSet1" }, glue = { "example.cucumber.maps" }
// plugin = {"pretty", "html:reports/html/", "json:reports/json/results.json" },
// dryRun = false,
// strict = true,
// monochrome = true,

)

public class CucumberRunner1 {
}
