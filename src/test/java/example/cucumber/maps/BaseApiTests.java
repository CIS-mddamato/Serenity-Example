package example.cucumber.maps;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;

/** @author mddamato */

public class BaseApiTests {

	protected static final String emailRegex = "[a-zA-Z0-9_\\'.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";
}
