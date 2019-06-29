package cn.vove7.bottomdialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import cn.vove7.bottomdialog.builder.BottomDialogBuilder

/**
 * # BottomDialogActivity
 *
 * @author Vove
 * 2019/6/29
 */

class BottomDialogActivity : AppCompatActivity() {

    private lateinit var dialog: BottomDialog

    companion object {
        /**
         * 构造器
         * @param context Context
         * @param action [@kotlin.ExtensionFunctionType] Function1<BottomDialogBuilder, Unit>
         */
        fun builder(context: Context, action: BottomDialogBuilder.() -> Unit) {
            val b = BottomDialogBuilder(context).apply(action)
            start(context, b)
        }

        @Synchronized
        private fun start(context: Context, dialog: BottomDialogBuilder) {
            val i = Intent(context, BottomDialogActivity::class.java)
            i.putExtra("tag", dialog.hashCode())
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            initCache(dialog)
            context.startActivity(i)
        }

        @Synchronized
        private fun initCache(dialog: BottomDialogBuilder) {
            if (dialogArray == null) {
                dialogArray = SparseArray()
            }
            dialogArray?.put(dialog.hashCode(), dialog)
        }

        @Synchronized
        private fun clearCache(tag: Int) {
            dialogArray?.remove(tag)
            if (dialogArray?.size() == 0) {
                dialogArray = null
            }
        }

        private var dialogArray: SparseArray<BottomDialogBuilder>? = null
    }

    private var dialogTag: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = intent
        if (i?.hasExtra("tag") != true) {
            finish()
            return
        }
        dialogTag = i.getIntExtra("tag", 0)
        try {
            dialog = BottomDialog(dialogArray?.get(dialogTag)!!.also { it.context = this@BottomDialogActivity })
        } catch (e: Exception) {
            throw Exception("出现意外：\n(1) 请使用 BottomDialog.activity 创建BottomDialogActivity")
        }
        dialog.setOnDismissListener {
            finish()
        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearCache(dialogTag)
    }
}