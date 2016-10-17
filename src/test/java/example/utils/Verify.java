package example.utils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.rules.Verifier;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFailure;


/**
 * Make a verification that will fail the test but not stop test execution.
 */
public class Verify {

	public static <T> void verifyThat(String reason, T actual, Matcher<? super T> matcher) {
		if (!matcher.matches(actual)) {
			Description description = new StringDescription();
			description.appendText(reason)
			.appendText("\nExpected: ")
			.appendDescriptionOf(matcher)
			.appendText("\n     but: ");
			matcher.describeMismatch(actual, description);
			String descriptionString = description.toString();
			try {
				AssertionError assertion = new AssertionError(descriptionString);
				StepFailure sf = new StepFailure(ExecutedStepDescription.withTitle("ExecutedStepDescription.withTitle"), assertion);
				StepEventBus seb = StepEventBus.getEventBus();
				TestStep step = new TestStep("new TestStep");
				step.failedWith(assertion);
				step.setResult(TestResult.FAILURE);
				TestOutcome outcome = TestOutcome.forTest("", Verifier.class);
				outcome.recordStep(step);
				outcome.setAnnotatedResult(TestResult.FAILURE);
				outcome.setTestFailureMessage(descriptionString);
				seb.stepFailed(sf);
				seb.testFinished(outcome);
			}
			catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}
	}
}

