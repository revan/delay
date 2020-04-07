package io.revan.delay

import android.content.ComponentName
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import kotlinx.android.synthetic.main.activity_delay_launch.*
import java.lang.IllegalArgumentException

private const val TIMER_LENGTH = 10L * 1000L  // TODO: pass this as intent
private const val TIMER_UNIT_MS = 1000L

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class DelayLaunchActivity : AppCompatActivity() {

    var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_delay_launch)

        intent.extras!!.let {intent ->
            val proxiedIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_TASK_ON_HOME or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                component = ComponentName(
                    intent.getString("pkg") ?: throw IllegalArgumentException("pkg expected"),
                    intent.getString("cls") ?: throw IllegalArgumentException("cls expected")
                )
            }

            timer = object: CountDownTimer(TIMER_LENGTH, TIMER_UNIT_MS) {
                override fun onFinish() {
                    fullscreen_content.text = getString(R.string.countdown_done)
                    startActivity(proxiedIntent)
                }

                override fun onTick(millisUntilFinished: Long) {
                    fullscreen_content.text = getString(
                        R.string.countdown_template, millisUntilFinished / 1000 + 1)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        timer!!.start()
    }

    override fun onPause() {
        super.onPause()

        timer!!.cancel()
    }

}
