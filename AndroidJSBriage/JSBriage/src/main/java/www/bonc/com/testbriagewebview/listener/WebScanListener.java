package www.bonc.com.testbriagewebview.listener;

import android.content.Intent;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cuibg on 2016/11/24.
 * 扫描二维码处理类
 */

public class WebScanListener {
    public WebScanListener() {
    }

    /**
     * 扫描二维码后回调数据
     *
     * @param webView
     * @param data
     */
    public void putQrcodeTojs(Intent data,WebView webView) {
        JSONObject json = new JSONObject();
        String jsUrl = "";
        if (data != null) {
            String result = data.getStringExtra("result");
            try {
                json.put("codeInfo", result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsUrl = "boncAppEngine.codeScanner.successHandler(" + json + ")";
        } else {
            try {
                json.put("description", "扫描二维码失败");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsUrl = "boncAppEngine.codeScanner.errorHandler(" + json + ")";
        }
        webView.loadUrl("javascript:" + jsUrl);
    }

    /**
     * 取消二维码扫描
     *
     * @param webView
     */
    public void cancelScan(WebView webView) {
        JSONObject json = new JSONObject();
        try {
            json.put("description", "取消扫描");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsUrl = "boncAppEngine.codeScanner.cancleHandler(" + json + ")";
        webView.loadUrl("javascript:" + jsUrl);
    }
}
