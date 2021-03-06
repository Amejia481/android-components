/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.feature.scroll

import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
import android.view.View
import mozilla.components.browser.session.SelectionAwareSessionObserver
import mozilla.components.browser.session.Session
import mozilla.components.browser.session.SessionManager
import mozilla.components.concept.engine.EngineView

/**
 * Feature implementation for connecting an [EngineView] with any View that you want to coordinate scrolling
 * behavior with.
 */
class ScrollFeature(
    sessionManager: SessionManager,
    private val engineView: EngineView,
    private val view: View,
    private val scrollFlags: Int = DEFAULT_SCROLL_FLAGS
) : SelectionAwareSessionObserver(sessionManager) {

    /**
     * Start feature: Starts adding scrolling behavior for the indicated view.
     */
    fun start() {
        observeSelected()
    }

    override fun onLoadingStateChanged(session: Session, loading: Boolean) {

        val params = view.layoutParams as AppBarLayout.LayoutParams

        if (engineView.canScrollVerticallyDown()) {
            params.scrollFlags = scrollFlags
        } else {
            params.scrollFlags = 0
        }

        view.layoutParams = params
    }

    companion object {
        const val DEFAULT_SCROLL_FLAGS = SCROLL_FLAG_SCROLL or
            SCROLL_FLAG_ENTER_ALWAYS or
            SCROLL_FLAG_SNAP or
            SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
    }
}
