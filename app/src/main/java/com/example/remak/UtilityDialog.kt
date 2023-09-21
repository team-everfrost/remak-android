package com.example.remak

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton

object UtilityDialog {
    fun showWarnDialog(
        context: Context,
        getContent: String,
        getSubContent: String,
        confirmBtnText: String,
        cancelBtnText: String,
        confirmClick: () -> Unit,
        cancelClick: () -> Unit
    ) {

        val dialog = Dialog(context)
        val window = dialog.window
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val layoutParams = window.attributes
        layoutParams?.dimAmount = 0.7f // 어둡게 할 배경의 투명도를 설정
        window.attributes = layoutParams
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_warning)

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (size.x * 0.85).toInt()
            window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)
        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (rect.width() * 0.85).toInt()
            window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        val confirmBtn = dialog.findViewById<AppCompatButton>(R.id.confirmBtn)
        val cancelBtn = dialog.findViewById<AppCompatButton>(R.id.cancelBtn)
        val content = dialog.findViewById<TextView>(R.id.msgTextView)
        val subContent = dialog.findViewById<TextView>(R.id.subMsgTextView)
        content.text = getContent
        subContent.text = getSubContent
        confirmBtn.text = confirmBtnText
        cancelBtn.text = cancelBtnText

        confirmBtn.setOnClickListener {
            dialog.dismiss()
            confirmClick()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
            cancelClick()
        }
        dialog.show()
    }

    fun showInformDialog(msg: String, subMsg: String, context: Context, confirmClick: () -> Unit) {
        val dialog = Dialog(context)
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_information)

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (size.x * 0.85).toInt()
            window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)

        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (rect.width() * 0.85).toInt()
            window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)
        }

        val confirmBtn = dialog.findViewById<View>(R.id.confirmBtn)
        confirmBtn.setOnClickListener {
            confirmClick()
            dialog.dismiss()
        }
        val msgText = dialog.findViewById<TextView>(R.id.msgTextView)
        val subMsgText = dialog.findViewById<TextView>(R.id.subMsgTextView)
        msgText.text = msg
        subMsgText.text = subMsg
        dialog.show()
    }

    fun showImageDialog(
        context: Context,
        downloadBtnClick: () -> Unit,
        shareBtnClick: () -> Unit,
    ) {
        val dialog = Dialog(context)
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog_image_download)

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (size.x * 0.85).toInt()
            window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)

        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (rect.width() * 0.85).toInt()
            window.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        val downloadBtn = dialog.findViewById<View>(R.id.downloadBtn)
        val shareBtn = dialog.findViewById<View>(R.id.shareBtn)
        downloadBtn.setOnClickListener {
            downloadBtnClick()
            dialog.dismiss()
        }
        shareBtn.setOnClickListener {
            shareBtnClick()
            dialog.dismiss()
        }
        dialog.show()
    }

}
