package com.example.e_library.ui.theme.screens.reader



import android.content.Intent
import android.net.Uri
import android.webkit.URLUtil
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun PdfViewerScreen(pdfUrl: String) {
    val context = LocalContext.current

    // If valid, launch in custom tab (acts like in-app viewer)
    if (URLUtil.isValidUrl(pdfUrl)) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(pdfUrl))
    } else {
        // fallback: use ACTION_VIEW intent
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(pdfUrl), "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
