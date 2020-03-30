package io.revan.delayedstart

import android.content.ComponentName
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_delay_launch.*
import java.lang.IllegalArgumentException

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class DelayLaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_delay_launch)

        intent.extras!!.let {intent ->
            val proxiedIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                flags = Intent.FLAG_ACTIVITY_TASK_ON_HOME or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                component = ComponentName(
                    intent.getString("pkg") ?: throw IllegalArgumentException("pkg expected"),
                    intent.getString("cls") ?: throw IllegalArgumentException("cls expected")
                )
            }

            // TODO: have this trigger on a timer rather than button
            dummy_button.setOnClickListener {
                startActivity(proxiedIntent)
            }
        }
    }

}
