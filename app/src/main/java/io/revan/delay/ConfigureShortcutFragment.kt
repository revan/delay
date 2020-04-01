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
import java.util.*

private const val ARG_PACKAGE = "pkg"
private const val ARG_CLASS = "cls"
private const val ARG_NAME = "name"

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

        createShortcut(pkg!!, cls!!, name!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configure_shortcut, container, false)
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
            putExtra("pkg", pkg)
            putExtra("cls", cls)
            component = ComponentName("io.revan.delay", "io.revan.delay.DelayLaunchActivity")
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
