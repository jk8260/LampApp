package com.elasalle.lamp.util;

import android.annotation.SuppressLint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.elasalle.lamp.LampApp;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class PdfHelper {

    /**
     *
     * Note: WebView's {@link android.webkit.WebView#capturePicture() capturePicture()} is deprecated but
     * there's an issue with Lollipop that prevents capturing the entire webview content so it is used here.
     * @see <a href="https://bugs.chromium.org/p/chromium/issues/detail?id=490246"> Webview.onDraw to pdf backed canvas does not work</a>
     *
     */
    @SuppressWarnings("deprecation")
    public static File webviewToPdf(@NonNull final WebView webView) throws IOException {
        final Picture picture = webView.capturePicture();
        final PdfDocument document = createPdfDocument(picture);
        return savePdfFile(document);
    }

    @NonNull
    @SuppressLint("SetWorldReadable")
    private static File savePdfFile(PdfDocument document) throws IOException {
        FileUtils.deleteQuietly(LampApp.getInstance().getExternalCacheDir());
        File outputDir = LampApp.getInstance().getExternalCacheDir();
        final File file = File.createTempFile("asset",".pdf", outputDir);
        //noinspection ResultOfMethodCallIgnored
        file.setReadable(true, false);
        document.writeTo(FileUtils.openOutputStream(file));
        return file;
    }

    @NonNull
    private static PdfDocument createPdfDocument(Picture picture) {
        final int width = picture.getWidth();
        final int height = picture.getHeight();
        final PdfDocument document = new PdfDocument();
        final PdfDocument.PageInfo pageInfo = new PdfDocument
                .PageInfo.Builder(width, height, 1)
                .setContentRect(new Rect(0, 0, width, height))
                .create();
        final PdfDocument.Page page = document.startPage(pageInfo);
        picture.draw(page.getCanvas());
        document.finishPage(page);
        return document;
    }
}