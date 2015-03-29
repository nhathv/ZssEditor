package com.example.nhathv.zsseditor;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    WebView webView;
    Button buttonTest;
    Button buttonTest2;

    String WEBBASE_EDITOR_PATH = "file:///android_asset/www/zsseditor/editor.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonTest = (Button) findViewById(R.id.buttonTest);
        buttonTest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String html = "<div class=\"abcClass\">" +
                        "<h1>AAABC</h1>" +
                        "<h2>AAABC</h2>" +
                        "<a href=\"http://google.com\" />" +
                        "<img src=\"http://a5.img.bongda.com.vn/wp-content/uploads/2015/03/3b124.jpg\" width=\"320\" height=\"180\" />"+
                        "</div>";

                Log.d("WEBVIEW", "trigger BEFORE: " + html);
                String htmlClean = removeQuotesFromHTML(html);
                Log.d("WEBVIEW", "trigger AFTER: " + htmlClean);

                insertHTML(htmlClean);
            }
        });

        buttonTest2 = (Button) findViewById(R.id.buttonTest2);
        buttonTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHTML();
            }
        });

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
        webView.loadUrl(WEBBASE_EDITOR_PATH);

        // to handle callback
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.d("WEBVIEW", "url: " + url);
                if (url.indexOf("callback://0/") != -1){
                    String className = url.replace("callback://0/", "");
                    Log.d("WEBVIEW", "className: " + className);

                } else if (url.indexOf("debug://") != -1){
                    String debug = url.replace("debug://", "");
                    Log.d("WEBVIEW", "debug: " + debug);
                } else if (url.indexOf("scroll://") != -1){
                    int position = Integer.parseInt(url.replace("scroll://", ""));
                    Log.d("WEBVIEW", "position: " + position);
                }
                return true;
            }
        });

        // get return from javascript
        MyJavaScriptInterface javaInterface = new MyJavaScriptInterface();
        webView.addJavascriptInterface(javaInterface, "HTMLOUT");
    }

    /**
     * Get HTML of editor
     */
    private void getHTML(){
        String trigger = "javascript:(function(){ " +
                "var resultSrc = zss_editor.getHTML(); " +
                "window.HTMLOUT.someCallback(resultSrc); " +
                "})()";
        webView.loadUrl(trigger);
    }

    /**
     * set HTML to web-base editor
     * @param htmlCleaned should remove Quotes
     */
    private void setHTML(String htmlCleaned){
        String trigger = String.format("javascript:zss_editor.setHTML(\"%s\")", htmlCleaned);
        webView.loadUrl(trigger);
    }

    /**
     * insert HTML to web-base editor
     * @param htmlCleaned should remove Quotes
     */
    private void insertHTML(String htmlCleaned){
        String trigger = String.format("javascript:zss_editor.zss_editor.insertHTML(\"%s\")", htmlCleaned);
        webView.loadUrl(trigger);
    }

    /**
     * Save the selection location
     */
    private void prepareInsert(){
        webView.loadUrl("javascript:zss_editor.prepareInsert()");
    }

    /**
     * focus to editor, show keyboard
     */
    private void focusTextEditor(){
        webView.loadUrl("javascript:zss_editor.focusEditor()");
    }

    /**
     * out of focus editor, hide keyboard
     */
    private void blurTextEditor(){
        webView.loadUrl("javascript:zss_editor.blurEditor()");
    }

    /**
     * Remove quote from html string
     * @param html
     * @return cleaned html string
     */
    private String removeQuotesFromHTML(String html) {
        // should replace \" to \\\", but, i dont know why this doesnt work.
        html = html.replaceAll("\"", "'");
        // replace “ by &quot;
        html = html.replaceAll("“", "&quot;");
        // replace ” by &quot;
        html = html.replaceAll("”", "&quot;");
        // replace \r by \\r
        html = html.replaceAll("\r", "\\r");
        // replace \n by \\n
        html = html.replaceAll("\n", "\\n");

        return html;
    }

    /**
     * To handle returl of java script function
     */
    class MyJavaScriptInterface {

        @JavascriptInterface
        public void someCallback(String jsResult) {
            Log.d("[WEBVIEW]", "jsResult: " + jsResult);

            // should notify after receive string
        }
    }
}
