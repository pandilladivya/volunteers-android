package vola.systers.com.volunteers_android.activities;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import vola.systers.com.volunteers_android.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;


public class SignInActivityTest {

    @Rule
    public ActivityTestRule<SignInActivity> signInActivityTestRule = new ActivityTestRule<SignInActivity>(SignInActivity.class);

    @Before
    public void setUp() throws Exception {

    }

    private String invalidEmail = "divya@123.com";
    private String invalidPassword="divya@1234";
    private String email = "divya@systers.com";
    private String password="divya1234";
    @Test
    public void testWithInvalidCredentials()
    {
        // input some text in the edit text
        onView(withId(R.id.input_email)).perform(typeText(invalidEmail));
        onView(withId(R.id.input_password)).perform(typeText(invalidPassword));
        // close soft keyboard
        Espresso.closeSoftKeyboard();
        // perform button click
        onView(withId(R.id.btn_login)).perform(click()).check(matches(isDisplayed()));
 }

    @Test
    public void testWithValidCredentials()
    {
        // input some text in the edit text
        onView(withId(R.id.input_email)).perform(typeText(email));
        onView(withId(R.id.input_password)).perform(typeText(password));
        // close soft keyboard
        Espresso.closeSoftKeyboard();
        // perform button click
        onView(withId(R.id.btn_login)).perform(click());
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }


    @After
    public void tearDown() throws Exception {

    }

}