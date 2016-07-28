package activity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

//import com.devbrackets.android.exomedia.listener.OnPreparedListener;
//import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
//import com.github.barteksc.pdfviewer.PDFView;
//import com.github.barteksc.pdfviewer.listener.OnDrawListener;
//import com.github.barteksc.pdfviewer.listener.OnErrorListener;
//import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
//import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.nganthoi.salai.tabgen.BuildConfig;
import com.nganthoi.salai.tabgen.R;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import Utils.ConstantValues;

/**
 * Created by atul on 28/7/16.
 */
public class CmeChapterDetail extends AppCompatActivity {

//    PDFView pdfview;
    String file_type,attachment_url,TOKEN;
    RelativeLayout rlViewer;
    ProgressBar progressBar;
    WebView webView;
    ImageView imgDetail;
    CardView cvCard;
//    EMVideoView emVideoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_detail);
        file_type=getIntent().getStringExtra("FILE_TYPE");
        attachment_url=getIntent().getStringExtra("ATTATCHMENT_URL");
        TOKEN=getIntent().getStringExtra("TOKEN");
        initComponent();
    }

    private void initComponent() {
//        pdfview=(PDFView)findViewById(R.id.pdfView);
        rlViewer=(RelativeLayout) findViewById(R.id.rlViewer);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        webView=(WebView) findViewById(R.id.webView);
        imgDetail=(ImageView) findViewById(R.id.imgDetail);
//        cvCard=(CardView) findViewById(R.id.cvCard);
        progressBar.setMax(100);
//        emVideoView=(EMVideoView) findViewById(R.id.emVideoView);
        webView.setWebViewClient(new WebViewClientDemo());
        webView.setWebChromeClient(new WebChromeClientDemo());
        webView.getSettings().setJavaScriptEnabled(true);

        if(file_type.contains("pdf")){
//            pdfview.setVisibility(View.VISIBLE);
            imgDetail.setVisibility(View.GONE);
//            cvCard.setVisibility(View.GONE);
            rlViewer.setVisibility(View.GONE);
//            pdfview.fromUri(Uri.parse(attachment_url))
//                    .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
//                    .enableSwipe(true)
//                    .enableDoubletap(true)
//                    .swipeVertical(false)
//                    .defaultPage(1)
//                    .showMinimap(false)
//                    .onDraw(new OnDrawListener() {
//                        @Override
//                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
//
//                        }
//                    })
//                    .onLoad(new OnLoadCompleteListener() {
//                        @Override
//                        public void loadComplete(int nbPages) {
//
//                        }
//                    })
//                    .onPageChange(new OnPageChangeListener() {
//                        @Override
//                        public void onPageChanged(int page, int pageCount) {
//
//                        }
//                    })
//                    .onError(new OnErrorListener() {
//                        @Override
//                        public void onError(Throwable t) {
//
//                        }
//                    })
//                    .enableAnnotationRendering(false)
//                    .password(null)
//                    .showPageWithAnimation(true)
//                    .load();
        }else if(file_type.contains("image")){
            imgDetail.setVisibility(View.VISIBLE);
//            pdfview.setVisibility(View.GONE);
//            cvCard.setVisibility(View.GONE);
            rlViewer.setVisibility(View.GONE);
            OkHttpClient picassoClient1 = new OkHttpClient();
            picassoClient1.networkInterceptors().add(new Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json")
                            .addHeader("Authorization", "Bearer " + TOKEN)
                            .build();
                    return chain.proceed(newRequest);
                }
            });
            new Picasso.Builder(getApplicationContext()).loggingEnabled(BuildConfig.DEBUG)
                                .downloader(new OkHttpDownloader(picassoClient1)).build()
                                .load("" + attachment_url)
                                .fit()
                                .error(R.drawable.username)
                                .into(imgDetail);
        }else if(file_type.contains("video")){
//            cvCard.setVisibility(View.VISIBLE);
            imgDetail.setVisibility(View.GONE);
//            pdfview.setVisibility(View.GONE);
//            rlViewer.setVisibility(View.GONE);
//            emVideoView.setVideoURI(Uri.parse(""+attachment_url));
        }else if (file_type.contains("html")){
            rlViewer.setVisibility(View.VISIBLE);
            imgDetail.setVisibility(View.GONE);
//            pdfview.setVisibility(View.GONE);
//            cvCard.setVisibility(View.GONE);
            webView.loadUrl(""+attachment_url);
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    private class WebViewClientDemo extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            progressBar.setProgress(100);
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }
    }
    private class WebChromeClientDemo extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            progressBar.setProgress(progress);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public void onPrepared() {
//        emVideoView.start();
//    }
}
