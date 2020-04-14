package io.revan.delay

import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_choose_app.view.*
import kotlinx.android.synthetic.main.fragment_configure_shortcut.*
import kotlinx.android.synthetic.main.fragment_configure_shortcut.view.*
import kotlinx.android.synthetic.main.fragment_configure_shortcut.view.launch_delay_input
import java.util.*

private const val DELAY_MIN_SECS = 1
private const val DELAY_MAX_SECS = 60
private const val DELAY_DEFAULT_SECS = 10
private const val DELAY_ACTIVITY_CLASS = "io.revan.delay.DelayLaunchActivity"
private const val DELAY_ACTIVITY_PACKAGE = "io.revan.delay"
private const val SHORTCUT_INTENT_ACTION = "SHORTCUT_INTENT_ACTION"


class ConfigureShortcutFragment : Fragment() {
    private var pkg: String? = null
    private var cls: String? = null
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            pkg = it.getString(ARG_PACKAGE)
            cls = it.getString(ARG_CLASS)
            name = it.getString(ARG_NAME)
        }

        // TODO display error if any fields are missing
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configure_shortcut, container, false)

        view.create_shortcut_button.setOnClickListener {
            createShortcut(pkg!!, cls!!, name!!)
        }
        view.launch_delay_input.apply {
            minValue = DELAY_MIN_SECS
            maxValue = DELAY_MAX_SECS
            wrapSelectorWheel = false
            value = DELAY_DEFAULT_SECS
        }
        view.icon.setImageDrawable(context!!.packageManager.getApplicationIcon(pkg!!))
        view.content.text = name

        return view
    }

    private fun createShortcut(pkg: String, cls: String, name: String)  {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            putExtra(ARG_PACKAGE, pkg)
            putExtra(ARG_CLASS, cls)
            putExtra(ARG_DELAY, launch_delay_input.value)
            component = ComponentName(DELAY_ACTIVITY_PACKAGE, DELAY_ACTIVITY_CLASS)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }

        val pinShortcutInfo = ShortcutInfoCompat.Builder(context!!, UUID.randomUUID().toString())
            .setIcon(makeIconCompatFromDrawable(context!!.packageManager.getApplicationIcon(pkg)))
            .setIntent(intent)
            .setShortLabel(name)
            .build()

        val pinnedShortcutCallbackIntent = ShortcutManagerCompat.createShortcutResultIntent(
            context!!, pinShortcutInfo)
        pinnedShortcutCallbackIntent.action = SHORTCUT_INTENT_ACTION

        val successCallback = PendingIntent.getBroadcast(
            context, 0, pinnedShortcutCallbackIntent,0)

        val receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Only triggers for successful results.
                Toast.makeText(
                    context, R.string.shortcut_created_confirmation, Toast.LENGTH_SHORT).show()

                if (context is Activity) {
                    context.unregisterReceiver(this)
                    context.finish()
                }
            }
        }
        context!!.registerReceiver(receiver, IntentFilter(pinnedShortcutCallbackIntent.action))

        ShortcutManagerCompat.requestPinShortcut(context!!, pinShortcutInfo,
            successCallback.intentSender)

    }

    private fun makeIconCompatFromDrawable(drawable: Drawable): IconCompat {
        val bitmap = Bitmap.createBitmap(
            if (drawable.intrinsicWidth == -1) 1 else drawable.intrinsicWidth,
            if (drawable.intrinsicHeight == -1) 1 else drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        Canvas(bitmap).let {
            drawable.setBounds(0, 0, it.width, it.height)
            drawable.draw(it)
        }
        return IconCompat.createWithBitmap(bitmap)
    }
}
