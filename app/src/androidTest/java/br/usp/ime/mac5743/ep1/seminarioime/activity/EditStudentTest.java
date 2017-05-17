package br.usp.ime.mac5743.ep1.seminarioime.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.usp.ime.mac5743.ep1.seminarioime.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EditStudentTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void editStudentTest() {
        ViewInteraction appCompatEditText = onView(
                withId(R.id.nusp));
        appCompatEditText.perform(scrollTo(), replaceText("9883284"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.password));
        appCompatEditText2.perform(scrollTo(), replaceText("q1w2e3"), closeSoftKeyboard());

        pressBack();

        ViewInteraction switch_ = onView(
                allOf(withId(R.id.auto_connect), withText("Connect automatically next time")));
        switch_.perform(scrollTo(), click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.login), withText("Login")));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Edit account"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.name), withText("Aderlei Filho")));
        appCompatEditText3.perform(scrollTo(), replaceText("Aderle"), closeSoftKeyboard());

        pressBack();

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.name), withText("Aderle")));
        appCompatEditText4.perform(scrollTo(), click());

        pressBack();

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.save), withText("Save")));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Edit account"), isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.name), withText("Aderle"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                5),
                        isDisplayed()));
        editText.check(matches(withText("Aderle")));

        pressBack();

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.save), withText("Save")));
        appCompatButton3.perform(scrollTo(), click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
