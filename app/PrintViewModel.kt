// File: app/src/main/java/com/obsidianprint/helper/PrintViewModel.kt
package com.obsidianprint.helper // Refactored

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.json.JSONObject
import java.net.URLDecoder

class PrintViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<PrintUiState>(PrintUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun processIntentData(data: Uri?) {
        if (data == null) {
            _uiState.value = PrintUiState.Error("No data received.")
            return
        }

        try {
            _uiState.value = PrintUiState.Loading
            val markdownContent = URLDecoder.decode(data.getQueryParameter("content"), "UTF-8")
            val settingsJsonString = URLDecoder.decode(data.getQueryParameter("settings"), "UTF-8")
            val settings = JSONObject(settingsJsonString)

            val htmlContent = generateHtml(markdownContent, settings)
            _uiState.value = PrintUiState.Success(htmlContent, settings)

        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.value = PrintUiState.Error("Error processing print data: ${e.message}")
        }
    }

    private fun generateHtml(markdown: String, settings: JSONObject): String {
        val parser = Parser.builder().build()
        val document = parser.parse(markdown)
        val renderer = HtmlRenderer.builder().build()
        val htmlContent = renderer.render(document)
        return buildFinalHtml(htmlContent, settings)
    }

    private fun buildFinalHtml(htmlContent: String, settings: JSONObject): String {
        val htmlBuilder = StringBuilder()
        htmlBuilder.append("<html><head><style>")
        htmlBuilder.append("@page { ")
        try {
            htmlBuilder.append("margin-top: ").append(settings.getString("marginTop")).append("mm; ")
            htmlBuilder.append("margin-bottom: ").append(settings.getString("marginBottom")).append("mm; ")
            htmlBuilder.append("margin-left: ").append(settings.getString("marginLeft")).append("mm; ")
            htmlBuilder.append("margin-right: ").append(settings.getString("marginRight")).append("mm; ")
        } catch (e: Exception) {
            // Use a default margin if settings are not present
            htmlBuilder.append("margin: 20mm; ")
        }
        htmlBuilder.append("} body { font-family: sans-serif; }")
        htmlBuilder.append("</style></head><body>")
        htmlBuilder.append(htmlContent)
        htmlBuilder.append("</body></html>")
        return htmlBuilder.toString()
    }
}

// Sealed interface to represent the different states of the UI
sealed interface PrintUiState {
    object Idle : PrintUiState
    object Loading : PrintUiState
    data class Success(val htmlContent: String, val settings: JSONObject) : PrintUiState
    data class Error(val message: String) : PrintUiState
}
