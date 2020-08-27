package obj.quickblox.sample.chat.java.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.helper.StringifyArrayList;
import obj.quickblox.sample.chat.java.App;
import obj.quickblox.sample.chat.java.R;

import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.util.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static obj.quickblox.sample.chat.java.Prefrences.PrefrenceConstants.*;
import static obj.quickblox.sample.chat.java.utils.ResourceUtils.getString;

public class SharedPrefsHelper {
    private static final String SHARED_PREFS_NAME = "qb";
    private static final String QB_USER_ID = "qb_user_id";
    private static final String QB_USER_LOGIN = "qb_user_login";
    private static final String QB_USER_PASSWORD = "qb_user_password";
    private static final String QB_USER_FULL_NAME = "qb_user_full_name";
    private static final String QB_USER_TAGS = "qb_user_tags";
    private static final String Language_Set="set_language";
    private static final String Dialog_Name="Dialog_Name";
    private static SharedPrefsHelper instance;
    private static SharedPreferences sharedPreferences,mPreferences;
    SharedPreferences.Editor mEditor;
    private static final String PASSWORD="";
    private static final String PASSWORD_STATUS="true";
    private static final String SECURITY_QUSTION="SECURITY_QUSTION_Q";
    private static final String SECURITY_ANSWER="SECURITY_ANSWER_A";
    private static final String E_CARD_URL="ecard_url";
    private static final String FORWARD_sms="sms_for";
    private static final String SERCET_CHAT="0";
    private static final String LOGIN_SERVICE_STATUS="stop";
    private static final String QBChatDialog_DB = "QBChatDialog";
    private static final String QBChatMessage_Offline = "QBChatMessage_Offline";




    public static synchronized SharedPrefsHelper getInstance() {
        if (instance == null) {
            instance = new SharedPrefsHelper();
        }
        return instance;
    }

    private SharedPrefsHelper() {
        instance = this;
        sharedPreferences = App.getInstance().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        mPreferences= App.getInstance().getSharedPreferences("MyPREFERENCES_OnBoarding_sPRED", Context.MODE_PRIVATE);
        mEditor =mPreferences.edit();

    }

    public void delete(String key) {
        if (sharedPreferences.contains(key)) {
            getEditor().remove(key).commit();
        }
    }

    public void save(String key, Object value) {
        SharedPreferences.Editor editor = getEditor();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-supported preference");
        }

        editor.commit();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    private boolean has(String key) {
        return sharedPreferences.contains(key);
    }

    public void saveQbUser(QBUser qbUser) {
        save(QB_USER_ID, qbUser.getId());
        save(QB_USER_LOGIN, qbUser.getLogin());
        save(QB_USER_PASSWORD, qbUser.getPassword());
        save(QB_USER_FULL_NAME, qbUser.getFullName());
        save(QB_USER_TAGS, qbUser.getTags().getItemsAsString());
    }

    public void removeQbUser() {
        delete(QB_USER_ID);
        delete(QB_USER_LOGIN);
        delete(QB_USER_PASSWORD);
        delete(QB_USER_FULL_NAME);
        delete(QB_USER_TAGS);
    }

    public QBUser getQbUser() {
        if (hasQbUser()) {
            Integer id = get(QB_USER_ID);
            String login = get(QB_USER_LOGIN);
            String password = get(QB_USER_PASSWORD);
            String fullName = get(QB_USER_FULL_NAME);
            String tagsInString = get(QB_USER_TAGS);

            StringifyArrayList<String> tags = null;

            if (tagsInString != null) {
                tags = new StringifyArrayList<>();
                tags.add(tagsInString.split(","));
            }

            QBUser user = new QBUser(login, password);
            user.setId(id);
            user.setFullName(fullName);
            user.setTags(tags);
            return user;
        } else {
            return null;
        }
    }

    public void set_Language(String Lan)
    {
        save(Language_Set,Lan);
    }
    public String get_Language()
    {
        return get(Language_Set);
    }

    public void set_PASSWORD(String PAS)
    {
        save(PASSWORD,PAS);
    }
    public String get_PASSWORD()
    {
        return get(PASSWORD);
    }

    public void set_Dialog_Name(String Dialog_Name_UP)
    {
        save(Dialog_Name,Dialog_Name_UP);
    }
    public String get_Dialog_Name()
    {
        return get(Dialog_Name);
    }

    public void set_SECURITY_QUSTION(String QUSTION)
    {
        save(SECURITY_QUSTION,QUSTION);
    }
    public String get_SECURITY_QUSTION()
    {
        return get(SECURITY_QUSTION);
    }

    public void set_SECURITY_ANSWER(String ANSWER)
    {
        save(SECURITY_ANSWER,ANSWER);
    }
    public String get_SECURITY_ANSWER() { return get(SECURITY_ANSWER); }

