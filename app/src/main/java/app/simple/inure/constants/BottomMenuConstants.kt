package app.simple.inure.constants

import app.simple.inure.R

object BottomMenuConstants {
    private val allAppsBottomMenuItems = arrayListOf(
            R.drawable.ic_sort,
            R.drawable.ic_filter,
            R.drawable.ic_settings,
            -1,
            R.drawable.ic_search
    )

    fun getAllAppsBottomMenuItems(): ArrayList<Int> {
        return allAppsBottomMenuItems
    }
}