/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.menu

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mozilla.components.support.ktx.android.content.res.pxToDp

/**
 * A popup menu composed of BrowserMenuItem objects.
 */
class BrowserMenu internal constructor(
    private val adapter: BrowserMenuAdapter
) {
    private var currentPopup: PopupWindow? = null
    private var menuList: RecyclerView? = null

    @Suppress("UNUSED_PARAMETER")
    @SuppressLint("InflateParams")
    fun show(anchor: View, orientation: Orientation = Orientation.DOWN): PopupWindow {
        val rootView = LayoutInflater.from(anchor.context).inflate(R.layout.mozac_browser_menu, null)

        adapter.menu = this

        menuList = rootView.findViewById<RecyclerView>(R.id.mozac_browser_menu_recyclerView).apply {
            layoutManager = LinearLayoutManager(anchor.context, RecyclerView.VERTICAL, false)
            adapter = this@BrowserMenu.adapter
        }

        return PopupWindow(
                rootView,
                WindowManager.LayoutParams.WRAP_CONTENT,
            calculatePopupHeight(rootView)
        ).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isFocusable = true
            elevation = rootView.resources.pxToDp(MENU_ELEVATION_DP).toFloat()

            setOnDismissListener {
                adapter.menu = null
                currentPopup = null
            }

            showAsDropDown(anchor)
        }.also {
            currentPopup = it
        }
    }

    fun dismiss() {
        currentPopup?.dismiss()
    }

    fun invalidate() {
        menuList?.let { adapter.invalidate(it) }
    }

    companion object {
        private const val MENU_ELEVATION_DP = 8

        /**
         * Determines the orientation to be used for a menu based on the positioning of the [parent] in the layout.
         */
        fun determineMenuOrientation(parent: View): Orientation {
            val params = parent.layoutParams
            return if (params is CoordinatorLayout.LayoutParams) {
                if ((params.gravity and Gravity.BOTTOM) == Gravity.BOTTOM) {
                    Orientation.UP
                } else {
                    Orientation.DOWN
                }
            } else {
                Orientation.DOWN
            }
        }
    }

    enum class Orientation {
        UP,
        DOWN
    }

    private fun calculatePopupHeight(container: View): Int {
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        container.measure(spec, spec)

        return if (container.context.resources.displayMetrics.heightPixels > container.measuredHeight) {
            container.measuredHeight
        } else {
            // As the container is larger than available space let's use MATCH_PARENT as the height.
            WindowManager.LayoutParams.MATCH_PARENT
        }
    }
}

private fun determineVerticalOffset(orientation: BrowserMenu.Orientation, view: View, anchor: View): Int {
    return if (orientation == BrowserMenu.Orientation.DOWN) {
        // Menu should overlay anchor.
        -anchor.height
    } else {
        // Measure menu and then position menu above (and overlapping) anchor
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(spec, spec)
        -view.measuredHeight
    }
}
