package io.revan.delay

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

private const val SCREENSHOT_DIR = "/sdcard/Pictures/screenshots"
private const val MAIN_ACTIVITY = "io.revan.delay"
private const val AUTOMATION_TIMEOUT = 5000L
private const val TARGET_APP_NAME = "YouTube"
private const val TARGET_APP_PACKAGE = "com.google.android.youtube"
private const val EXPECTED_DELAY = 10000L

@RunWith(AndroidJUnit4::class)
class CreateShortcutTest {

    private lateinit var device: UiDevice
    private var screenshotCount = 0

    fun screenshot() {
        File(SCREENSHOT_DIR).mkdirs()
        device.executeShellCommand(
            "screencap -p %s/%d.png".format(SCREENSHOT_DIR, screenshotCount++))
        Thread.sleep(100)
    }

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        goHome()
        screenshot()
        if (!shortcutCreated) {
            createShortcut()
            shortcutCreated = true
        }
    }

    companion object {
        var shortcutCreated = false
    }

    private fun goHome() {
        device.pressHome()
        device.wait(
            Until.hasObject(By.pkg(device.launcherPackageName).depth(0)),
            AUTOMATION_TIMEOUT)
    }

    private fun openMainActivity() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = context.packageManager.getLaunchIntentForPackage(MAIN_ACTIVITY)!!.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        device.wait(
            Until.hasObject(By.pkg(MAIN_ACTIVITY).depth(0)),
            AUTOMATION_TIMEOUT)
    }

    private fun createShortcut() {
        openMainActivity()
        screenshot()

        val targetSelector = UiSelector().text(TARGET_APP_NAME)
        UiScrollable(UiSelector().scrollable(true)).scrollIntoView(targetSelector)
        screenshot()
        device.findObject(targetSelector).clickAndWaitForNewWindow()

        screenshot()
        device.findObject(UiSelector().text("CREATE SHORTCUT")).clickAndWaitForNewWindow()
        screenshot()
        device.findObject(UiSelector().text("Add automatically")).click()
    }

    private fun openShortcut() {
        device.findObject(UiSelector().text(TARGET_APP_NAME)).clickAndWaitForNewWindow()
    }

    @Test
    fun shortcutOpensApp() {
        openShortcut()
        screenshot()
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
        goHome()
        openShortcut()

        Assert.assertFalse(
            "Wrapped app should not have opened after " + partialWait / 1000 + " seconds.",
            device.findObject(UiSelector().packageName(TARGET_APP_PACKAGE))
                .waitForExists(partialWait + AUTOMATION_TIMEOUT)
        )
    }

}
