package com.obsidianprinth.helper;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PrintAdapter extends PrintDocumentAdapter {

    private Context context;
    private String htmlContent;
    private WebView webView;

    public PrintAdapter(Context context, String htmlContent) {
        this.context = context;
        this.htmlContent = htmlContent;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        webView = new WebView(context);
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }
                PrintDocumentInfo info = new PrintDocumentInfo.Builder("obsidian_document.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                        .build();
                callback.onLayoutFinished(info, !newAttributes.equals(oldAttributes));
            }
        });
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        if (cancellationSignal.isCanceled()) {
            callback.onWriteCancelled();
            return;
        }
        if (webView != null) {
            webView.print(getPrintManager().getPrintJobs().get(0), this, null);
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        } else {
            callback.onWriteFailed("WebView not initialized");
        }
    }

    private android.print.PrintManager getPrintManager() {
        return (android.print.PrintManager) context.getSystemService(Context.PRINT_SERVICE);
    }
}
