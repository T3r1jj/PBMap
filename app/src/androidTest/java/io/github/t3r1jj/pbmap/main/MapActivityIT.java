package io.github.t3r1jj.pbmap.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

import io.github.t3r1jj.pbmap.R;
import io.github.t3r1jj.pbmap.main.external.InstallListener;
import io.github.t3r1jj.pbmap.testing.DisableAnimationsRule;
import io.github.t3r1jj.pbmap.testing.RetryRunner;
import io.github.t3r1jj.pbmap.testing.ScreenshotOnTestFailedRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static io.github.t3r1jj.pbmap.main.drawer.MapsDrawerFragmentIT.autoOpenDrawerReturningPreferences;
import static io.github.t3r1jj.pbmap.main.drawer.TutorialIT.verifyTutorial;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(RetryRunner.class)
public class MapActivityIT {

    private final ActivityTestRule<MapActivity> activityRule =
            new ActivityTestRule<>(MapActivity.class, true, false);

    @Rule
    public RuleChain testRule = RuleChain
            .outerRule(activityRule)
            .around(new DisableAnimationsRule())
            .around(new ScreenshotOnTestFailedRule());

    @Before
    public void setUp() {
        PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getInstrumentation().getTargetContext())
                .edit()
                .clear()
                .apply();
    }

    @After
    public void tearDown() {
        PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getInstrumentation().getTargetContext())
                .edit()
                .clear()
                .apply();
    }

    @Test
    @LargeTest
    public void initiateImprovementUseCase() {
        autoOpenDrawerReturningPreferences(false);
        activityRule.launchActivity(new Intent());
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        int x = device.getDisplayWidth() / 2;
        int y = device.getDisplayHeight() / 2;
        device.swipe(x, y, x, y, 500);

        onView(withText(R.string.place_destination_marker)).check(matches(isDisplayed()));
        onView(withText(R.string.place_source_marker)).check(matches(isDisplayed()));
        onView(withText(R.string.improve)).check(matches(isDisplayed()));
        onView(withText(R.string.place_destination_marker)).check(matches(isDisplayed()));
    }

    @Test
    @SmallTest
    public void setLogo() {
        activityRule.launchActivity(new Intent());
        AtomicBoolean successfulResult = new AtomicBoolean();
        Drawable drawable = InstrumentationRegistry.getInstrumentation().getContext().getDrawable(io.github.t3r1jj.pbmap.test.R.drawable.test_logo);
        ImageView logo = new ImageView(InstrumentationRegistry.getInstrumentation().getTargetContext());
        logo.setImageDrawable(drawable);
        activityRule.getActivity().runOnUiThread(() -> {
            activityRule.getActivity().setLogo(logo);
            successfulResult.set(true);
        });
        SystemClock.sleep(1000);
        assertTrue(successfulResult.get());
    }

    @Test
    @SmallTest
    public void testTutorialFinished() {
        PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getInstrumentation().getTargetContext())
                .edit()
                .putBoolean(Tutorial.INTRODUCTION_FINISHED, true)
                .apply();
        activityRule.launchActivity(new Intent());
        onView(withId(R.id.help_fab)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    @SmallTest
    public void testTutorial() {
        activityRule.launchActivity(new Intent());
        onView(withContentDescription(R.string.navigation_drawer_close)).perform(click());
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.findObject(By.res("io.github.t3r1jj.pbmap:id/help_fab")).click();
        Context ctx = getInstrumentation().getTargetContext();
        verifyTutorial(device, ctx);
    }

    @Test
    @LargeTest
    public void testUnfinished() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEARCH);
        sendIntent.putExtra(SearchManager.QUERY, "PB_WB_L0_IET");
        activityRule.launchActivity(sendIntent);
        onView(withText(R.string.unfinished)).check(matches(isDisplayed()));
    }

    @Test
    @LargeTest
    public void testViewUrl() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("https://example.com/mobile/pb_wb_l0"));
        activityRule.launchActivity(sendIntent);
        onView(withText(R.string.name_pb_wb_l0)).check(matches(isDisplayed()));
    }

    @Test
    @LargeTest
    public void testViewReferrer() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getInstrumentation().getTargetContext());
        preferences.edit().putString(InstallListener.REFERRER, Uri.decode("https%3A%2F%2Fpbmap.termian.dev%2Fmobile%2Fpb_wa_l3%2F204")).apply();
        activityRule.launchActivity(new Intent());
        onView(withText(R.string.name_pb_wa_l3)).check(matches(isDisplayed()));
        onView(withText("204")).check(matches(isDisplayed()));
        assertFalse(preferences.contains(InstallListener.REFERRER));
    }

}