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
import cn.vove7.bottomdialog.extension.awesomeHeader
import cn.vove7.bottomdialog.toolbar
import cn.vove7.bottomdialog.util.ObservableList
import cn.vove7.bottomsheetdialog.builder.AppListBuilder
import cn.vove7.bottomsheetdialog.builder.ViewIntentBuilder
import cn.vove7.bottomsheetdialog.builder.markdownContent
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 *
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BottomDialogBuilder.apply {
            enableAutoDarkTheme = true
            darkTheme = R.style.BottomDialog_Dark
        }

        val items = listOf(
            getString(R.string.message_dialog),
            getString(R.string.simple_list_dialog),
            getString(R.string.adapter_list_dialog),
            "Multi Buttons Dialog",
            "Dialog Activity",
            "Toolbar",
            "Awesome Header Dialog",
            "Delay Show ActivityDialog",
            "Awesome Share",
            "Markdown Dialog",
            "UnExpandable Dialog",
            "DarkDialog"
        )
        list_view.adapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                return ((convertView as TextView?)
                    ?: TextView(this@MainActivity)).apply {

                    setPadding(20, 30, 20, 30)
                    text = items[position]

                    setOnClickListener {
                        onClick(position)
                    }
                }
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
                    title("Hello", true)
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
                    oneButton(
                        "OK",
                        bgColorId = R.color.colorAccent,
                        textColorId = android.R.color.white
                    ) {
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
                    this.title("Hello", true)
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
                    title("应用列表", true)
                    content(AppListBuilder(this@MainActivity) { _, p, i, l ->
                        toast("$p\n$i\n$l")
                    })
                    oneButton("取消")
                }
            }

            3 -> {

                BottomDialog.builder(this) {
                    title("Hello", true)
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
                    title("BottomDialogActivity", true)
                    message("in activity")
                    oneButton("Cancel")
                }
            }
            5 -> {
                BottomDialog.builder(this) {
                    toolbar {
                        title = "Hello"
                        round = true
                        navIconId = R.drawable.ic_close
                        onIconClick = {
                            dialog.dismiss()
                        }
                    }
                }
            }
            6 -> {
                BottomDialog.builder(this) {
                    awesomeHeader("分享到", round = false)
                    message(buildString {
                        for (i in 0..100) append(i)
                    })
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
                        intentFilter.component = ComponentName(
                            item.activityInfo.packageName,
                            item.activityInfo.name
                        )
                        intentFilter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intentFilter)
                    })
                }
            }
            9 -> {
                BottomDialog.builder(this) {
                    awesomeHeader("介绍")

                    markdownContent {
                        loadMarkdownFromAsset("intro.md")
                    }
                    oneButton(
                        "确定",
                        bgColorId = R.color.colorPrimary,
                        textColorId = android.R.color.white
                    )
                }
            }
            10 -> {
                BottomDialog.builder(this) {
                    expandable = false
                    peekHeightProportion = 0.8f
                    title("介绍", true)
                    markdownContent {
                        loadMarkdownFromAsset("intro.md")
                    }
                    oneButton(
                        "确定",
                        bgColorId = R.color.colorPrimary,
                        textColorId = android.R.color.white
                    )
                }
            }
            11 -> {
                BottomDialog.builder(this) {
                    title("一天掉多少根头发", true, true)
                    cancelable(false)
                    withCloseIcon()
                    message(
                        "  　现在，解决一天掉多少根头发的问题，是非常非常重要的。 所以， 韩非在不经意间这样说过，内外相应，言行相称。这不禁令我深思。 爱尔兰在不经意间这样说过，越是无能的人，越喜欢挑剔别人的错儿。这启发了我， 别林斯基曾经说过，好的书籍是最贵重的珍宝。这不禁令我深思。 一天掉多少根头发因何而发生？ 一般来讲，我们都必须务必慎重的考虑考虑。 那么， 可是，即使是这样，一天掉多少根头发的出现仍然代表了一定的意义。 现在，解决一天掉多少根头发的问题，是非常非常重要的。 所以， 所谓一天掉多少根头发，关键是一天掉多少根头发需要如何写。 所谓一天掉多少根头发，关键是一天掉多少根头发需要如何写。 我们都知道，只要有意义，那么就必须慎重考虑。 卡耐基曾经提到过，一个不注意小事情的人，永远不会成就大事业。这句话语虽然很短，但令我浮想联翩。 带着这些问题，我们来审视一下一天掉多少根头发。 就我个人来说，一天掉多少根头发对我的意义，不能不说非常重大。 而这些并不是完全重要，更加重要的问题是， 一天掉多少根头发因何而发生？ 普列姆昌德说过一句富有哲理的话，希望的灯一旦熄灭，生活刹那间变成了一片黑暗。这句话语虽然很短，但令我浮想联翩。 可是，即使是这样，一天掉多少根头发的出现仍然代表了一定的意义。 经过上述讨论， 从这个角度来看， 一天掉多少根头发的发生，到底需要如何做到，不一天掉多少根头发的发生，又会如何产生。 所谓一天掉多少根头发，关键是一天掉多少根头发需要如何写。 一般来说， 普列姆昌德说过一句富有哲理的话，希望的灯一旦熄灭，生活刹那间变成了一片黑暗。这句话语虽然很短，但令我浮想联翩。 一般来说， 我们都知道，只要有意义，那么就必须慎重考虑。 卡耐基曾经说过，我们若已接受最坏的，就再没有什么损失。我希望诸位也能好好地体会这句话。 在这种困难的抉择下，本人思来想去，寝食难安。 总结的来说， 一天掉多少根头发的发生，到底需要如何做到，不一天掉多少根头发的发生，又会如何产生。 文森特·皮尔说过一句富有哲理的话，改变你的想法，你就改变了自己的世界。我希望诸位也能好好地体会这句话。 可是，即使是这样，一天掉多少根头发的出现仍然代表了一定的意义。 可是，即使是这样，一天掉多少根头发的出现仍然代表了一定的意义。 我们都知道，只要有意义，那么就必须慎重考虑。 而这些并不是完全重要，更加重要的问题是， 从这个角度来看， 在这种困难的抉择下，本人思来想去，寝食难安。 一天掉多少根头发因何而发生？ 所谓一天掉多少根头发，关键是一天掉多少根头发需要如何写。 问题的关键究竟为何？ 吉格·金克拉曾经提到过，如果你能做梦，你就能实现它。这启发了我， 每个人都不得不面对这些问题。 在面对这种问题时， 问题的关键究竟为何？ 从这个角度来看， 莎士比亚曾经说过，意志命运往往背道而驰，决心到最后会全部推倒。这启发了我。"
                    )
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
