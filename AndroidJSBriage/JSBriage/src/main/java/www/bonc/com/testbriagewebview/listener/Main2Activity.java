package www.bonc.com.testbriagewebview.listener;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import www.bonc.com.testbriagewebview.R;

public class Main2Activity extends Activity {
    Button pick;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        pick = (Button) findViewById(R.id.pick);
        pick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                Main2Activity.this.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = managedQuery(contactData, null, null, null,
                            null);
                   // getContactPhone(cursor);
                    WebContactsListener webContactsListener = new WebContactsListener();
                    try {
                        webContactsListener.getContactInfo(null,Main2Activity.this,cursor);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                break;
        }
    }

    private String getContactPhone(Cursor cursor) {
        // TODO Auto-generated method stub
        int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String result = "";
        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人电话的cursor
            Cursor phone = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                            + contactId, null, null);
            if (phone.moveToFirst()) {
                for (; !phone.isAfterLast(); phone.moveToNext()) {
                    int index = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeindex = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int phone_type = phone.getInt(typeindex);
                    String phoneNumber = phone.getString(index);
                    result = phoneNumber;
//                  switch (phone_type) {//此处请看下方注释
//                  case 2:
//                      result = phoneNumber;
//                      break;
//
//                  default:
//                      break;
//                  }
                }
                if (!phone.isClosed()) {
                    phone.close();
                }
            }
        }
        return result;
    }



}
