package www.bonc.com.testbriagewebview.listener;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cuibg on 2016/11/24.
 * 处理联系人的类
 */

public class WebContactsListener {
    /**
     * 添加联系人
     */
    public void addContact() {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
    }

    /**
     * 获取联系人信息
     *
     * @param webView
     * @param context
     */
    public void getContactInfo(WebView webView, Context context, Cursor currentCursor) throws JSONException {
        currentCursor.moveToFirst();
        int idColumn = currentCursor.getColumnIndex(ContactsContract.Contacts._ID);
        String currentContactId = currentCursor.getString(idColumn);//当前选中的id号
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + currentContactId, null, null);

        JSONObject jsonObject = new JSONObject();
        JSONArray phoneArray = new JSONArray();//放手机信息的
        JSONObject addressObject = new JSONObject();//放通讯地址
        JSONObject ortypObject = new JSONObject();//组织信息
        JSONObject jsonImObject = new JSONObject();//即时通讯地址
        JSONArray emailArray = new JSONArray();//邮箱地址
        JSONArray urlArray = new JSONArray();//网站页
        String mimetype = "";
        while (cursor.moveToNext()) {
            mimetype = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE)); // 取得mimetype类型,扩展的数据都在这个类型里面
            switch (mimetype) {
                //联系人各种名字
                case StructuredName.CONTENT_ITEM_TYPE:
                    JSONObject nameObject = new JSONObject();
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                    String prefix = cursor.getString(cursor.getColumnIndex(StructuredName.PREFIX));
                    nameObject.put("honorificPrefix", prefix);
                    String firstName = cursor.getString(cursor.getColumnIndex(StructuredName.FAMILY_NAME));
                    nameObject.put("familyName", firstName);
                    String middleName = cursor.getString(cursor.getColumnIndex(StructuredName.MIDDLE_NAME));
                    nameObject.put("middleName", middleName);
                    String lastname = cursor.getString(cursor.getColumnIndex(StructuredName.GIVEN_NAME));
                    nameObject.put("givenName", lastname);
                    String suffix = cursor.getString(cursor.getColumnIndex(StructuredName.SUFFIX));
                    nameObject.put("honorificSuffix", suffix);
                    jsonObject.put("name", nameObject);
                    break;
                //联系人各种电话
                case Phone.CONTENT_ITEM_TYPE:
                    int phoneType = cursor.getInt(cursor.getColumnIndex(Phone.TYPE)); // 手机
                    switch (phoneType) {
                        case Phone.TYPE_MOBILE:
                            String mobile = cursor.getString(cursor
                                    .getColumnIndex(Phone.NUMBER));
                            phoneArray.put(mobile);
                            break;
                        // 住宅电话
                        case Phone.TYPE_HOME:
                            String homeNum = cursor.getString(cursor
                                    .getColumnIndex(Phone.NUMBER));
                            phoneArray.put(homeNum);
                            break;
                        // 单位电话
                        case Phone.TYPE_WORK:
                            String jobNum = cursor.getString(cursor
                                    .getColumnIndex(Phone.NUMBER));
                            phoneArray.put(jobNum);
                            break;
                        // 单位传真
                        case Phone.TYPE_FAX_WORK:
                            String workFax = cursor.getString(cursor
                                    .getColumnIndex(Phone.NUMBER));
                            phoneArray.put(workFax);
                            break;
                        // 住宅传真
                        case Phone.TYPE_FAX_HOME:
                            String homeFax = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                            phoneArray.put(homeFax);
                            break;
                        // 寻呼机
                        case Phone.TYPE_PAGER:
                            String pager = cursor.getString(cursor
                                    .getColumnIndex(Phone.NUMBER));
                            phoneArray.put(pager);
                            break;
                        // 回拨号码
                        case Phone.TYPE_CALLBACK:
                            String quickNum = cursor.getString(cursor
                                    .getColumnIndex(Phone.NUMBER));
                            phoneArray.put(quickNum);
                            break;
                        // 公司总机
                        case Phone.TYPE_COMPANY_MAIN:
                            String jobTel = cursor.getString(cursor
                                    .getColumnIndex(Phone.NUMBER));
                            phoneArray.put(jobTel);
                            break;
                        // 车载电话
                        case Phone.TYPE_CAR:
                            String carNum = cursor.getString(cursor
                                    .getColumnIndex(Phone.NUMBER));
                            phoneArray.put(carNum);
                            break;
                        // ISDN
                        case Phone.TYPE_ISDN:
                            String isdn = cursor.getString(cursor
                                    .getColumnIndex(Phone.NUMBER));
                            phoneArray.put(isdn);
                            break;
                        // 总机
                        case Phone.TYPE_MAIN:
                            String tel = cursor.getString(cursor
                                    .getColumnIndex(Phone.NUMBER));
                            phoneArray.put(tel);
                            break;
                        // 无线装置
                        case Phone.TYPE_RADIO:
                            String wirelessDev = cursor.getString(cursor
                                    .getColumnIndex(Phone.NUMBER));
                            phoneArray.put(wirelessDev);
                            break;
                        // 单位手机
                        case Phone.TYPE_WORK_MOBILE:
                            String jobMobile = cursor.getString(cursor
                                    .getColumnIndex(Phone.NUMBER));
                            phoneArray.put(jobMobile);
                            break;
                    }

                    break;
                //获取组织信息
                case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE:
                    int orgType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE)); // 单位
                    if (orgType == ContactsContract.CommonDataKinds.Organization.TYPE_CUSTOM) {
                        String company = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                        ortypObject.put("company", company);
                        String jobTitle = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                        ortypObject.put("jobTitle", jobTitle);
                        String department = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Organization.DEPARTMENT));
                        ortypObject.put("department", department);
                    }
                    break;

                case Im.CONTENT_ITEM_TYPE://获取即时通讯地址
                    int protocal = cursor.getInt(cursor.getColumnIndex(Im.PROTOCOL));
                    if (Im.TYPE_CUSTOM == protocal) {
                        String workMsg = cursor.getString(cursor
                                .getColumnIndex(Im.DATA));
                        jsonImObject.put("workMsg", workMsg);
                    } else if (Im.PROTOCOL_MSN == protocal) {
                        String workMsg = cursor.getString(cursor
                                .getColumnIndex(Im.DATA));
                        jsonImObject.put("workMsg", workMsg);
                    }
                    if (Im.PROTOCOL_QQ == protocal) {
                        String instantsMsg = cursor.getString(cursor
                                .getColumnIndex(Im.DATA));
                        jsonImObject.put("instantsMsg", instantsMsg);
                    }
                    break;
                //通讯地址
                case StructuredPostal.CONTENT_ITEM_TYPE:
                    int postalType = cursor.getInt(cursor
                            .getColumnIndex(StructuredPostal.TYPE));
                    // 单位通讯地址
                    if (postalType == StructuredPostal.TYPE_WORK) {
                        String street = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.STREET));
                        addressObject.put("street", street);
                        String ciry = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.CITY));
                        addressObject.put("ciry", ciry);
                        String box = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.POBOX));
                        addressObject.put("box", box);
                        String area = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.NEIGHBORHOOD));
                        addressObject.put("area", area);

                        String state = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.REGION));
                        addressObject.put("state", state);
                        String zip = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.POSTCODE));
                        addressObject.put("zip", zip);
                        String country = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.COUNTRY));
                        addressObject.put("country", country);
                    }
                    // 住宅通讯地址
                    if (postalType == StructuredPostal.TYPE_HOME) {
                        String homeStreet = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.STREET));
                        addressObject.put("homeStreet", homeStreet);
                        String homeCity = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.CITY));
                        addressObject.put("homeCity", homeCity);
                        String homeBox = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.POBOX));
                        addressObject.put("homeBox", homeBox);
                        String homeArea = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.NEIGHBORHOOD));
                        addressObject.put("homeArea", homeArea);
                        String homeState = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.REGION));
                        addressObject.put("homeState", homeState);
                        String homeZip = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.POSTCODE));
                        addressObject.put("homeZip", homeZip);
                        String homeCountry = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.COUNTRY));
                        addressObject.put("homeCountry", homeCountry);

                    }
                    // 其他通讯地址
                    if (postalType == StructuredPostal.TYPE_OTHER) {
                        String otherStreet = cursor.getString(cursor.getColumnIndex(StructuredPostal.STREET));
                        addressObject.put("otherStreet", otherStreet);

                        String otherCity = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.CITY));
                        addressObject.put("otherCity", otherCity);
                        String otherBox = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.POBOX));
                        addressObject.put("otherBox", otherBox);
                        String otherArea = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.NEIGHBORHOOD));
                        addressObject.put("otherArea", otherArea);
                        String otherState = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.REGION));
                        addressObject.put("otherState", otherState);
                        String otherZip = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.POSTCODE));
                        addressObject.put("otherZip", otherZip);
                        String otherCountry = cursor.getString(cursor
                                .getColumnIndex(StructuredPostal.COUNTRY));
                        addressObject.put("otherCountry", otherCountry);
                    }
                    break;
                //nickName
                case Nickname.CONTENT_ITEM_TYPE:
                    String nickName = cursor.getString(cursor
                            .getColumnIndex(Nickname.NAME));
                    jsonObject.put("nickName", nickName);
                    break;
                //备注
                case Note.CONTENT_ITEM_TYPE:
                    String remark = cursor.getString(cursor.getColumnIndex(Note.NOTE));
                    jsonObject.put("note", remark);
                    break;
                case Email.CONTENT_ITEM_TYPE://邮箱地址
                    String emailAddress = cursor.getString(cursor.getColumnIndex(Email.ADDRESS));
                    emailArray.put(emailAddress);
                    break;
                //网站页
                case Website.CONTENT_ITEM_TYPE:
                    int webType = cursor.getInt(cursor.getColumnIndex(Website.TYPE)); // 主页

                    if (webType == Website.TYPE_CUSTOM) {
                        String home = cursor.getString(cursor
                                .getColumnIndex(Website.URL));
                        urlArray.put(home);
                    } // 主页
                    else if (webType == Website.TYPE_HOME) {
                        String home = cursor.getString(cursor
                                .getColumnIndex(Website.URL));
                        urlArray.put(home);
                    }
                    // 个人主页
                    if (webType == Website.TYPE_HOMEPAGE) {
                        String homePage = cursor.getString(cursor
                                .getColumnIndex(Website.URL));
                        urlArray.put(homePage);
                    }
                    // 工作主页
                    if (webType == Website.TYPE_WORK) {
                        String workPage = cursor.getString(cursor
                                .getColumnIndex(Website.URL));
                        urlArray.put(workPage);
                    }

                    break;

            }

        }
        jsonObject.put("phoneNumbers", phoneArray);
        jsonObject.put("organizations", ortypObject);
        jsonObject.put("ims", jsonImObject);
        jsonObject.put("addresses", addressObject);
        jsonObject.put("emails", emailArray);
        jsonObject.put("urls", urlArray);
        Log.i("fuckbonc",jsonObject+"");
        if (!cursor.isClosed()) {
            cursor.close();
        }
    }
}
