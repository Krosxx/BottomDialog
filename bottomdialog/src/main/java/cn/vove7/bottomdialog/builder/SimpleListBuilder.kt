package cn.vove7.bottomdialog.builder

import cn.vove7.bottomdialog.R
import cn.vove7.bottomdialog.util.ObservableList
import kotlinx.android.synthetic.main.item_simple_list.view.*

/**
 * # SimpleListBuilder
 * 简单列表
 * @author Vove
 * 2019/6/25
 */
class SimpleListBuilder(
        items: ObservableList<String?>,
        autoDismiss: Boolean = true,
        onItemClick: OnItemClick<String?>
) : ListAdapterBuilder<String?>(items, autoDismiss, onItemClick) {

    override val itemView: (type: Int) -> Int = { R.layout.item_simple_list }
    override val bindView: BindView<String?> = { view, item ->
        view.item_text.text = item
    }

}
