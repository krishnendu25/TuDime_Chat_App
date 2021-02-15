package com.TuDime.ui.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.TuDime.R
import com.android.volley.VolleyError
import org.json.JSONArray
import org.json.JSONObject

class PriceRatesWebView : BaseActivity() {
    private var web: WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms__and__conditions)
        web = findViewById<View>(R.id.webview) as WebView
        val webSettings = web!!.settings
        webSettings.javaScriptEnabled = true
        try {
            showProgressDialog(R.string.load)
            web!!.loadUrl("https://tudime.net/price-page-for-app/")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setupWebView();
    }

    override fun ErrorResponse(error: VolleyError, requestCode: Int, networkresponse: JSONObject) {}
    override fun SuccessResponse(response: JSONObject, requestCode: Int) {}
    override fun SuccessResponseArray(response: JSONArray, requestCode: Int) {}
    override fun SuccessResponseRaw(response: String, requestCode: Int) {}




    private fun setupWebView() {

        val webViewClient: WebViewClient = object: WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                showProgressDialog(R.string.load)
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                hideProgressDialog()
                super.onPageFinished(view, url)
            }
        }
        web!!.webViewClient = webViewClient

        web!!.settings.defaultTextEncodingName = "utf-8"
    }

}