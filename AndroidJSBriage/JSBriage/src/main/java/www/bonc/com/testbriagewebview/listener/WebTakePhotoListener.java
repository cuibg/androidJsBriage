package www.bonc.com.testbriagewebview.listener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.WebView;
import android.graphics.Bitmap.CompressFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by cuibg on 2016/11/22.
 * 拍照插件
 */

public class WebTakePhotoListener {
    private String params;
    /**
     * 设置参数
     *
     * @param params
     */
    public void putParams(String params) {
        this.params = params;
    }

    /**
     * 将拍照得到的图片通过base64传到js
     *
     * @param data
     * @param webView
     */
    public void passPictureToJs(Intent data, WebView webView) {
        if (data != null) {
            Bitmap bitmap = data.getParcelableExtra("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int mediaType = 0;
            int quality = 100;
            if (TextUtils.isEmpty(params)) {
                try {
                    JSONArray jsonArray = new JSONArray(params);
                    mediaType = (int) jsonArray.get(0);
                    quality = ((int) jsonArray.get(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Bitmap.CompressFormat compressFormat = mediaType == 0 ? Bitmap.CompressFormat.JPEG : CompressFormat.PNG;
            String type = "JPEG";
            if (compressFormat.equals(CompressFormat.PNG)) {
                type = "PNG";
            }
            bitmap.compress(compressFormat, 60, baos);
            byte[] bytes = baos.toByteArray();
            String dataBase64String = Base64.encodeToString(bytes, Base64.DEFAULT);
            JSONObject json = new JSONObject();
            try {
                JSONObject dataURL = json.put("dataURL", "data:" + "image/" + type + ";base64," + dataBase64String);
                json.put("type", type);
                json.put("dataBase64String", dataBase64String);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = "boncAppEngine.camera.successHandler(" + json + ")";
            webView.loadUrl("javascript:" + url);
        } else {
            JSONObject json = new JSONObject();
            try {
                json.put("description", "获取图片失败，请重试");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = "boncAppEngine.error.successHandler(" + json + ")";
            webView.loadUrl("javascript:" + url);
        }
    }

    /**
     * 取消传递picture到js
     *
     * @param webView
     */
    public void cancelPassPicture(WebView webView) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("description", "取消拍照");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "boncAppEngine.cancleHandler.successHandler(" + jsonObject + ")";
        webView.loadUrl("javascript:" + url);
    }
}
