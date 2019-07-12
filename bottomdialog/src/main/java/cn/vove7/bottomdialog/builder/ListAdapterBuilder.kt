package cn.vove7.bottomdialog.builder

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import cn.vove7.bottomdialog.R
import cn.vove7.bottomdialog.interfaces.ContentBuilder
import cn.vove7.bottomdialog.util.*
import kotlinx.android.synthetic.main.content_list.view.*

/**
 * # ListAdapterBuilder
 * 内容布局使用[RecyclerView]
 *
 * @param T
 * @property items ObservableList<T> 列表项 T 可实现[Typeable]，区分元素类型
 * @property loading Boolean 加载标志
 * @property itemView Function1<Int, Int> 元素布局资源
 * @property bindView Function2<View, T, Unit> 绑定视图事件
 * @property layoutManager LayoutManager
 * @constructor
 * @author Vove
 * 2019/6/25
 */
abstract class ListAdapterBuilder<T>(
        items: ObservableList<T>,
        autoDismiss: Boolean = true,
        onItemClick: OnItemClick<T>?
) : ContentBuilder() {

    /**
     * 元素列表
     */
    val items: ObservableList<T> by listenListToUpdate(items, this)

    /**
     * 数据加载标志
     * 若数据异步加载，可通过loading设置加载UI
     */
    var loading = false
        set(value) {
            runOnUi {
                try {
                    if (value) loadingBar.fadeIn()
                    else loadingBar.fadeOut(endStatus = View.INVISIBLE)
                } catch (e: Exception) {
                }
            }
            field = value
        }

    val context: Context get() = dialog.context

    //布局
    override val layoutRes: Int = R.layout.content_list

    open val adapter: RecyclerView.Adapter<*> by lazy {
        ListAdapter(context, items, onItemClick, autoDismiss, dialog, itemView, bindView)
    }

    abstract val itemView: (type: Int) -> Int

    abstract val bindView: BindView<T>

    open val layoutManager: RecyclerView.LayoutManager
            by lazy { LinearLayoutManager(context) }

    lateinit var recyclerView: RecyclerView
        private set

    lateinit var loadingBar: ProgressBar

    override fun init(view: View) {
        recyclerView = view.list_view
        loadingBar = view.loading_bar

        if (loading) loadingBar.fadeIn()
        else loadingBar.visibility = View.INVISIBLE

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun updateContent(type: Int, data: Any?) {
        try {
            when (type) {
                1 -> {
                    adapter.notifyItemInserted(data as Int)
                }
                2 -> {
                    val p = data as Pair<Int, Int>
                    adapter.notifyItemRangeRemoved(p.first, p.second - p.first + 1)
                }
                else -> {
                    adapter.notifyDataSetChanged()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}

typealias OnItemClick<T> = (dialog: Dialog, position: Int, item: T, isLongClick: Boolean) -> Unit
typealias BindView<T> = (itemView: View, item: T) -> Unit

class ListAdapter<T>(
        context: Context,
        private val items: MutableList<T>,
        private val onItemClick: OnItemClick<T>?,
        private val autoDismiss: Boolean,
        private val dialog: Dialog,
        val itemView: (Int) -> Int,
        val bindView: BindView<T>
) : RecyclerView.Adapter<VH>() {
    private val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return if (item is Typeable) {
            item.getType()
        } else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): VH {
        val view = inflater.inflate(itemView(type), parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(v: VH, pos: Int) {
        bindView(v.itemView, items[pos])
        v.itemView.apply {
            setOnClickListener {
                onItemClick?.invoke(dialog, v.adapterPosition, items[v.adapterPosition], false)
                if (autoDismiss) dialog.dismiss()
            }
            setOnLongClickListener {
                onItemClick?.invoke(dialog, v.adapterPosition, items[v.adapterPosition], true)
                true
            }
        }
    }
}

class VH(itemView: View) : RecyclerView.ViewHolder(itemView)

interface Typeable {
    fun getType(): Int
}