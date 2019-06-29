package cn.vove7.bottomdialog

import android.support.design.widget.BottomSheetBehavior
import android.view.View

/**
 * # BehaviorController
 *
 * @author Vove
 * 2019/6/24
 */
class BehaviorController(view: View, lis: StatusCallback) {


    private val behavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(view)

    var isHideable: Boolean
        set(value) {
            behavior.isHideable = value
        }
        get() = behavior.isHideable

    var peekHeight :Int
        set(value) {
            behavior.peekHeight = value
        }
        get() = behavior.peekHeight

    init {
        behavior.peekHeight = 800
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                lis.onSlide(p1)
            }

            override fun onStateChanged(p0: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> lis.onHidden()
                    BottomSheetBehavior.STATE_EXPANDED -> lis.onExpand()
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> lis.onHalfExpand()
                }
            }

        })
    }

    fun hide() {
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun halfExpand() {
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    fun expand() {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

}

interface StatusCallback {
    fun onHidden() {}
    fun onExpand() {}
    fun onHalfExpand() {}
    fun onSlide(slideOffset: Float)
}