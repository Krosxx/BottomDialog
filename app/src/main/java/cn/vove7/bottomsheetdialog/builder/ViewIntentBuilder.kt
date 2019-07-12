package cn.vove7.bottomsheetdialog.builder

import android.content.Intent
import android.content.pm.ResolveInfo
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import cn.vove7.bottomdialog.builder.BindView
import cn.vove7.bottomdialog.builder.ListAdapterBuilder
import cn.vove7.bottomdialog.builder.OnItemClick
import cn.vove7.bottomdialog.util.ObservableList
import cn.vove7.bottomsheetdialog.R
import kotlinx.android.synthetic.main.item_intent.view.*
import kotlin.concurrent.thread

/**
 * # ViewIntentBuilder
 *
 * @author Vove
 * 2019/7/11
 */
class ViewIntentBuilder(
        val intent: Intent,
        items: ObservableList<ResolveInfo> = ObservableList(),
        onItemClick: OnItemClick<ResolveInfo>?
) : ListAdapterBuilder<ResolveInfo>(
        items, true, onItemClick
) {
    override val itemView: (type: Int) -> Int = { R.layout.item_intent }
    override val layoutManager: RecyclerView.LayoutManager by lazy { GridLayoutManager(context, 4) }

    override fun init(view: View) {
        super.init(view)
        load()
    }


    val pm get() = dialog.context.packageManager

    private fun load() {
        thread {
            val list = pm.queryIntentActivities(intent, 0).filter {
                it.activityInfo.name != null && it.activityInfo.exported
                true
            }
            items.addAll(list)
        }
    }

    override val bindView: BindView<ResolveInfo> = { view, item ->
        view.icon.setImageDrawable(item.loadIcon(pm))
        view.text.setText(item.loadLabel(pm))
    }
}