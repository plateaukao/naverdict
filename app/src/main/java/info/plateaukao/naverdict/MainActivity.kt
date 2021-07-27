package info.plateaukao.naverdict

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.webkit.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


private var dictionarySource = DictionarySource.Naver

class MainActivity : AppCompatActivity() {
    private lateinit var webView : WebView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isInMultiWindowMode) {
            setTheme(R.style.AppTheme_NoActionBar)
        }
        setContentView(if (isInMultiWindowMode) R.layout.activity_main else R.layout.activity_main_floating)
        setFinishOnTouchOutside(true)

        webView = findViewById(R.id.webView)
        initWebView()
        button = findViewById(R.id.dict_switch)
        button.setOnClickListener {
            dictionarySource = when(dictionarySource) {
                DictionarySource.Naver -> DictionarySource.Goo
                DictionarySource.Goo -> DictionarySource.Naver
            }
            handleIntent(this.intent)
        }

        handleIntent(this.intent)

        overridePendingTransition(0, 0)
    }

    private fun initWebView() {
        with (webView.settings) {
            javaScriptEnabled = true
            databaseEnabled = true
            domStorageEnabled = true
            textZoom = 80
            useWideViewPort = false
            userAgentString = "Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3"
        }

        object: WebViewClient() {
            private var isLoading = false
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                isLoading = true
                super.onPageStarted(view, url, favicon)
            }
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if (isLoading) {
                    this@MainActivity.webView.post{ hideElements() }
                    isLoading = false
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                hideElements()
                isLoading = false
            }

            private fun hideElements() = webView.loadUrl(dictionarySource.javascriptString)

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                webView.loadUrl(request?.url.toString())
                return true
            }
        }.also { webView.webViewClient = it }

        webView.webChromeClient = object: WebChromeClient() {

        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        handleIntent(intent)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun handleIntent(intent: Intent?) {
        when {
            getKeyword(intent) != null -> {
                val keyword = getKeyword(intent) ?: return
                searchInDict(keyword)
            }
            intent?.action == Intent.ACTION_PROCESS_TEXT -> {
                val text = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: return
                searchInDict(text)
            }
            else -> {
                webView.loadUrl(dictionarySource.homeString)
            }
        }
    }

    private fun getKeyword(intent: Intent?): String? = intent?.getStringExtra("EXTRA_QUERY")

    private fun searchInDict(keyword: String) =
        webView.loadUrl(dictionarySource.searchString + keyword)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}