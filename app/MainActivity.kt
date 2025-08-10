// File: app/src/main/java/com/obsidianprint/helper/MainActivity.kt
package com.obsidianprint.helper // Refactored

import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private val viewModel: PrintViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The intent data is processed once when the activity is created.
        if (savedInstanceState == null) {
            viewModel.processIntentData(intent.data)
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.0f) // Translucent background
                ) {
                    PrintScreen(viewModel = viewModel) {
                        // Finish activity after a short delay to allow the print dialog to show.
                        window.decorView.postDelayed({ finish() }, 1500)
                    }
                }
            }
        }
    }
}

@Composable
fun PrintScreen(viewModel: PrintViewModel, onPrintJobStarted: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val state = uiState) {
            is PrintUiState.Idle -> {
                // Nothing to do in idle state
            }
            is PrintUiState.Loading -> {
                CircularProgressIndicator()
            }
            is PrintUiState.Error -> {
                // Show an error and finish
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                (context as? ComponentActivity)?.finish()
            }
            is PrintUiState.Success -> {
                // When content is ready, load it into a WebView and print.
                PrintWebView(
                    htmlContent = state.htmlContent,
                    settings = state.settings,
                    onPrintJobStarted = onPrintJobStarted
                )
            }
        }
    }
}

@Composable
fun PrintWebView(
    htmlContent: String,
    settings: JSONObject,
    onPrintJobStarted: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // LaunchedEffect ensures this runs once when the composable enters the composition
    LaunchedEffect(key1 = htmlContent) {
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                startPrint(context, view, settings)
                onPrintJobStarted()
            }
        }
        webView.loadDataWithBaseURL("about:blank", htmlContent, "text/html", "UTF-8", null)
    }
}

private fun startPrint(context: Context, webView: WebView, settings: JSONObject) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
    val printAdapter: PrintDocumentAdapter = webView.createPrintDocumentAdapter("ObsidianDocument")
    val jobName = context.getString(R.string.app_name) + " Document"
    val builder = PrintAttributes.Builder()

    try {
        val pageSize = settings.optString("pageSize", "A4")
        when {
            "Letter".equals(pageSize, ignoreCase = true) -> builder.setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
            "Legal".equals(pageSize, ignoreCase = true) -> builder.setMediaSize(PrintAttributes.MediaSize.NA_LEGAL)
            else -> builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        }
    } catch (e: Exception) {
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
    }

    printManager?.print(jobName, printAdapter, builder.build())
}
