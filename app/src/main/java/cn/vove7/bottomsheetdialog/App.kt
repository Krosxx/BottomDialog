package cn.vove7.bottomsheetdialog

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
//        refWatcher = LeakCanary.install(this)
    }

    companion object {
        private lateinit var refWatcher: RefWatcher
        fun watch(obj: Any) {
//            refWatcher.watch(obj)
        }

    }


}
