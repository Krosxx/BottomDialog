package cn.vove7.bottomdialog

import android.view.View
import androidx.annotation.IntDef
import com.google.android.material.bottomsheet.BottomSheetBehavior

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

    var peekHeight: Int
        set(value) {
            behavior.peekHeight = value
        }
        get() = behavior.peekHeight

    init {
        behavior.peekHeight = 800
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                lis.onSlide(p1)
            }

            override fun onStateChanged(p0: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> lis.onHidden()
                    BottomSheetBehavior.STATE_EXPANDED -> lis.onExpand()
                    BottomSheetBehavior.STATE_COLLAPSED -> lis.onCollapsed()
                }
            }

        })
    }

    fun hide() {
        isHideable = true
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun collapsed() {
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun expand() {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

}

interface StatusCallback {
    fun onHidden() {}
    fun onExpand() {}
    fun onCollapsed() {}
    fun onSlide(slideOffset: Float)
}