package cn.vove7.bottomdialog.util

import cn.vove7.bottomdialog.interfaces.ContentBuilder
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * # observable
 *
 * @author Vove
 * 2019/6/25
 */
fun <T> listenToUpdate(initialValue: T, builder: ContentBuilder, type: Int = 0)
        : ReadWriteProperty<Any?, T> = ChangeToNotify(initialValue, builder, type)

class ChangeToNotify<T>(
        initialValue: T,
        private val builder: ContentBuilder,
        private val type: Int
) : ObservableProperty<T>(initialValue) {

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        runOnUi {
            try {//view 未初始化, 可能导致异常
                builder.updateContent(type)
            } catch (e: Exception) {
            }
        }
    }
}

/**
 * 监听列表变动
 * 通知Adapter
 * @param initialValue ObservableList<T>
 * @param builder ContentBuilder
 * @return ListChangeToNotify<T>
 */
fun <T> listenListToUpdate(initialValue: ObservableList<T>, builder: ContentBuilder)
        : ListChangeToNotify<T> = ListChangeToNotify(initialValue, builder)


class ListChangeToNotify<T>(initialValue: ObservableList<T>, builder: ContentBuilder) {
    private val value = initialValue

    /**
     * 监听ObservableList变化，通知Adapter更新样式
     * 1: insert
     * 2: remove f->t
     * 3: changed
     */
    init {
        initialValue.listener = object : DataCahngedListener<T> {
            override fun onAddAll(elements: Collection<T>) {
                runOnUi {
                    builder.updateContent(3)
                }
            }

            override fun onRemove(elements: Collection<T>) {
                runOnUi {
                    builder.updateContent(3)
                }
            }

            override fun onRemove(elements: T) {
                runOnUi {
                    builder.updateContent(3)
                }
            }

            override fun onAdd(element: T) {
                runOnUi {
                    builder.updateContent(1, initialValue.size - 1)
                }
            }

            override fun onAdd(index: Int, element: T) {
                runOnUi {
                    builder.updateContent(1, index)
                }
            }

            override fun onRemoveRange(fromIndex: Int, toIndex: Int) {
                runOnUi {
                    builder.updateContent(2, Pair(fromIndex, toIndex))
                }
            }

            override fun onSet(index: Int, element: T) {
                runOnUi {
                    builder.updateContent(3, index)
                }
            }

            override fun onRemoveAt(index: Int) {
                runOnUi {
                    builder.updateContent(2, Pair(index, index))
                }
            }
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): ObservableList<T> {
        return value
    }

}