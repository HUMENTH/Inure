package app.simple.inure.ui.viewers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import app.simple.inure.R
import app.simple.inure.constants.BundleConstants
import app.simple.inure.decorations.fastscroll.FastScrollerBuilder
import app.simple.inure.decorations.padding.PaddingAwareNestedScrollView
import app.simple.inure.decorations.ripple.DynamicRippleImageButton
import app.simple.inure.decorations.views.TypeFaceEditText
import app.simple.inure.decorations.views.TypeFaceTextView
import app.simple.inure.dialogs.miscellaneous.ErrorPopup
import app.simple.inure.extension.fragments.ScopedFragment
import app.simple.inure.popups.app.PopupXmlViewer
import app.simple.inure.util.ColorUtils.resolveAttrColor
import app.simple.inure.util.ViewUtils.invisible
import app.simple.inure.util.ViewUtils.visible
import app.simple.inure.viewmodels.factory.JSONViewModelFactory
import app.simple.inure.viewmodels.viewers.JSONViewerViewModel
import java.io.IOException

class JSONViewer : ScopedFragment() {

    private lateinit var json: TypeFaceEditText
    private lateinit var name: TypeFaceTextView
    private lateinit var loader: ProgressBar
    private lateinit var scrollView: PaddingAwareNestedScrollView
    private lateinit var options: DynamicRippleImageButton
    private lateinit var jsonViewModelFactory: JSONViewModelFactory
    private lateinit var jsonViewerViewModel: JSONViewerViewModel

    private var path: String? = null

    private val exportManifest = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri: Uri? ->
        if (uri == null) {
            // Back button pressed.
            return@registerForActivityResult
        }
        try {
            requireContext().contentResolver.openOutputStream(uri).use { outputStream ->
                if (outputStream == null) throw IOException()
                outputStream.write(json.text.toString().toByteArray())
                outputStream.flush()
                Toast.makeText(requireContext(), R.string.saved_successfully, Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), R.string.failed, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_json_viewer, container, false)

        json = view.findViewById(R.id.json_viewer)
        name = view.findViewById(R.id.json_name)
        scrollView = view.findViewById(R.id.json_nested_scroll_view)
        loader = view.findViewById(R.id.json_loader)
        options = view.findViewById(R.id.json_viewer_options)

        path = requireArguments().getString(BundleConstants.pathToJSON)!!
        packageInfo = requireArguments().getParcelable(BundleConstants.packageInfo)!!

        jsonViewModelFactory = JSONViewModelFactory(requireActivity().application,
                                                    packageInfo,
                                                    requireContext().resolveAttrColor(R.attr.colorAppAccent),
                                                    path!!)

        jsonViewerViewModel = ViewModelProvider(this, jsonViewModelFactory).get(JSONViewerViewModel::class.java)

        startPostponedEnterTransition()

        FastScrollerBuilder(scrollView).useMd2Style().build()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name.text = path

        jsonViewerViewModel.getSpanned().observe(viewLifecycleOwner, {
            json.setText(it)
            loader.invisible()
            options.visible()
        })

        jsonViewerViewModel.getError().observe(viewLifecycleOwner, {
            val e = ErrorPopup.newInstance(it)
            e.show(childFragmentManager, "error_dialog")
            e.setOnErrorDialogCallbackListener(object : ErrorPopup.Companion.ErrorDialogCallbacks {
                override fun onDismiss() {
                    requireActivity().onBackPressed()
                }
            })
        })

        options.setOnClickListener {
            val p = PopupXmlViewer(it)

            p.setOnPopupClickedListener(object : PopupXmlViewer.PopupXmlCallbacks {
                override fun onPopupItemClicked(source: String) {
                    when (source) {
                        getString(R.string.copy) -> {
                            val clipboard: ClipboardManager? = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                            val clip = ClipData.newPlainText("xml", json.text.toString())
                            clipboard?.setPrimaryClip(clip)
                        }
                        getString(R.string.save) -> {
                            val fileName: String = packageInfo.packageName + "_" + name.text
                            exportManifest.launch(fileName)
                        }
                    }
                }
            })
        }
    }

    companion object {
        fun newInstance(packageInfo: PackageInfo, path: String): JSONViewer {
            val args = Bundle()
            args.putParcelable(BundleConstants.packageInfo, packageInfo)
            args.putString(BundleConstants.pathToJSON, path)
            val fragment = JSONViewer()
            fragment.arguments = args
            return fragment
        }
    }
}