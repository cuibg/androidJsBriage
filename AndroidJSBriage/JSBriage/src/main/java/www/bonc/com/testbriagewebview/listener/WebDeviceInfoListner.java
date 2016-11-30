package www.bonc.com.testbriagewebview.listener;

import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by cuibg on 2016/11/22.
 * 获取设备信息并传入到webview，或者调起震动
 */

public class WebDeviceInfoListner {
    public WebDeviceInfoListner() {
    }

    /**
     * 获取手机信息
     * @param webView
     * @param context
     */
    public void getDeViceInfo(WebView webView, Context context) {
        String manufacturer = Build.MANUFACTURER;
        String name = Build.BRAND;
        String model = android.os.Build.MODEL;
        String version = Build.VERSION.RELEASE;

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice = "" + tm.getDeviceId();
        String tmSerial = "" + tm.getSimSerialNumber();
        String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uuid = deviceUuid.toString();//uuid
        JSONObject jsonObject = new JSONObject();
        String jsUrl = "";
        if (TextUtils.isEmpty(manufacturer) && TextUtils.isEmpty(model) && TextUtils.isEmpty(model) && TextUtils.isEmpty(uuid) && TextUtils.isEmpty(version)) {
            try {
                jsonObject.put("description", "对不起，没有获取手机信息，请重新尝试");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsUrl = "boncAppEngine.device.errorHandler(" + jsonObject.toString() + ")";
        } else {
            try {
                jsonObject.put("manufacturer", manufacturer);
                jsonObject.put("platform", "android");
                jsonObject.put("model", model);
                jsonObject.put("uuid", uuid);
                jsonObject.put("name", name);
                jsonObject.put("version", version);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsUrl = "boncAppEngine.device.successHandler(" + jsonObject.toString() + ")";
        }
        webView.loadUrl("javascript:" + jsUrl);
    }

    /**
     * 震动
     * @param params
     * @param context
     */
    public void setVibrate(String params, Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        int frequency = 0;
        try {
            JSONArray paramsArray = new JSONArray(params);
            frequency = (int) paramsArray.get(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (frequency != 0) {
            long[] longs = new long[2 * frequency];
            for (int i = 0; i < 2 * frequency; i++) {
                if (i % 2 == 0) {
                    longs[i] = 500;
                } else {
                    longs[i] = 500;
                }
            }
            vibrator.vibrate(longs, -1);
        } else {
            vibrator.vibrate(1000);
        }
    }
}
