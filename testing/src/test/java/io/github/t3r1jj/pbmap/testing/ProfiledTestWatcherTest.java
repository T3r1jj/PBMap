package io.github.t3r1jj.pbmap.testing;

import android.util.Log;

import androidx.test.runner.screenshot.Screenshot;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * In docs we believe
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {Log.class, Screenshot.class})
public class ProfiledTestWatcherTest {

    private final VerifyAnswer verifyAnswer = new VerifyAnswer();

    @Test
    public void testProfiledLog() {
        ProfiledTestWatcher profiledTestWatcher = new ProfiledTestWatcher();
        String mockedName = "mocked name";
        PowerMockito.mockStatic(Log.class);
        when(Log.i(eq(ProfiledTestWatcher.class.getSimpleName()), argThat(s -> s.contains(mockedName) && s.matches(".*\\dms"))))
                .then(verifyAnswer);
        profiledTestWatcher.starting(Description.createTestDescription(ProfiledTestWatcherTest.class, mockedName));
        profiledTestWatcher.finished(Description.createTestDescription(ProfiledTestWatcherTest.class, mockedName));
        verifyAnswer.assertCalled();
    }

    @Test
    public void testProfiledLogWithoutStart() {
        ProfiledTestWatcher profiledTestWatcher = new ProfiledTestWatcher();
        String mockedName = "mocked name";
        PowerMockito.mockStatic(Log.class);
        when(Log.i(eq(ProfiledTestWatcher.class.getSimpleName()), argThat(s -> s.contains(mockedName) && s.matches(".*\\dms"))))
                .then(verifyAnswer);
        profiledTestWatcher.finished(Description.createTestDescription(ProfiledTestWatcherTest.class, mockedName));
        verifyAnswer.assertCalled();
    }

    @Test
    public void testProfiledLogWithoutFinish() {
        ProfiledTestWatcher profiledTestWatcher = new ProfiledTestWatcher();
        String mockedName = "mocked name";
        PowerMockito.mockStatic(Log.class);
        when(Log.i(eq(ProfiledTestWatcher.class.getSimpleName()), argThat(s -> s.contains(mockedName) && s.matches(".*\\dms"))))
                .then(verifyAnswer);
        profiledTestWatcher.starting(Description.createTestDescription(ProfiledTestWatcherTest.class, mockedName));
        verifyAnswer.assertNotCalled();
    }
}