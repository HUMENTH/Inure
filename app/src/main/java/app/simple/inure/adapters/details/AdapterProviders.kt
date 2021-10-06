package app.simple.inure.adapters.details

import android.content.pm.PackageInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import app.simple.inure.R
import app.simple.inure.apk.utils.ProvidersUtils
import app.simple.inure.decorations.ripple.DynamicRippleConstraintLayout
import app.simple.inure.decorations.viewholders.VerticalListViewHolder
import app.simple.inure.decorations.views.TypeFaceTextView
import app.simple.inure.glide.util.ImageLoader.loadIconFromProviderInfo
import app.simple.inure.model.ProviderInfoModel

class AdapterProviders(private val providers: MutableList<ProviderInfoModel>, private val packageInfo: PackageInfo)
    : RecyclerView.Adapter<AdapterProviders.Holder>() {

    private lateinit var providersCallbacks: ProvidersCallbacks

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_providers, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.icon.loadIconFromProviderInfo(providers[position].providerInfo)

        holder.name.text = providers[position].name.substring(providers[position].name.lastIndexOf(".") + 1)
        holder.process.text = providers[position].name

        holder.status.text = providers[position].status
        holder.authority.text = providers[position].authority

        holder.container.setOnLongClickListener {
            providersCallbacks
                    .onProvidersLongPressed(
                        providers[holder.absoluteAdapterPosition].name,
                        packageInfo,
                        it,
                        ProvidersUtils.isEnabled(holder.itemView.context, packageInfo.packageName, providers[holder.absoluteAdapterPosition].name),
                        holder.absoluteAdapterPosition)
            true
        }
    }

    override fun getItemCount(): Int {
        return providers.size
    }

    inner class Holder(itemView: View) : VerticalListViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.adapter_providers_icon)
        val name: TypeFaceTextView = itemView.findViewById(R.id.adapter_providers_name)
        val process: TypeFaceTextView = itemView.findViewById(R.id.adapter_providers_package)
        val status: TypeFaceTextView = itemView.findViewById(R.id.adapter_providers_status)
        val authority: TypeFaceTextView = itemView.findViewById(R.id.adapter_providers_authority)
        val container: DynamicRippleConstraintLayout = itemView.findViewById(R.id.adapter_providers_container)
    }

    fun setOnProvidersCallbackListener(providersCallbacks: ProvidersCallbacks) {
        this.providersCallbacks = providersCallbacks
    }

    companion object {
        interface ProvidersCallbacks {
            fun onProvidersLongPressed(packageId: String, packageInfo: PackageInfo, icon: View, isComponentEnabled: Boolean, position: Int)
        }
    }
}