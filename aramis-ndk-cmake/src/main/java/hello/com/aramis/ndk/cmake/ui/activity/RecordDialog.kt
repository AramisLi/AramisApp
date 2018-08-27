package hello.com.aramis.ndk.cmake.ui.activity

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.aramis.library.component.dialog.BunnyDialog
import hello.com.aramis.ndk.cmake.R
import hello.com.aramis.ndk.cmake.widget.RecordLoadingView

/**
 *Created by Aramis
 *Date:2018/8/23
 *Description:
 */
class RecordDialog(context: Context) : BunnyDialog {
    private var dialog: Dialog? = null
    private var recordView: RecordLoadingView? = null
    var onAnimFinished: (() -> Unit)? = null
        set(value) {
            recordView?.onAnimFinished = value
        }

    init {
        dialog = Dialog(context, R.style.new_custom_dialog)
        dialog?.setContentView(R.layout.dialog_record)
        dialog?.getWindow()!!.setBackgroundDrawable(ColorDrawable())
        val s = dialog!!.getWindow()!!.decorView
        recordView = s.findViewById(R.id.recordView)
        dialog?.setCanceledOnTouchOutside(false)

    }

    override fun show() {
        dialog?.show()
        recordView?.startAnim()
    }

    override fun dismiss() {
        dialog?.dismiss()
    }

    override fun isShowing(): Boolean = dialog?.isShowing ?: false
}