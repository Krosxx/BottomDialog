package cn.vove7.bottomdialog.util

/**
 * # ObservableList
 * 带监听接口的List
 * 暂不支持sort
 *
 * @author Vove
 * 2019/6/25
 */
class ObservableList<T> : ArrayList<T> {


    companion object {
        inline fun <reified T> build(builderAction: ObservableList<T> .() -> Unit)
                : ObservableList<T> =
            ObservableList<T>().apply(builderAction)

    }

    constructor(listener: DataCahngedListener<T>? = null) : super() {
        this.listener = listener
    }

    constructor(c: MutableCollection<out T>) : super(c)

    var listener: DataCahngedListener<T>? = null

    override fun clear() {
        super.clear()
        listener?.onClear()
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val r = super.addAll(elements)
        if (r) listener?.onAddAll(elements)
        return r
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val r = super.removeAll(elements)
        if (r) listener?.onRemove(elements)
        return r
    }

    override fun add(element: T): Boolean {
        val r = super.add(element)
        if (r) listener?.onAdd(element)
        return r
    }

    override fun removeRange(fromIndex: Int, toIndex: Int) {
        super.removeRange(fromIndex, toIndex)
        listener?.onRemoveRange(fromIndex, toIndex)
    }

    override fun add(index: Int, element: T) {
        super.add(index, element)
        listener?.onAdd(index, element)
    }

    override fun remove(element: T): Boolean {
        val r = super.remove(element)
        listener?.onRemove(element)
        return r
    }

    override fun set(index: Int, element: T): T {
        val r = super.set(index, element)
        listener?.onSet(index, element)
        return r
    }

    override fun removeAt(index: Int): T {
        val r = super.removeAt(index)
        listener?.onRemoveAt(index)
        return r
    }

}

interface DataCahngedListener<T> {
    fun onClear() {}

    fun onAddAll(elements: Collection<T>)

    fun onRemove(elements: Collection<T>)
    fun onRemove(elements: T)

    fun onAdd(element: T)
    fun onAdd(index: Int, element: T)

    fun onRemoveRange(fromIndex: Int, toIndex: Int)

    fun onSet(index: Int, element: T)
    fun onRemoveAt(index: Int)
}