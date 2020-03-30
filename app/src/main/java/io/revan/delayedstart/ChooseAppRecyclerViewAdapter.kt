package io.revan.delayedstart

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_choose_app.view.*

class ChooseAppRecyclerViewAdapter(
    private val mValues: List<InstalledApp>,
    private val mListener: ChooseAppFragment.OnListFragmentInteractionListener?
) : RecyclerView.Adapter<ChooseAppRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as InstalledApp
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_choose_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mContentView.text = item.name
        holder.mIconView.setImageDrawable(item.icon)

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mContentView: TextView = mView.content
        val mIconView: ImageView = mView.icon

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}

data class InstalledApp(
    val pkg: String,
    val cls: String,
    val name: String,
    val icon: Drawable
)