    public void set_PASSWORD_STATUS(String PASSWORD_sat) {save(PASSWORD_STATUS,PASSWORD_sat); }
    public String get_PASSWORD_STATUS() { return get(PASSWORD_STATUS); }

    public void set_E_CARD_URL(String E_CARD_URL_sat) {save(E_CARD_URL,E_CARD_URL_sat); }
    public String get_E_CARD_URL() { return get(E_CARD_URL); }
    public void set_FORWARD (String FORWARD ) {save(FORWARD_sms,FORWARD); }
    public String get_FORWARD () { return get(FORWARD_sms); }

    public void set_SERCET_CHAT(String ssec) {save(SERCET_CHAT,ssec); }
    public String get_SERCET_CHAT() { return get(SERCET_CHAT); }


    public void set_LOGIN_SERVICE_STATUS(String ssec) {save(LOGIN_SERVICE_STATUS,ssec); }
    public String get_LOGIN_SERVICE_STATUS() { return get(LOGIN_SERVICE_STATUS); }


    public boolean hasQbUser() {
        return has(QB_USER_LOGIN) && has(QB_USER_PASSWORD);
    }

    public static void clearAllData() {
        SharedPreferences.Editor editor = getEditor();
        editor.clear().commit();
        mPreferences.edit().clear().commit();
    }
    private static SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }



    public int getAppVersion() {
        return mPreferences.getInt(PREFS_APP_VERSION, 1);
    }

    public String getServerUrl() {
        return mPreferences.getString(PREFS_SERVER_URL, null);
    }

    public void setServerUrl(String serverUrl) {
        mEditor.putString(PREFS_SERVER_URL, serverUrl).apply();
    }

    public String getProfilePhotostatus() {
        return mPreferences.getString("Profile_pfoto", getString(R.string.public_profile));
    }

    public void setProfilePhotostatus(String value) {
        mEditor.putString("Profile_pfoto", value).apply();
    }

    public String getUserId() {
        return mPreferences.getString(PREFS_USER_ID, "0");
    }

    public void setUserId(String value) {
        mEditor.putString(PREFS_USER_ID, value).apply();
    }


    public String getPasswordForSecretChat() {
        return mPreferences.getString("password", "0");
    }

    public void setPasswordForSecretChat(String value) {
        mEditor.putString("password", value).apply();
    }

    public String getProfileId() {
        return mPreferences.getString("PROFILE_ID", "0");
    }

    public void setProfileId(String value) {
        mEditor.putString("PROFILE_ID", value).apply();
    }

    public String getAppLang() {
        return mPreferences.getString("app_lang", "0");
    }

    public void setAppLang(String value) {
        mEditor.putString("app_lang", value).apply();
    }

    public String getEcardAppLang() {
        return mPreferences.getString("ecard_app_lang", "0");
    }

    public void setEcardAppLang(String value) {
        mEditor.putString("ecard_app_lang", value).apply();
    }

    public String getEconuterUsers() {
        return mPreferences.getString("user", "");
    }

    public void setEconuterUsers(String value) {
        mEditor.putString("user", value).apply();
    }

    public Boolean getPasswordSet() {
        return mPreferences.getBoolean("pass", false);
    }

    public void setPasswordSet(Boolean value) {
        mEditor.putBoolean("pass", value).apply();
    }

    public String getReminderNotify() {
        return mPreferences.getString("notify", "");
    }

    public void setReminderNotify(String value) {
        mEditor.putString("notify", value).apply();
    }

    public String getHideSpecificFriends() {
        return mPreferences.getString("hide_specific", "");
    }

    public void setHideSpecificFriends(String value) {
        mEditor.putString("hide_specific", value).apply();
    }


    public String getCallInfo() {
        return mPreferences.getString("CallInfo", "0");
    }

    public void setCallInfo(String value) {
        mEditor.putString("CallInfo", value).apply();
    }


    public Boolean getLastSeen() {
        return mPreferences.getBoolean("Last_Seen", false);
    }

    public void setLastSeen(Boolean value) {
        mEditor.putBoolean("Last_Seen", value).apply();
    }

    public Boolean getHideOnlineStatus() {
        return mPreferences.getBoolean("hide", false);
    }

    public void setHideOnlineStatus(Boolean value) {
        mEditor.putBoolean("hide", value).apply();
    }

    public Boolean getEncountersUse() {
        return mPreferences.getBoolean("use", true);
    }

    public void setEncountersUse(Boolean value) {
        mEditor.putBoolean("use", value).apply();
    }

    public Boolean getTuDimeMemebershipExpire() {
        return mPreferences.getBoolean("expire", false);
    }

    public void setTuDimeMemebershipExpire(Boolean value) {
        mEditor.putBoolean("expire", value).apply();
    }

    public String  getEncounterMsg() {
        return mPreferences.getString("en_msg", "");
    }

    public void setEncounterMsg(String  value) {
        mEditor.putString("en_msg", value).apply();
    }

    public String getChatBackupStatus() {
        return mPreferences.getString("backup_chat", "never");
    }

    public void setChatBackupStatus(String  value) {
        mEditor.putString("backup_chat", value).apply();
    }

    public Boolean isDiscoverMe() {
        return mPreferences.getBoolean(PREFS_DISCOVER_ME, false);
    }

    public void setDsicoverMe(boolean value) {
        mEditor.putBoolean(PREFS_DISCOVER_ME, value).apply();
    }

    public String getSelectedDate() {
        return mPreferences.getString("SelectedDate", "");
    }

    public void setSelectedDate(String SelectedDate) {
        mEditor.putString("SelectedDate", SelectedDate).apply();
    }
    public String getSelectedTime() {
        return mPreferences.getString("SelectedTime", "");
    }

    public void setSelectedTime(String SelectedTime) {
        mEditor.putString("SelectedTime", SelectedTime).apply();
    }

    public String getMembershipdialogDate() {
        return mPreferences.getString("mem_date", "");
    }

    public void setMembershipdialogDate(String SelectedDate) {
        mEditor.putString("mem_date", SelectedDate).apply();
    }

    public Boolean getVersion() {
        return mPreferences.getBoolean("version", false);
    }

    public void setVersion(Boolean SelectedDate) {
        mEditor.putBoolean("version", SelectedDate).apply();
    }

    public String getQbUserId() {
        return mPreferences.getString(PREFS_QB_USER_ID, "");
    }

    public void setQbUserId(String value) {
        mEditor.putString(PREFS_QB_USER_ID, value).apply();
    }

    public String getSessionToken() {
        return mPreferences.getString(PREFS_SESSION_TOKEN, "0");
    }

    public void setSessionToken(String value) {
        mEditor.putString(PREFS_SESSION_TOKEN, value).apply();
    }

    public String getDeviceToken() {
        return mPreferences.getString(PREFS_DEVICE_TOKEN, "");
    }

    public void setDeviceToken(String regid) {
        mEditor.putString(PREFS_DEVICE_TOKEN, regid).apply();
    }

    public String getDisableAccount() {
        return mPreferences.getString("account_diable", "");
    }

    public void setDisableAccount(String regid) {
        mEditor.putString("account_diable", regid).apply();
    }

    public String getUserName() {
        return mPreferences.getString(PREFS_USERNAME, "");
    }

    public void setUserName(String userName) {
        mEditor.putString(PREFS_USERNAME, userName).apply();
    }

    public void setPhoneNum(String phoneNumber) {
        mEditor.putString(PREFS_PHONE_NUM, phoneNumber).apply();
    }
    public void setUSERID(String USERID) {
        mEditor.putString(PREFS_USERID, USERID).apply();
    }

    public String getUSERID() {
        return mPreferences.getString(PREFS_USERID, null);
    }
    public String getPhoneNum() {
        return mPreferences.getString(PREFS_PHONE_NUM, null);
    }

    public void setCountryName(String countryName) {
        mEditor.putString(PREFS_COUNTRY_NAME, countryName).apply();
    }

    public String getCountryName() {
        return mPreferences.getString(PREFS_COUNTRY_NAME, "");
    }

    public void setCountryCode(String countryName) {
        mEditor.putString(PREFS_COUNTRY_CODE, countryName).apply();
    }

    public void set_wallpaper(String wallpaper) {
        mEditor.putString(CHAT_WINDOW_WALLPAPER, wallpaper).apply();
    }
    public String get__wallpaper(){
        return mPreferences.getString(CHAT_WINDOW_WALLPAPER, "");
    }

    public String getCountryCode() {
        return mPreferences.getString(PREFS_COUNTRY_CODE, "");
    }

    public void setNumberVerified(boolean isNumberVerified) {
        mEditor.putBoolean(PREFS_IS_NUMBER_VERIFIED, isNumberVerified).apply();
    }

    public boolean isNumberVerified() {
        return mPreferences.getBoolean(PREFS_IS_NUMBER_VERIFIED, false);
    }

    public void setAgreementAccepted(boolean isAgreementAccepted) {
        mEditor.putBoolean(PREFS_IS_AGREEMENT_ACCEPTED, isAgreementAccepted).apply();
    }

    public boolean isAgreementAccepted() {
        return mPreferences.getBoolean(PREFS_IS_AGREEMENT_ACCEPTED, false);
    }

    public void setProfileRegistrationDone(boolean isProfileRegistered) {
        mEditor.putBoolean(PREFS_IS_PROFILE_REGISTERED, isProfileRegistered).apply();
    }

    public boolean isProfileRegistered() {
        return mPreferences.getBoolean(PREFS_IS_PROFILE_REGISTERED, false);
    }

    public ArrayList<String> getAllStatus(Context mContext) {
        String allStatus = mPreferences.getString(PREFS_ALL_STATUS, "");
        if (StringUtils.isNullOrEmpty(allStatus)) {
            ArrayList<String> statusList = new ArrayList<String>();
            statusList.add(mContext.getString(R.string.status_default_ciaom));
            statusList.add(mContext.getString(R.string.status_available));
            statusList.add(mContext.getString(R.string.status_busy));
            statusList.add(mContext.getString(R.string.status_at_school));
            statusList.add(mContext.getString(R.string.status_at_work));
            statusList.add(mContext.getString(R.string.status_battery_about_to_die));
            statusList.add(mContext.getString(R.string.status_can_t_talk_vchat_only));
            statusList.add(mContext.getString(R.string.status_sleeping));
            statusList.add(mContext.getString(R.string.status_in_a_meeting));
            statusList.add(mContext.getString(R.string.status_urgent_calls_only));
            return statusList;
        }
        return new Gson().fromJson(allStatus, new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    public void setAllStatus(String status,Context mContext) {
        Gson gson = new Gson();
        ArrayList<String> statusList;
        String allStatus = mPreferences.getString(PREFS_ALL_STATUS, "");
        if (StringUtils.isNullOrEmpty(allStatus)) {
            statusList = new ArrayList<String>();
            statusList.add(mContext.getString(R.string.status_available));
            statusList.add(mContext.getString(R.string.status_busy));
            statusList.add(mContext.getString(R.string.status_at_school));
            statusList.add(mContext.getString(R.string.status_at_work));
            statusList.add(mContext.getString(R.string.status_battery_about_to_die));
            statusList.add(mContext.getString(R.string.status_can_t_talk_vchat_only));
            statusList.add(mContext.getString(R.string.status_sleeping));
            statusList.add(mContext.getString(R.string.status_in_a_meeting));
            statusList.add(mContext.getString(R.string.status_urgent_calls_only));
            statusList.add(0, status);
        } else {
            statusList = gson.fromJson(allStatus, ArrayList.class);
            if (!statusList.contains(status))
                statusList.add(0, status);
        }
        mEditor.putString(PREFS_ALL_STATUS, gson.toJson(statusList)).apply();
    }

    public void setKeyboardHeight(int keyboardHeight) {
        mEditor.putInt(PREFS_KEYBOARD_HEIGHT, keyboardHeight).apply();
    }

    public int getKeyboardHeight() {
        return mPreferences.getInt(PREFS_KEYBOARD_HEIGHT, -1);
    }

    public String getProfilePic() {
        return mPreferences.getString(PREFS_PROFILE_PIC, "");
    }

    public void setProfilePic(String profielPic) {
        mEditor.putString(PREFS_PROFILE_PIC, profielPic).apply();
    }

    public void setWallpaperName(String wallpaperName) {
        mEditor.putString(PREFS_WALLPAPER, wallpaperName).apply();
    }

    public String getWallpaperName() {
        return mPreferences.getString(PREFS_WALLPAPER, "theme_1");
    }

    public void setCurrentStatus(String status) {
        mEditor.putString(PREFS_CURRENT_STATUS, status).apply();
    }

    public String getCurrentStatus() {
        return mPreferences.getString(PREFS_CURRENT_STATUS, "");
    }

    public void setRandomUserId(String randomUserId) {
        mEditor.putString(PREFS_RANDOM_ID_FOR_CONTACT, randomUserId).apply();
    }

    public String getRandomUserId() {
        return mPreferences.getString(PREFS_RANDOM_ID_FOR_CONTACT, "");
    }


    public void setRingtoneTitle(String selectedRingTone) {
        mEditor.putString(PREFS_RINGTONE_TITLE, selectedRingTone).apply();
    }

    public String getRingtoneTitle() {
        return mPreferences.getString(PREFS_RINGTONE_TITLE, "Default");
    }

    public void setRingtoneUri(String selectedRingToneUri) {
        mEditor.putString(PREFS_RINGTONE_URI, selectedRingToneUri).apply();
    }

    public String getTotalCredit() {
        return mPreferences.getString("Total_credit", "Default");
    }

    public void setTotalCredit(String total_credit) {
        mEditor.putString("Total_credit", total_credit).apply();
    }


    public String getRingtoneUri() {
        return mPreferences.getString(PREFS_RINGTONE_URI, Settings.System.DEFAULT_NOTIFICATION_URI.toString());
    }

    public void setRingtoneUriEncounter(String selectedRingToneUriEncounter) {
        mEditor.putString("encounter_uri", selectedRingToneUriEncounter).apply();
    }

    public String getRingtoneUriEncounter() {
        return mPreferences.getString("encounter_uri", Settings.System.DEFAULT_NOTIFICATION_URI.toString());
    }


    public void setRingtoneTitleGroup(String selectedRingToneGroup) {
        mEditor.putString(PREFS_RINGTONE_TITLE_GROUP, selectedRingToneGroup).apply();
    }

    public String getRingtoneTitleGroup() {
        return mPreferences.getString(PREFS_RINGTONE_TITLE_GROUP, "Default");
    }

    public void setRingtoneUriGroup(String selectedRingToneUriGroup) {
        mEditor.putString(PREFS_RINGTONE_URI_GROUP, selectedRingToneUriGroup).apply();
    }

    public String getRingtoneUriGroup() {
        return mPreferences.getString(PREFS_RINGTONE_URI_GROUP, Settings.System.DEFAULT_NOTIFICATION_URI.toString());
    }

    public void setRingtoneTitleCall(String selectedRingToneCall) {
        mEditor.putString(PREFS_RINGTONE_TITLE_CALL, selectedRingToneCall).apply();
    }

    public String getRingtoneTitleCall() {
        return mPreferences.getString(PREFS_RINGTONE_TITLE_CALL, "Default");
    }

    public void setRingtoneTitleEncounters(String selectedRingToneCall) {
        mEditor.putString("ringtone_encounter", selectedRingToneCall).apply();
    }

    public String getRingtoneTitleEncounters() {
        return mPreferences.getString("ringtone_encounter", "Default");
    }

    public void setRingtoneUriCall(String selectedRingToneUriCall) {
        mEditor.putString(PREFS_RINGTONE_URI_CALL, selectedRingToneUriCall).apply();
    }

    public String getRingtoneUriCall() {
        return mPreferences.getString(PREFS_RINGTONE_URI_CALL, Settings.System.DEFAULT_RINGTONE_URI.toString());
    }


    public void setVibration(String selectedVibration) {
        mEditor.putString(PREFS_VIBRATE, selectedVibration).apply();
    }

    public String getVibration() {
        return mPreferences.getString(PREFS_VIBRATE, "1");
    }

    public String getVibrationGroup() {
        return mPreferences.getString(PREFS_VIBRATE_GROUP, "1");
    }

    public void setVibrationGroup(String selectedVibration) {
        mEditor.putString(PREFS_VIBRATE_GROUP, selectedVibration).apply();
    }

    public String getVibrationCall() {
        return mPreferences.getString(PREFS_VIBRATE_CALL, "1");
    }

    public void setVibrationCall(String selectedVibration) {
        mEditor.putString(PREFS_VIBRATE_CALL, selectedVibration).apply();
    }

    public String getVibrationEncounters() {
        return mPreferences.getString("vibrate_encounters", "1");
    }

    public void setVibrationEncounters(String selectedVibration) {
        mEditor.putString("vibrate_encounters", selectedVibration).apply();
    }


    public void setContactSync(boolean isUpdated) {
        mEditor.putBoolean("conSyn", isUpdated).apply();
    }

    public boolean getContactSync() {
        return mPreferences.getBoolean("conSyn", false);
    }


    public void setVoiceMsg(boolean isVoice) {
        mEditor.putBoolean("isVoice", isVoice).apply();
    }

    public boolean getVoiceMsg() {
        return mPreferences.getBoolean("isVoice", false);
    }


    public void setQbUserIdUpdatedOnServer(boolean isUpdated) {
        mEditor.putBoolean(PREFS_IS_QB_USER_ID_UPDATED_ON_SERVER, isUpdated).apply();
    }

    public boolean getQbUserIdUpdatedOnServer() {
        return mPreferences.getBoolean(PREFS_IS_QB_USER_ID_UPDATED_ON_SERVER, false);
    }

    public void setShowChatMessagePreview(boolean isChecked) {
        mEditor.putBoolean(PREFS_SHOW_MESSAGE_PREVIEW, isChecked).apply();
    }

    public boolean isShowChatMessagePreview() {
        return mPreferences.getBoolean(PREFS_SHOW_MESSAGE_PREVIEW, true);
    }

    public void setAutoDownloadMedia(boolean isChecked) {
        mEditor.putBoolean(PREFS_AUTO_DOWNLOAD_MEDIA, isChecked).apply();
    }

    public boolean isAutoDownloadMedia() {
        return mPreferences.getBoolean(PREFS_AUTO_DOWNLOAD_MEDIA, true);
    }

    public String getTimeLineResponse() {
        return mPreferences.getString(PREFS_TIMELINE_RESPONSE, "");
    }

    public void setTimeLineResponse(String value) {
        mEditor.putString(PREFS_TIMELINE_RESPONSE, value).apply();
    }


    public int getRadarDistance() {
        return mPreferences.getInt(PREFS_RADAR_DISTANCE, 0);
    }

    public void setRadarDistance(int value) {
        mEditor.putInt(PREFS_RADAR_DISTANCE, value).apply();
    }

    public int getPrivacyPosition() {
        return mPreferences.getInt(PREFS_PRIVACY_SELECTED_POSITION, 0);
    }

    public void setPrivacyPosition(int value) {
        mEditor.putInt(PREFS_PRIVACY_SELECTED_POSITION, value).apply();
    }

    //Timeline settings
    public boolean getLikeNotification() {
        return mPreferences.getBoolean(PREFS_TIMELINE_SETTING_LIKES, false);
    }

    public void setLikeNotification(boolean isLike) {
        mEditor.putBoolean(PREFS_TIMELINE_SETTING_LIKES, isLike).apply();
    }


    public boolean getCommentNotification() {
        return mPreferences.getBoolean(PREFS_TIMELINE_SETTING_COMMENTS, false);
    }

    public void setCommentNotification(boolean isComment) {
        mEditor.putBoolean(PREFS_TIMELINE_SETTING_COMMENTS, isComment).apply();
    }

    public boolean getEcardstype() {
        return mPreferences.getBoolean("ecardstype", false);
    }

    public void setEcardstype(boolean isComment) {
        mEditor.putBoolean("ecardstype", isComment).apply();
    }

    public boolean getEcardsUrl() {
        return mPreferences.getBoolean("ecardsUrl", false);
    }

    public void setEcardsUrl(boolean ecardsUrl) {
        mEditor.putBoolean("ecardsUrl", ecardsUrl).apply();
    }

    public String getEncounterPosition() {
        return mPreferences.getString("position", "");
    }

    public void setEncounterPosition(String position) {
        mEditor.putString("position", position).apply();
    }

    public String getEncounterNAme() {
        return mPreferences.getString("name", "");
    }

    public void setEncounterNAme(String position) {
        mEditor.putString("name", position).apply();
    }

    public String getChattingPersonData() {
        return mPreferences.getString("getChattingPersonData", "");
    }

    public void setChattingPersonData(String position) {
        mEditor.putString("getChattingPersonData", position).apply();
    }

    public boolean getTudimeSendCardStatus() {
        return mPreferences.getBoolean("getTudimeSendCardStatus", false);
    }

    public void setTudimeSendCardStatus(boolean position) {
        mEditor.putBoolean("getTudimeSendCardStatus", position).apply();
    }

    public boolean getAllowTagNotification() {
        return mPreferences.getBoolean(PREFS_TIMELINE_SETTING_ALLOW_TAG, false);
    }

    public void setAllowTagNotification(boolean isTagNotification) {
        mEditor.putBoolean(PREFS_TIMELINE_SETTING_ALLOW_TAG, isTagNotification).apply();
    }

    public String getRelationshipStatus() {
        return mPreferences.getString("rel", "");
    }

    public void setRelationshipStatus(String rel) {
        mEditor.putString("rel", rel).apply();
    }

    public String getSexualityStatus() {
        return mPreferences.getString("sexuality", "");
    }

    public void setSexualityStatus(String sexuality) {
        mEditor.putString("sexuality", sexuality).apply();
    }

    public String getKnownLanguage() {
        return mPreferences.getString("language", "");
    }

    public void setKnownLanguage(String language) {
        mEditor.putString("language", language).apply();
    }

    public String getInterest() {
        return mPreferences.getString("interest", "");
    }

    public void setInterest(String interest) {
        mEditor.putString("interest", interest).apply();
    }

    public String getAge() {
        return mPreferences.getString("age", "");
    }


    public void setAge(String age) {
        mEditor.putString("age", age).apply();
    }

    public String getHereTo() {
        return mPreferences.getString("here", "");
    }

    public void setHereTo(String here) {
        mEditor.putString("here", here).apply();
    }

    public String getDateBday() {
        return mPreferences.getString("date", "");
    }

    public void setDateBday(String here) {
        mEditor.putString("date", here).apply();
    }

    public String getMonth() {
        return mPreferences.getString("mnth", "");
    }

    public void setMonth(String here) {
        mEditor.putString("mnth", here).apply();
    }

    public String getAboutYourself() {
        return mPreferences.getString("about", "");
    }

    public void setAboutYourself(String about) {
        mEditor.putString("about", about).apply();
    }

    public String getCurrentLocation() {
        return mPreferences.getString("loc", "");
    }

    public void setCurrentLocation(String loc) {
        mEditor.putString("loc", loc).apply();
    }

    public String getMinAgeRange() {
        return mPreferences.getString("MinageRange", "");
    }

    public void setMinAgeRange(String age) {
        mEditor.putString("MinageRange", age).apply();
    }

    public String getMinRange() {
        return mPreferences.getString("MinRange", "");
    }

    public void setMinRange(String age) {
        mEditor.putString("MinRange", age).apply();
    }

    public String getMaxAgeRange() {
        return mPreferences.getString("MaxageRange", "");
    }

    public void setMaxAgeRange(String age) {
        mEditor.putString("MaxageRange", age).apply();
    }

    public String getMaxRange() {
        return mPreferences.getString("MaxRange", "");
    }

    public void setMaxRange(String age) {
        mEditor.putString("MaxRange", age).apply();
    }

    public String getProfileEncounters() {
        return mPreferences.getString("EProfile", "");
    }

    public void setProfileEncounters(String age) {
        mEditor.putString("EProfile", age).apply();
    }

    public String getRangeType() {
        return mPreferences.getString("rangeType", "");
    }

    public void setRangeType(String age) {
        mEditor.putString("rangeType", age).apply();
    }

    public String getLookingFor() {
        return mPreferences.getString("looking", "");
    }

    public void setLookingFor(String age) {
        mEditor.putString("looking", age).apply();
    }

    public String getLoveLoadCount() {
        return mPreferences.getString("love_count", "0");
    }

    public void setLoveLoadCount(String age) {
        mEditor.putString("love_count", age).apply();
    }

    public String getBdayLoadCount() {
        return mPreferences.getString("Bday_count", "0");
    }

    public void setBdayLoadCount(String age) {
        mEditor.putString("Bday_count", age).apply();
    }

    public String getFriendLoadCount() {
        return mPreferences.getString("Friend_count", "0");
    }

    public void setFriendLoadCount(String age) {
        mEditor.putString("Friend_count", age).apply();
    }

    public String getSomeSpecLoadCount() {
        return mPreferences.getString("SomeSpec_count", "0");
    }

    public void setSomeSpecLoadCount(String age) {
        mEditor.putString("SomeSpec_count", age).apply();
    }

    public String getMissLoadCount() {
        return mPreferences.getString("Miss_count", "0");
    }

    public void setMissLoadCount(String age) {
        mEditor.putString("Miss_count", age).apply();
    }

    public String getCongoLoadCount() {
        return mPreferences.getString("Congo_count", "0");
    }

    public void setCongoLoadCount(String age) {
        mEditor.putString("Congo_count", age).apply();
    }

    public String getLuckLoadCount() {
        return mPreferences.getString("luck_count", "0");
    }

    public void setLuckLoadCount(String age) {
        mEditor.putString("luck_count", age).apply();
    }

    public String getWelleLoadCount() {
        return mPreferences.getString("Well_count", "0");
    }

    public void setWellLoadCount(String age) {
        mEditor.putString("Well_count", age).apply();
    }

    public String getInviteLoadCount() {
        return mPreferences.getString("Invite_count", "0");
    }

    public void setInviteLoadCount(String age) {
        mEditor.putString("Invite_count", age).apply();
    }

    public String getThankLoadCount() {
        return mPreferences.getString("Thank_count", "0");
    }

    public void setThankLoadCount(String age) {
        mEditor.putString("Thank_count", age).apply();
    }

    public String getSorryLoadCount() {
        return mPreferences.getString("Sorry_count", "0");
    }

    public void setSorryLoadCount(String age) {
        mEditor.putString("Sorry_count", age).apply();
    }

    public String getLoveLoadCountHindi() {
        return mPreferences.getString("love_countHindi", "0");
    }

    public void setLoveLoadCountHindi(String age) {
        mEditor.putString("love_countHindi", age).apply();
    }

    public String getBdayLoadCountHindi() {
        return mPreferences.getString("Bday_countHindi", "0");
    }

    public void setBdayLoadCountHindi(String age) {
        mEditor.putString("Bday_countHindi", age).apply();
    }

    public String getFriendLoadCountHindi() {
        return mPreferences.getString("Friend_countHindi", "0");
    }

    public void setFriendLoadCountHindi(String age) {
        mEditor.putString("Friend_countHindi", age).apply();
    }

    public String getSomeSpecLoadCountHindi() {
        return mPreferences.getString("SomeSpec_countHindi", "0");
    }

    public void setSomeSpecLoadCountHindi(String age) {
        mEditor.putString("SomeSpec_countHindi", age).apply();
    }

    public String getMissLoadCountHindi() {
        return mPreferences.getString("Miss_countHindi", "0");
    }

    public void setMissLoadCountHindi(String age) {
        mEditor.putString("Miss_countHindi", age).apply();
    }

    public String getCongoLoadCountHindi() {
        return mPreferences.getString("Congo_countHindi", "0");
    }

    public void setCongoLoadCountHindi(String age) {
        mEditor.putString("Congo_countHindi", age).apply();
    }

    public String getLuckLoadCountHindi() {
        return mPreferences.getString("luck_countHindi", "0");
    }

    public void setLuckLoadCountHindi(String age) {
        mEditor.putString("luck_countHindi", age).apply();
    }

    public String getWelleLoadCountHindi() {
        return mPreferences.getString("Well_countHindi", "0");
    }

    public void setWellLoadCountHindi(String age) {
        mEditor.putString("Well_countHindi", age).apply();
    }

    public String getInviteLoadCountHindi() {
        return mPreferences.getString("Invite_countHindi", "0");
    }

    public void setInviteLoadCountHindi(String age) {
        mEditor.putString("Invite_countHindi", age).apply();
    }

    public String getThankLoadCountHindi() {
        return mPreferences.getString("Thank_countHindi", "0");
    }

    public void setThankLoadCountHindi(String age) {
        mEditor.putString("Thank_countHindi", age).apply();
    }

    public String getSorryLoadCountHindi() {
        return mPreferences.getString("Sorry_countHindi", "0");
    }

    public void setSorryLoadCountHindi(String age) {
        mEditor.putString("Sorry_countHindi", age).apply();
    }

    public String getLoveLoadCountSpanish() {
        return mPreferences.getString("love_countSpanish", "0");
    }

    public void setLoveLoadCountSpanish(String age) {
        mEditor.putString("love_countSpanish", age).apply();
    }

    public String getBdayLoadCountSpanish() {
        return mPreferences.getString("Bday_countSpanish", "0");
    }

    public void setBdayLoadCountSpanish(String age) {
        mEditor.putString("Bday_countSpanish", age).apply();
    }

    public String getFriendLoadCountSpanish() {
        return mPreferences.getString("Friend_countSpanish", "0");
    }

    public void setFriendLoadCountSpanish(String age) {
        mEditor.putString("Friend_countSpanish", age).apply();
    }

    public String getSomeSpecLoadCountSpanish() {
        return mPreferences.getString("SomeSpec_countSpanish", "0");
    }

    public void setSomeSpecLoadCountSpanish(String age) {
        mEditor.putString("SomeSpec_countSpanish", age).apply();
    }

    public String getMissLoadCountSpanish() {
        return mPreferences.getString("Miss_countSpanish", "0");
    }

    public void setMissLoadCountSpanish(String age) {
        mEditor.putString("Miss_countSpanish", age).apply();
    }

    public String getCongoLoadCountSpanish() {
        return mPreferences.getString("Congo_countSpanish", "0");
    }

    public void setCongoLoadCountSpanish(String age) {
        mEditor.putString("Congo_countSpanish", age).apply();
    }

    public String getLuckLoadCountSpanish() {
        return mPreferences.getString("luck_countSpanish", "0");
    }

    public void setLuckLoadCountSpanish(String age) {
        mEditor.putString("luck_countSpanish", age).apply();
    }

    public String getWelleLoadCountSpanish() {
        return mPreferences.getString("Well_countSpanish", "0");
    }

    public void setWellLoadCountSpanish(String age) {
        mEditor.putString("Well_countSpanish", age).apply();
    }

    public String getInviteLoadCountSpanish() {
        return mPreferences.getString("Invite_countSpanish", "0");
    }

    public void setInviteLoadCountSpanish(String age) {
        mEditor.putString("Invite_countSpanish", age).apply();
    }

    public String getThankLoadCountSpanish() {
        return mPreferences.getString("Thank_countSpanish", "0");
    }

    public void setThankLoadCountSpanish(String age) {
        mEditor.putString("Thank_countSpanish", age).apply();
    }

    public String getSorryLoadCountSpanish() {
        return mPreferences.getString("Sorry_countSpanish", "0");
    }

    public void setSorryLoadCountSpanish(String age) {
        mEditor.putString("Sorry_countSpanish", age).apply();
    }

    public boolean getAutoEcardSend() {
        return mPreferences.getBoolean("getAutoEcardSend", false);
    }

    public void setAutoEcardSend(boolean age) {
        mEditor.putBoolean("getAutoEcardSend", age).apply();
    }

    public Collection<QBChatDialog> getQBChatDialog_DB() {
        Gson gson = new Gson();
        String json = mPreferences.getString(QBChatDialog_DB, "");
            Type type = new TypeToken<Collection<QBChatDialog>>() {
            }.getType();
            return gson.fromJson(json, type);

    }
    public void setQBChatDialog_DB(Collection<QBChatDialog> value) {
        Gson gson = new Gson();
        String json = gson.toJson(value);
        mEditor.putString(QBChatDialog_DB,json).apply();
    }



    public ArrayList<ArrayList<QBChatMessage>> getQBChatMessage_Offline() {
        Gson gson = new Gson();
        String json = mPreferences.getString(QBChatMessage_Offline, "");
        Type type = new TypeToken<ArrayList<ArrayList<QBChatMessage>>>() {
        }.getType();
        return gson.fromJson(json, type);

    }
    public void setQBChatMessage_Offline(ArrayList<ArrayList<QBChatMessage>> value) {
        Gson gson = new Gson();
        String json = gson.toJson(value);
        mEditor.putString(QBChatMessage_Offline,json).apply();
    }

}