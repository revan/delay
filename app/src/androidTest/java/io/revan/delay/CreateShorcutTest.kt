package io.revan.delay

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import androidx.test.uiautomator.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val MAIN_ACTIVITY = "io.revan.delay"
private const val AUTOMATION_TIMEOUT = 5000L
private const val TARGET_APP_NAME = "YouTube"
private const val TARGET_APP_PACKAGE = "com.google.android.youtube"
private const val EXPECTED_DELAY = 10000L


@RunWith(AndroidJUnit4::class)
class CreateShorcutTest {

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        goHome()
        openMainActivity()
        createShortcut()
    }

    private fun goHome() {
        device.pressHome()
        device.wait(
            Until.hasObject(By.pkg(device.launcherPackageName).depth(0)),
            AUTOMATION_TIMEOUT)
    }

    private fun openMainActivity() {
        val context = InstrumentationRegistry.getTargetContext()
        val intent = context.packageManager.getLaunchIntentForPackage(MAIN_ACTIVITY)!!.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        device.wait(
            Until.hasObject(By.pkg(MAIN_ACTIVITY).depth(0)),
            AUTOMATION_TIMEOUT)
    }

    private fun createShortcut() {
        val targetSelector = UiSelector().text(TARGET_APP_NAME)
        UiScrollable(UiSelector().scrollable(true)).scrollIntoView(targetSelector)
        device.findObject(targetSelector).clickAndWaitForNewWindow()

        device.findObject(UiSelector().text("Add automatically")).click()
    }

    private fun openShortcut() {
        goHome()
        device.findObject(UiSelector().text(TARGET_APP_NAME)).clickAndWaitForNewWindow()
    }

    @Test
    fun shortcutOpensApp() {
        openShortcut()
        Assert.assertTrue(
            "Wrapped app should be opened after " + EXPECTED_DELAY / 1000 + " seconds.",
            device.findObject(UiSelector().packageName(TARGET_APP_PACKAGE))
                .waitForExists(EXPECTED_DELAY + AUTOMATION_TIMEOUT)
        )
    }

    @Test
    fun losingFocusResetsTimer() {
        val partialWait = EXPECTED_DELAY / 3

        openShortcut()
        device.waitForIdle(partialWait)
        openShortcut()

        Assert.assertFalse(
            "Wrapped app should not have opened after " + partialWait / 1000 + " seconds.",
            device.findObject(UiSelector().packageName(TARGET_APP_PACKAGE))
                .waitForExists(partialWait + AUTOMATION_TIMEOUT)
        )
    }

}
