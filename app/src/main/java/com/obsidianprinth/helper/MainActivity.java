package com.obsidianprinth.helper;

import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            }
        }
        finish();
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            String html = convertMarkdownToHtml(sharedText);
            printHtmlContent(html);
        }
    }

    private String convertMarkdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    private void printHtmlContent(String html) {
        WebView webView = new WebView(this);
        webView.loadDataWithBaseURL(null, html, "text/HTML", "UTF-8", null);
        
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        String jobName = getString(R.string.app_name) + " Document";
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
        
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        try {
            JSONObject obsidianJson = new JSONObject(getIntent().getStringExtra("obsidian_json"));
            if (obsidianJson.has("print_size")) {
                String size = obsidianJson.getString("print_size");
                if ("A4".equalsIgnoreCase(size)) {
                    builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
                } else if ("Letter".equalsIgnoreCase(size)) {
                    builder.setMediaSize(PrintAttributes.MediaSize.NA_LETTER);
                }
            } else {
                 builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
            }
        } catch (JSONException e) {
            builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
        }
        printManager.print(jobName, printAdapter, builder.build());
    }
}
