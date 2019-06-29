package cn.vove7.bottomsheetdialog.builder

import android.content.Context
import cn.vove7.bottomdialog.builder.BindView
import cn.vove7.bottomdialog.builder.ListAdapterBuilder
import cn.vove7.bottomdialog.builder.OnItemClick
import cn.vove7.bottomdialog.util.ObservableList
import cn.vove7.bottomsheetdialog.R
import kotlinx.android.synthetic.main.item_app_list.view.*
import java.lang.Thread.sleep
import kotlin.concurrent.thread

/**
 * # AppListBuilder
 *
 * @author Vove
 * 2019/6/28
 */
class AppListBuilder(
        context: Context,
        autoDismiss: Boolean = true,
        private val applist: ObservableList<AppInfo> = ObservableList(),
        onItemClick: OnItemClick<AppInfo>

) : ListAdapterBuilder<AppInfo>(applist, autoDismiss, onItemClick) {

    init {
        loading = true
        thread {
            sleep(1500)
            loadAppList(context)
        }
    }

    override val itemView: (type: Int) -> Int = { R.layout.item_app_list }

    override val bindView: BindView<AppInfo> = { view, item ->
        view.text_1.text = item.name
        view.text_2.text = item.pkg
    }

    private fun loadAppList(context: Context) {
        val pm = context.packageManager
        applist.addAll(ObservableList.build {
            pm.getInstalledPackages(0)?.forEach {
                add(AppInfo(it.packageName, it.applicationInfo.loadLabel(pm)))
            }
        })
        loading = false
    }
}


data class AppInfo(
        val pkg: String,
        val name: CharSequence
)
