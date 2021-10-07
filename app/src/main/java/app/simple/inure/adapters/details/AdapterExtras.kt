package app.simple.inure.adapters.details

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.simple.inure.R
import app.simple.inure.decorations.ripple.DynamicRippleTextView
import app.simple.inure.decorations.viewholders.VerticalListViewHolder
import app.simple.inure.preferences.ExtrasPreferences
import app.simple.inure.preferences.SharedPreferences.getSharedPreferences
import app.simple.inure.util.StringUtils.highlightExtensions


class AdapterExtras(val list: MutableList<String>) : RecyclerView.Adapter<AdapterExtras.Holder>(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var extrasCallbacks: ExtrasCallbacks
    private var isHighlighted = ExtrasPreferences.isExtensionsHighlighted()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        getSharedPreferences().registerOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_resources, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.extra.text = if (isHighlighted) {
            list[position].highlightExtensions()
        } else {
            list[position]
        }

        holder.extra.setOnClickListener {
            extrasCallbacks.onExtrasClicked(list[position])
        }

        holder.extra.setOnLongClickListener {
            extrasCallbacks.onExtrasLongClicked(list[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            ExtrasPreferences.highlight -> {
                isHighlighted = ExtrasPreferences.isExtensionsHighlighted().also {
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun unregister() {
        getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
    }

    fun setOnResourceClickListener(resourceCallbacks: ExtrasCallbacks) {
        this.extrasCallbacks = resourceCallbacks
    }

    inner class Holder(itemView: View) : VerticalListViewHolder(itemView) {
        val extra: DynamicRippleTextView = itemView.findViewById(R.id.adapter_resources_name)
    }

    interface ExtrasCallbacks {
        fun onExtrasClicked(path: String)
        fun onExtrasLongClicked(path: String)
    }
}