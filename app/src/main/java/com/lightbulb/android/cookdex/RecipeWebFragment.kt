package com.lightbulb.android.cookdex

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

private const val ARGUMENT_URI = "recipe_uri"

class RecipeWebFragment : Fragment() {

    private lateinit var uri: Uri
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //looks for the Url contained in the arguments and assigns it to be used with the webview
        uri = arguments?.getParcelable(ARGUMENT_URI) ?: Uri.EMPTY
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recipe_web_page, container, false)

        //makes some settings for the webview then loads the web page
        webView = view.findViewById(R.id.web_view)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl(uri.toString())

        return view
    }

    //used by the hosting activity to retrieve a new object of this class containing the website Url in its arguments
    companion object {
        fun newInstance(uri: Uri): RecipeWebFragment {
            return RecipeWebFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARGUMENT_URI, uri)
                }
            }
        }
    }
}