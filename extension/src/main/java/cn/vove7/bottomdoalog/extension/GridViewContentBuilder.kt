package cn.vove7.bottomdoalog.extension

import android.view.View
import android.widget.BaseAdapter
import android.widget.GridView
import cn.vove7.bottomdialog.interfaces.ContentBuilder
import cn.vove7.bottomdialog.util.ObservableList
import kotlinx.android.synthetic.main.content_grid.view.*

/**
 * # GridViewContentBuilder
 *
 * @author Vove
 * 2019/7/11
 */
abstract class GridViewContentBuilder<T> : ContentBuilder() {
    override val layoutRes: Int
        get() = R.layout.content_grid

    val dataset = ObservableList<T>()

    abstract val numColumns: Int
    lateinit var gridView: GridView
    abstract val gridAdapter: BaseAdapter

    override fun init(view: View) {
        gridView = view.grid_view
        gridView.adapter = gridAdapter
        gridView.numColumns = numColumns
    }

    override fun updateContent(type: Int, data: Any?) {
        gridAdapter.notifyDataSetChanged()
    }
}