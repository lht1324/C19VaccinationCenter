package com.overeasy.c19vaccinationcenter.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.jakewharton.rxbinding4.view.clicks
import com.overeasy.c19vaccinationcenter.R
import com.overeasy.c19vaccinationcenter.databinding.DialogHelpBinding

class HelpDialog(private val mContext: Context) : Dialog(mContext) {
    private lateinit var binding: DialogHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCancelable(true)

        init()
    }

    private fun init() {
        val layoutParams = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dimAmount = 0.5f
        }
        window!!.attributes = layoutParams

        binding.apply {
            textView2.clicks().subscribe {
                showAlertDialog(R.string.marker, R.string.markerHelp)
            }

            textView3.clicks().subscribe {
                showAlertDialog(R.string.initMap, R.string.initMapHelp)
            }
        }
    }

    private fun showAlertDialog(titleId: Int, messageId: Int) = AlertDialog.Builder(mContext, R.style.DialogTheme)
        .setTitle(titleId)
        .setMessage(messageId)
        .setNegativeButton(R.string.back) { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}