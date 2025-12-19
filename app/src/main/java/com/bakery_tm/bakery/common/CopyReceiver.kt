package com.bakery_tm.bakery.common

import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast

class CopyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val textToCopy = intent.getStringExtra("COPY_TEXT") ?: return

        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip = ClipData.newPlainText("copied_text", textToCopy)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, "Скопировано в буфер", Toast.LENGTH_SHORT).show()
    }
}