package www.bonc.com.testbriagewebview.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static android.content.Context.SENSOR_SERVICE;


/**
 * 加速度计的开启与停止
 * Created by cuibg on 2016/11/22.
 */

public class WebAccelerometerListener implements SensorEventListener {
    private WebView webView;
    private SensorManager sensorManager;
    private Sensor sensorDefault;
    private Handler handler = new Handler();
    /**
     * 开启加速度计
     */
    public void startAccelerometer(WebView webView,Context context) {
        this.webView = webView;
        if (sensorManager == null && sensorDefault == null) {
            sensorManager = ((SensorManager) context.getSystemService(SENSOR_SERVICE));
            sensorDefault = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensorDefault, SensorManager.SENSOR_DELAY_NORMAL, handler);
        }
    }

    /**
     * 停止加速度计
     */
    public void stopAccelerometer() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            sensorManager = null;
            sensorDefault = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String jsUrl = null;
        if (event != null) {
            float xValue = event.values[0];
            float yValue = event.values[1];
            float zValue = event.values[2];
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("x", xValue);
                jsonObject.put("y", yValue);
                jsonObject.put("z", zValue);
                Date date = new Date();
                jsonObject.put("timestamp", String.valueOf(date));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsUrl = "boncAppEngine.accelerometer.successHandler(" + jsonObject.toString() + ")";
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("error", "未获取加速度");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsUrl = "boncAppEngine.accelerometer.errorHandler(" + jsonObject.toString() + ")";
        }
        webView.loadUrl("javascript:" + jsUrl);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
