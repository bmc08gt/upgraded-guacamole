package dev.bmcreations.guacamole.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import dev.bmcreations.guacamole.R
import dev.bmcreations.guacamole.extensions.formattedStrings
import dev.bmcreations.guacamole.graph
import kotlinx.android.synthetic.main.activity_web_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class WebLoginActivity: AppCompatActivity(), AnkoLogger {

    private val tokenProvider by lazy { graph().sessionGraph.tokenProvider }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_login)

        title = "Connect with Apple Music"

         webview.settings.apply {
             javaScriptEnabled = true
         }

        webview.loadUrl(formattedStrings[R.string.musickit_web_auth](tokenProvider.developerToken))
        webview.webChromeClient = WebChromeClient()
        webview.webViewClient = object : WebViewClient() {
            @SuppressWarnings("deprecation")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean
                    = shouldOverrideUrlLoading(url)

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean = shouldOverrideUrlLoading(request?.url?.toString())

            private fun shouldOverrideUrlLoading(url: String?) : Boolean {
                info { url }
                webview.loadUrl(url)
                return true
            }
        }
    }

    companion object {
        fun newIntent(caller: Context): Intent = Intent(caller, WebLoginActivity::class.java)
    }
}
