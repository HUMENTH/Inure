package app.simple.inure.ui.viewers

import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import app.simple.inure.R
import app.simple.inure.adapters.details.AdapterGraphics
import app.simple.inure.constants.BundleConstants
import app.simple.inure.decorations.overscroll.CustomVerticalRecyclerView
import app.simple.inure.decorations.ripple.DynamicRippleImageButton
import app.simple.inure.decorations.typeface.TypeFaceEditTextDynamicCorner
import app.simple.inure.decorations.typeface.TypeFaceTextView
import app.simple.inure.extensions.fragments.ScopedFragment
import app.simple.inure.factories.panels.PackageInfoFactory
import app.simple.inure.popups.viewers.PopupGraphicsFilter
import app.simple.inure.popups.viewers.PopupGraphicsMenu
import app.simple.inure.preferences.DevelopmentPreferences
import app.simple.inure.preferences.GraphicsPreferences
import app.simple.inure.util.FragmentHelper
import app.simple.inure.util.NullSafety.isNull
import app.simple.inure.util.ViewUtils.gone
import app.simple.inure.util.ViewUtils.visible
import app.simple.inure.viewmodels.viewers.GraphicsViewModel

class Graphics : ScopedFragment() {

    private lateinit var options: DynamicRippleImageButton
    private lateinit var recyclerView: CustomVerticalRecyclerView
    private lateinit var search: DynamicRippleImageButton
    private lateinit var title: TypeFaceTextView
    private lateinit var filter: DynamicRippleImageButton
    private lateinit var searchBox: TypeFaceEditTextDynamicCorner
    private var adapterGraphics: AdapterGraphics? = null
    private lateinit var graphicsViewModel: GraphicsViewModel
    private lateinit var packageInfoFactory: PackageInfoFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_graphics, container, false)

        options = view.findViewById(R.id.graphics_options)
        recyclerView = view.findViewById(R.id.graphics_recycler_view)
        search = view.findViewById(R.id.graphics_search_btn)
        searchBox = view.findViewById(R.id.graphics_search)
        title = view.findViewById(R.id.graphics_title)
        filter = view.findViewById(R.id.graphics_filter)
        packageInfo = requireArguments().getParcelable(BundleConstants.packageInfo)!!
        packageInfoFactory = PackageInfoFactory(packageInfo)
        graphicsViewModel = ViewModelProvider(this, packageInfoFactory).get(GraphicsViewModel::class.java)

        searchBoxState(animate = false)
        startPostponedEnterTransition()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graphicsViewModel.getGraphics().observe(viewLifecycleOwner) {
            if (recyclerView.adapter.isNull()) {
                adapterGraphics = AdapterGraphics(packageInfo.applicationInfo.sourceDir, it, searchBox.text.toString().trim())
                recyclerView.adapter = adapterGraphics

                adapterGraphics!!.setOnResourceClickListener(object : AdapterGraphics.GraphicsCallbacks {
                    override fun onGraphicsClicked(path: String, filePath: String, view: ViewGroup, xOff: Float, yOff: Float) {
                        FragmentHelper.openFragment(requireActivity().supportFragmentManager,
                                                    ImageViewer.newInstance(packageInfo.applicationInfo.sourceDir, filePath),
                                                    "image_viewer")
                    }

                    override fun onGraphicsLongPressed(filePath: String) {
                        if (DevelopmentPreferences.isWebViewXmlViewer()) {
                            FragmentHelper.openFragment(requireActivity().supportFragmentManager,
                                                        XMLViewerWebView.newInstance(packageInfo, false, filePath),
                                                        "wv_xml")
                        } else {
                            FragmentHelper.openFragment(requireActivity().supportFragmentManager,
                                                        XMLViewerTextView.newInstance(packageInfo, false, filePath),
                                                        "tv_xml")
                        }
                    }
                })
            } else {
                adapterGraphics?.updateData(it, keyword = searchBox.text.toString())
            }

            searchBox.doOnTextChanged { text, _, _, _ ->
                if (searchBox.isFocused) {
                    graphicsViewModel.keyword = text.toString().trim()
                }
            }
        }

        graphicsViewModel.error.observe(viewLifecycleOwner) {
            showError(it)
        }

        graphicsViewModel.notFound.observe(viewLifecycleOwner) {
            showWarning(R.string.no_graphics_found)
        }

        filter.setOnClickListener {
            PopupGraphicsFilter(it)
        }

        options.setOnClickListener {
            PopupGraphicsMenu(it)
        }

        search.setOnClickListener {
            if (searchBox.text.isNullOrEmpty()) {
                GraphicsPreferences.setSearchVisibility(!GraphicsPreferences.isSearchVisible())
            } else {
                searchBox.text?.clear()
            }
        }
    }

    private fun searchBoxState(animate: Boolean) {
        if (GraphicsPreferences.isSearchVisible()) {
            search.setIcon(R.drawable.ic_close, animate)
            title.gone()
            searchBox.visible(animate)
            searchBox.showInput()
        } else {
            search.setIcon(R.drawable.ic_search, animate)
            title.visible(animate)
            searchBox.gone()
            searchBox.hideInput()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            GraphicsPreferences.graphicsSearch -> {
                searchBoxState(animate = true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapterGraphics?.unregister()
    }

    companion object {
        fun newInstance(packageInfo: PackageInfo): Graphics {
            val args = Bundle()
            args.putParcelable(BundleConstants.packageInfo, packageInfo)
            val fragment = Graphics()
            fragment.arguments = args
            return fragment
        }
    }
}