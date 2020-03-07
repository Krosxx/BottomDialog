package cn.vove7.bottomsheetdialog.builder

import android.graphics.Color
import android.view.View
import br.tiagohm.markdownview.MarkdownView
import br.tiagohm.markdownview.css.styles.Bootstrap
import cn.vove7.bottomdialog.builder.BottomDialogBuilder
import cn.vove7.bottomdialog.interfaces.ContentBuilder
import cn.vove7.bottomdialog.util.isDarkMode
import cn.vove7.bottomsheetdialog.R
import kotlinx.android.synthetic.main.dialog_markdown_view.view.*
import java.io.File


/**
 * # MarkdownContentBuilder
 *
 * @author Vove
 * 2019/6/30
 */

fun BottomDialogBuilder.markdownContent(action: MarkdownContentBuilder.() -> Unit) {
    content(MarkdownContentBuilder(context.isDarkMode), action)
}

class MarkdownContentBuilder(
    private val isDarkMode: Boolean = false
) : ContentBuilder() {

    override val layoutRes: Int
        get() = R.layout.dialog_markdown_view

    lateinit var mdView: MarkdownView

    override fun init(view: View) {
        mdView = view.markdown_view
        if (isDarkMode) {
            //防止白色背景闪屏
            mdView.setBackgroundColor(Color.parseColor("#212121"))
        }
        mdView.addStyleSheet(
            if (isDarkMode) MyDarkStyle() else MyStyle()
        )
    }

    var source: Any? = null
    var sourceType = 0

    fun loadMarkdownFromAsset(path: String) {
        sourceType = 1
        source = path
    }

    fun loadMarkdownFromFile(file: File) {
        sourceType = 2
        source = file
    }

    fun loadMarkdownFromUrl(url: String) {
        sourceType = 3
        source = url
    }

    fun loadMarkdown(text: String) {
        source = text
        sourceType = 4
    }

    override fun updateContent(type: Int, data: Any?) {
        when (sourceType) {
            1 -> mdView.loadMarkdownFromAsset(source as String)
            2 -> mdView.loadMarkdownFromFile(source as File)
            3 -> mdView.loadMarkdownFromUrl(source as String)
            4 -> mdView.loadMarkdown(source as String)
        }
    }
}

class MyDarkStyle : MyStyle() {
    init {
        this.addRule("body", "background: #212121","color: #eee")
        this.addRule("img", "opacity: 0.7")
        this.addRule("pre", "background: #f6f8fab0")
        this.addRule("code", "background: #444242")
        addRule(
            "blockquote",
            "border-left: 5px solid #444242"
        )
    }
}

/**
 * 自定义主题
 * 设置边距
 */
open class MyStyle : Bootstrap() {
    init {
        this.addRule("body", "line-height: 1.6", "padding: 5px")

        this.addRule("h1", "font-size: 28px")
        this.addRule("h2", "font-size: 24px")
        this.addRule("h3", "font-size: 18px")
        this.addRule("h4", "font-size: 16px")
        this.addRule("h5", "font-size: 14px")
        this.addRule("h6", "font-size: 14px")
        this.addRule(
            "pre",
            "position: relative",
            "padding: 5px 5px",
            "border: 0",
            "border-radius: 3px",
            "background-color: #f6f8fa"
        )
        this.addRule(
            "pre code",
            "position: relative",
            "line-height: 1.45",
            "background-color: transparent"
        )
        this.addRule("table tr:nth-child(2n)", "background-color: #f6f8fa")
        this.addRule("table th", "padding: 6px 13px", "border: 1px solid #dfe2e5")
        this.addRule("table td", "padding: 6px 13px", "border: 1px solid #dfe2e5")
        this.addRule(
            "kbd",
            "color: #444d56",
            "font-family: Consolas, \"Liberation Mono\", Menlo, Courier, monospace",
            "background-color: #fcfcfc",
            "border: solid 1px #c6cbd1",
            "border-bottom-color: #959da5",
            "border-radius: 3px",
            "box-shadow: inset 0 -1px 0 #959da5"
        )
        this.addRule(
            "pre[language]::before",
            "content: attr(language)",
            "position: absolute",
            "top: 0",
            "right: 5px",
            "padding: 2px 1px",
            "text-transform: uppercase",
            "color: #666",
            "font-size: 8.5px"
        )
        this.addRule("pre:not([language])", "padding: 6px 10px")
        this.addRule(".footnotes li p:last-of-type", "display: inline")
        this.addRule(".yt-player", "box-shadow: 0px 0px 12px rgba(0,0,0,0.2)")
        this.addRule(".scrollup", "background-color: #00BF4C")
        this.addRule(".hljs-comment", "color: #8e908c")
        this.addRule(".hljs-quote", "color: #8e908c")
        this.addRule(".hljs-variable", "color: #c82829")
        this.addRule(".hljs-template-variable", "color: #c82829")
        this.addRule(".hljs-tag", "color: #c82829")
        this.addRule(".hljs-name", "color: #c82829")
        this.addRule(".hljs-selector-id", "color: #c82829")
        this.addRule(".hljs-selector-class", "color: #c82829")
        this.addRule(".hljs-regexp", "color: #c82829")
        this.addRule(".hljs-deletion", "color: #c82829")
        this.addRule(".hljs-number", "color: #f5871f")
        this.addRule(".hljs-built_in", "color: #f5871f")
        this.addRule(".hljs-builtin-name", "color: #f5871f")
        this.addRule(".hljs-literal", "color: #f5871f")
        this.addRule(".hljs-type", "color: #f5871f")
        this.addRule(".hljs-params", "color: #f5871f")
        this.addRule(".hljs-meta", "color: #f5871f")
        this.addRule(".hljs-link", "color: #f5871f")
        this.addRule(".hljs-attribute", "color: #eab700")
        this.addRule(".hljs-string", "color: #718c00")
        this.addRule(".hljs-symbol", "color: #718c00")
        this.addRule(".hljs-bullet", "color: #718c00")
        this.addRule(".hljs-addition", "color: #718c00")
        this.addRule(".hljs-title", "color: #4271ae")
        this.addRule(".hljs-section", "color: #4271ae")
        this.addRule(".hljs-keyword", "color: #8959a8")
        this.addRule(".hljs-selector-tag", "color: #8959a8")
    }
}