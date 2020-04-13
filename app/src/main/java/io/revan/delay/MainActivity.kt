package io.revan.delay

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.findNavController

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ChooseAppFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
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
