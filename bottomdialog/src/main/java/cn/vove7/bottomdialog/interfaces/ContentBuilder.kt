package cn.vove7.bottomdialog.interfaces

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import cn.vove7.bottomdialog.BottomDialog

/**
 * # ContentBuilder
 *
 * @author Vove
 * 2019/6/24
 */
abstract class ContentBuilder {

    lateinit var contentView: View

    /**
     * 底部布局资源
     */
    internal abstract val layoutRes: Int
    lateinit var dialog: BottomDialog

    abstract fun init(view: View)

    fun build(context: Context, dialog: BottomDialog): View {
        this.dialog = dialog
        try {
            contentView
        } catch (e: Exception) {
            contentView = LayoutInflater.from(context).inflate(layoutRes, null, false)
            init(contentView)
            updateContent(-1)
        }

        return contentView
    }

    /**
     * 更新内容 渲染 装饰
     * 初始化时，type为-1
     * 供[listenToUpdate]监听
     * @param type Int 可用于局部刷新
     */
    abstract fun updateContent(type: Int, data: Any? = null)

}