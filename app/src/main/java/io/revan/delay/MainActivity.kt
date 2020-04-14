package io.revan.delay

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup_help.view.*

class MainActivity : AppCompatActivity(), ChooseAppFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_help -> {
                val popupView = (baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                        as LayoutInflater).inflate(R.layout.popup_help, null)

                val popup = PopupWindow(
                    popupView,
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT)
                popupView.close_button.setOnClickListener {
                    popup.dismiss()
                }
                val dismissCallback = object: OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        popup.dismiss()
                    }
                }
                popup.setOnDismissListener { dismissCallback.remove() }
                onBackPressedDispatcher.addCallback(this, dismissCallback)
                popup.showAtLocation(popupView, Gravity.CENTER, 0, 0)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onListFragmentInteraction(item: InstalledApp?) {
        findNavController(R.id.nav_host_fragment).navigate(
            R.id.action_chooseAppFragment_to_configureShortcutFragment,
            Bundle().apply {
                putString("pkg", item?.pkg)
                putString("cls", item?.cls)
                putString("name", item?.name)
            }
        )
    }
}
