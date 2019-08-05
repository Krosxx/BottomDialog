package cn.vove7.bottomsheetdialog

import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.vove7.bottomdialog.BottomDialog
import cn.vove7.bottomdialog.BottomDialogActivity
import cn.vove7.bottomdialog.builder.*
import cn.vove7.bottomdialog.toolbar
import cn.vove7.bottomdialog.util.ObservableList
import cn.vove7.bottomdialog.extension.awesomeHeader
import cn.vove7.bottomsheetdialog.builder.AppListBuilder
import cn.vove7.bottomsheetdialog.builder.ViewIntentBuilder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 *
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val items = listOf(
                getString(R.string.message_dialog),
                getString(R.string.simple_list_dialog),
                getString(R.string.adapter_list_dialog),
                "Multi Buttons Dialog",
                "Dialog Activity",
                "Toolbar",
                "Awesome Header Dialog",
                "Delay Show ActivityDialog",
                "Awesome Share"
        )
        list_view.adapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                return if (convertView == null) TextView(this@MainActivity).apply {

                    setPadding(20, 30, 20, 30)
                    setText(items[position])

                    setOnClickListener {
                        onClick(position)
                    }
                } else convertView
            }

            override fun getItem(position: Int): Any = items[position]


            override fun getItemId(position: Int): Long = position.toLong()

            override fun getCount(): Int = items.size
        }

    }


    fun onClick(pos: Int) {
        when (pos) {
            0 -> {
                BottomDialog.builder(this) {
                    title("Hello")
                    cancelable(false)
                    withCloseIcon()
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
            1 -> {

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
            2 -> {

                BottomDialog.builder(this) {
                    title("应用列表")
                    content(AppListBuilder(this@MainActivity) { _, p, i, l ->
                        toast("$p\n$i\n$l")
                    })
                    oneButton("取消")
                }
            }

            3 -> {

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
            4 -> {
                BottomDialogActivity.builder(this) {
                    title("1234567")
                    message("contet")
                    oneButton("Cancel")
                }
            }
            5 -> {
                BottomDialog.builder(this) {
                    toolbar {
                        title = "Hello"
                        navIconId = R.drawable.ic_close
                        onIconClick = {
                            dialog.dismiss()
                        }
                    }
                }
            }
            6 -> {
                BottomDialog.builder(this) {
                    awesomeHeader("分享到")

                }
            }
            7 -> {
                finish()
                Handler().postDelayed({
                    onClick(4)
                }, 1000)
            }
            8 -> {
                //分享
                BottomDialog.builder(this) {
                    awesomeHeader("分享到")

                    val intentFilter = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "分享内容")
                    }
                    content(ViewIntentBuilder(intentFilter) { dialog: Dialog, position: Int, item: ResolveInfo, isLongClick: Boolean ->
                        intentFilter.component = ComponentName(item.activityInfo.packageName,
                                item.activityInfo.name)
                        intentFilter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intentFilter)
                    })
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
