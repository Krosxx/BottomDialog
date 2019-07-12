# BottomDialog扩展


- 一加对话框效果[AwesomeHeader]

![](/screenshots/s4.gif)

```kotlin
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
```
