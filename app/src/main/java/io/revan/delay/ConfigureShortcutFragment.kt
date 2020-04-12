package io.revan.delay

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.pm.ShortcutInfoCompat
import android.support.v4.content.pm.ShortcutManagerCompat
import android.support.v4.graphics.drawable.IconCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_choose_app.view.*
import kotlinx.android.synthetic.main.fragment_configure_shortcut.*
import kotlinx.android.synthetic.main.fragment_configure_shortcut.view.*
import kotlinx.android.synthetic.main.fragment_configure_shortcut.view.launch_delay_input
import java.util.*

private const val DELAY_MIN_SECS = 1
private const val DELAY_MAX_SECS = 60
private const val DELAY_DEFAULT_SECS = 10

/**
 * A simple [Fragment] subclass.
 * Use the [ConfigureShortcutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        view.icon.setImageDrawable(context!!.packageManager.getApplicationIcon(pkg))
        view.content.text = name

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance(pkg: String, cls: String, name: String) =
            ConfigureShortcutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PACKAGE, pkg)
                    putString(ARG_CLASS, cls)
                    putString(ARG_NAME, name)
                }
            }
    }

    private fun createShortcut(pkg: String, cls: String, name: String)  {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            putExtra(ARG_PACKAGE, pkg)
            putExtra(ARG_CLASS, cls)
            putExtra(ARG_DELAY, launch_delay_input.value)
            component = ComponentName("io.revan.delay", "io.revan.delay.DelayLaunchActivity")
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }

        val pinShortcutInfo = ShortcutInfoCompat.Builder(context!!, UUID.randomUUID().toString())
            .setIcon(makeIconCompatFromDrawable(context!!.packageManager.getApplicationIcon(pkg)))
            .setIntent(intent)
            .setShortLabel(name)
            .build()

        // TODO handle this failing (lower API versions?)
        val pinnedShortcutCallbackIntent = ShortcutManagerCompat.createShortcutResultIntent(context!!, pinShortcutInfo)

        // Configure the intent so that your app's broadcast receiver gets
        // the callback successfully.For details, see PendingIntent.getBroadcast().
        val successCallback = PendingIntent.getBroadcast(context, /* request code */ 0,
            pinnedShortcutCallbackIntent, /* flags */ 0)

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
