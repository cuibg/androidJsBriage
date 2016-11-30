package www.bonc.com.testbriagewebview;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.json.JSONException;

import rock.qrcodelibrary.CaptureActivity;
import www.bonc.com.testbriagewebview.listener.WebAccelerometerListener;
import www.bonc.com.testbriagewebview.listener.WebContactsListener;
import www.bonc.com.testbriagewebview.listener.WebDeviceInfoListner;
import www.bonc.com.testbriagewebview.listener.WebScanListener;
import www.bonc.com.testbriagewebview.listener.WebTakePhotoListener;

public class MainActivity extends AppCompatActivity {


    private WebView webView;
    private String path;
    /* 请求码 */
    private  final int CAMERA_REQUEST_CODE = 0x0001 << 2;
    private  final int QRCODE_REQUEST_CODE = 0x0002 << 2;
    private  final int CONTACT_REQEST_CODE = 0x0003 <<2;
    private WebAccelerometerListener webAccelerometerListener;
    private WebTakePhotoListener webTakePhotoListener;
    private WebDeviceInfoListner webDeviceInfoListner;
    private WebContactsListener webContactsListener;
    private WebScanListener webScanListener;
    private TextView deleteContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = ((WebView) findViewById(R.id.webView));
        deleteContact = (TextView) findViewById(R.id.text1);

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent,CONTACT_REQEST_CODE);
            }
        });
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {


            private WebContactsListener webContactsListener;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mobile-service")) {
                    int index = url.indexOf("?");
                    String subUrl = url.substring(index + 1, url.length());
                    String[] splitInfo = subUrl.split("[&]");
                    String object = splitInfo[0].split("[=]")[1];//js传过来的行为对象
                    String command = splitInfo[1].split("[=]")[1];//动作
                    String params = "";
                    if (splitInfo.length == 3) {
                        params = splitInfo[2].split("[=]")[1];//参数
                    }
                    switch (object) {
                        case "device"://设备信息
                            if (webDeviceInfoListner == null) {
                                webDeviceInfoListner = new WebDeviceInfoListner();
                            }
                            if ("getDeviceInfo".equals(command)) {
                                webDeviceInfoListner.getDeViceInfo(webView, MainActivity.this);
                            } else if ("vibrate".equals(command)) {
                                webDeviceInfoListner.setVibrate(params, MainActivity.this);
                            }
                            break;
                        case "accelerometer"://加速度计
                            if (webAccelerometerListener == null) {
                                webAccelerometerListener = new WebAccelerometerListener();
                            }
                            if ("start".equals(command)) {
                                webAccelerometerListener.startAccelerometer(webView, MainActivity.this);
                            } else if ("stop".equals(command)) {
                                webAccelerometerListener.stopAccelerometer();
                            }
                            break;
                        case "camera"://照相机
                            if (webTakePhotoListener == null) {
                                webTakePhotoListener = new WebTakePhotoListener();
                            }
                            if ("takePhoto".equals(command)) {
                                webTakePhotoListener.putParams(params);
                                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                            }
                            break;
                        case "codeScanner"://二维码
                            if (webScanListener == null) {
                                webScanListener = new WebScanListener();
                            }
                            if ("scan".equals(command)) {
                                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                                startActivityForResult(intent, QRCODE_REQUEST_CODE);
                            }
                            break;
                        case "contacts"://联系人
                            if (webContactsListener == null) {
                                webContactsListener = new WebContactsListener();
                            }
                            if ("newContact".equals(command)) {
                                Intent intent = new Intent();
                                webContactsListener.addContact();
                            }else if ("contactInfo".equals(command)){
                                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                startActivityForResult(intent,CONTACT_REQEST_CODE);
                            }
                            break;
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE://拍照回调
                    if (webTakePhotoListener != null) {
                        webTakePhotoListener.passPictureToJs(data, webView);
                    }
                    break;
                case QRCODE_REQUEST_CODE://二维码回调
                    if (webScanListener != null) {
                        webScanListener.putQrcodeTojs(data, webView);
                    }
                    break;
                case CONTACT_REQEST_CODE://联系人信息回调
//                    if(webContactsListener!=null){
                     webContactsListener = new WebContactsListener();
                    Uri uriContact = data.getData();
                        Cursor cursorContact = getContentResolver().query(uriContact, null, null, null, null);
                        try {
                            this.webContactsListener.getContactInfo(webView,MainActivity.this,cursorContact);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                    }
            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE://拍照取消操作
                    if (webTakePhotoListener != null) {
                        webTakePhotoListener.cancelPassPicture(webView);
                    }
                    break;
                case QRCODE_REQUEST_CODE://二维码操作
                    if (webScanListener != null) {
                        webScanListener.cancelScan(webView);
                    }
                    break;
            }
        }
    }

}
