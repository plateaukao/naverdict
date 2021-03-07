package info.plateaukao.naverdict

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {
    private lateinit var webView : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // relaunch it by using new task
        if (isInMultiWindowMode && intent?.action == Intent.ACTION_PROCESS_TEXT) {
            launchInExistingApp(intent)
            finish()
            return
        }


        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        initWebView()

        handleIntent(this.intent)

        overridePendingTransition(0, 0)
    }

    private fun initWebView() {
        with (webView.settings) {
            javaScriptEnabled = true
            databaseEnabled = true
            domStorageEnabled = true
        }

        webView.webViewClient = object: WebViewClient() {
            val javaScriptString = "javascript:(function() { " +
                    // thick header
                    //"document.getElementById(\"header\").style.display = \"none\";" +
                    "document.getElementById(\"header\").remove();" +
                    // loading cursor
                    //"document.getElementById(\"loadingArea\").style.display = \"none\";" +
                    // wordbook button
                    "document.getElementsByClassName(\"nav_wordbook _nav_wordbook\")[0].style.display = \"none\";" +
                    // all / vocab / examples bar
                    "document.getElementsByClassName(\"Nlnb_menu_list\")[0].style.display = \"none\";" +
                    "})();"
            override fun onPageFinished(view: WebView?, url: String?) {
                hideElements()
            }

            private fun hideElements() {
                webView.loadUrl(javaScriptString)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                webView.loadUrl(request?.url.toString())
                return true
            }
        }

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

    private fun launchInExistingApp(intent: Intent?) {
        val text = intent?.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: return
        val intent = Intent("colordict.intent.action.SEARCH").apply {
            putExtra("EXTRA_QUERY", text)
        }
        startActivity(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when {
            getKeyword(intent) != null -> {
                val keyword = getKeyword(intent) ?: return
                searchInNaver(keyword)
            }
            intent?.action == Intent.ACTION_PROCESS_TEXT -> {
                val text = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: return
                searchInNaver(text)
            }
            else -> {
                webView.loadUrl("https://zh.dict.naver.com/")
            }
        }
    }

    private fun getKeyword(intent: Intent?): String? = intent?.getStringExtra("EXTRA_QUERY")

    private fun searchInNaver(keyword: String) {
        webView.loadUrl("https://zh.dict.naver.com/#/search?query=$keyword")
    }

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