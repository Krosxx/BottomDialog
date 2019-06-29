package cn.vove7.bottomsheetdialog

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import cn.vove7.bottomdialog.BottomDialog
import cn.vove7.bottomdialog.BottomDialogActivity
import cn.vove7.bottomdialog.ToolbarHeader
import cn.vove7.bottomdialog.builder.*
import cn.vove7.bottomdialog.toolbar
import cn.vove7.bottomdialog.util.ObservableList
import cn.vove7.bottomsheetdialog.builder.AppListBuilder
import java.util.*

/**
 *
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.button_message -> {
                BottomDialog.builder(this) {
                    title("Hello")
                    message(
                            buildString {
                                for (i in 0..30) {
                                    for (j in 0..i * 5) append(j)
                                    appendln()
                                }
                            }, true
                    )
                    oneButton("OK") {
                        onLongClick { dialog ->
                            dialog.updateContent<MessageContentBuilder> {
                                text = Random().nextDouble().toString()
                            }
                        }
                    }
                }
            }
            R.id.button_sim_list -> {

                val list = ObservableList.build<String?> {
                    for (i in 0..50) add("item $i")
                    add("到底了")
                }

                BottomDialog.builder(this) {
                    this.title("Hello")
                    mutableList(list) { _, position, s, l ->
                        toast("clicked $s at $position longClick: $l")
                    }
                    buttons {
                        negativeButton()
                        neutralButton("removeAt(0)") {
                            if (list.isNotEmpty())
                                list.removeAt(0)
                        }
                        positiveButton("add(0,'...')") {
                            list.add(0, "...")
                        }
                    }
                }
            }
            R.id.button_adapter_list -> {

                BottomDialog.builder(this) {
                    title("应用列表")
                    content(AppListBuilder(this@MainActivity) { _, p, i, l ->
                        toast("$p\n$i\n$l")
                    })
                    oneButton("取消")
                }
            }

            R.id.button_multi_buttons -> {

                BottomDialog.builder(this) {
                    title("Hello")
                    buttons {
                        positiveButton {
                            toast("确认")
                        }
                        negativeButton()
                        neutralButton("更多") {
                            PopupMenu(this@MainActivity, buttonNeutral).apply {
                                menu.add("1")
                                menu.add("2")
                                show()
                            }
                        }
                    }
                    message("啦啦啦", true)
                }

            }
            R.id.button_toggle -> {
                BottomDialogActivity.builder(this) {
                    title("1234567")
                    message("contet")
                    oneButton("Cancel")
                }
            }
            R.id.button_toolbar -> {

                BottomDialog.builder(this) {
                    header(ToolbarHeader()) {

                    }
                }
                BottomDialog.builder(this) {
                    toolbar {
                        title = "Hello"
                        navIconId = R.mipmap.ic_launcher
                        onIconClick = {
                            dialog.dismiss()
                        }
                    }
                }
            }
        }

    }


    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        App.watch(this)
    }
}
