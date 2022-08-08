package app.simple.inure.activities.app

import android.app.ActivityManager
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import app.simple.inure.R
import app.simple.inure.decorations.ripple.DynamicRippleTextView
import app.simple.inure.decorations.typeface.TypeFaceTextView
import app.simple.inure.decorations.views.CustomProgressBar
import app.simple.inure.dialogs.app.Sure
import app.simple.inure.extensions.activities.BaseActivity
import app.simple.inure.util.ViewUtils.gone
import app.simple.inure.util.ViewUtils.visible
import app.simple.inure.viewmodels.activity.ManageSpaceViewModel

class ManageSpace : BaseActivity() {

    private lateinit var clearData: DynamicRippleTextView
    private lateinit var clearTrackersData: DynamicRippleTextView

    private lateinit var trackersSize: TypeFaceTextView
    private lateinit var trackersLoader: CustomProgressBar

    private lateinit var manageSpaceViewModel: ManageSpaceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_space)
        manageSpaceViewModel = ViewModelProvider(this)[ManageSpaceViewModel::class.java]

        clearData = findViewById(R.id.clear_app_data)
        clearTrackersData = findViewById(R.id.clear_tracker_data)
        trackersSize = findViewById(R.id.trackers_cache_size)
        trackersLoader = findViewById(R.id.trackers_cache_loader)

        clearTrackersData.gone(animate = false)
        trackersSize.gone(animate = false)

        clearData.setOnClickListener {
            clearAppData()
        }

        manageSpaceViewModel.trackersCacheSize.observe(this) {
            trackersSize.visible(animate = true)
            trackersSize.text = it
            trackersLoader.gone(animate = true)
            clearTrackersData.visible(animate = true)

            clearTrackersData.setOnClickListener {
                val p = Sure.newInstance()
                p.setOnSureCallbackListener(object : Sure.Companion.SureCallbacks {
                    override fun onSure() {
                        trackersLoader.visible(animate = true)
                        manageSpaceViewModel.clearTrackersData()
                    }
                })

                p.show(supportFragmentManager, "sure")
            }
        }
    }

    private fun clearAppData() {
        val p = Sure.newInstance()
        p.setOnSureCallbackListener(object : Sure.Companion.SureCallbacks {
            override fun onSure() {
                (applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager)
                    .clearApplicationUserData() // note: it has a return value!
            }
        })

        p.show(supportFragmentManager, "sure")
    }
}