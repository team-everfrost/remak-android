package com.example.remak.view.dialog

import android.app.Dialog
import com.example.remak.App
import com.example.remak.databinding.AddLinkCustomDialogBinding

class AddLinkDialog {
    private lateinit var binding : AddLinkCustomDialogBinding
    val context = App.context()

    private val dlg = Dialog(context)

    fun show(content : String) {
        dlg.setContentView(binding.root)
        dlg.show()
    }

}