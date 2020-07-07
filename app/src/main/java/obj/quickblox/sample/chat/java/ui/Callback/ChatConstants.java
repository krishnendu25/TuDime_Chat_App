package obj.quickblox.sample.chat.java.ui.Callback;

import obj.quickblox.sample.chat.java.constants.ApiConstants;

public interface ChatConstants {
//    String STAGING_CHAT_DOMAIN = "xmppdev.ciaoim.com";
//    String PRODUCTION_CHAT_DOMAIN = "xantatechcms.com";

    String DOMAIN_NAME =  "";
    String HOST = DOMAIN_NAME;
    int PORT = 5222;
    String GROUP_CHAT_DOMAIN = "conference." + DOMAIN_NAME;
    String BROADCAST_CHAT_DOMAIN = "multicast." + DOMAIN_NAME;



    int CONNECTION_RETRY_ON_CLOSE = 3000;
    int RECONNECTION_DURATION = 10000;

    String EXTRA_CHAT_USER_NAME = "extra_chat_user_name";
    String EXTRA_LOGIN_EMAIL = "extra_login_email";
    String EXTRA_LOGIN_PASSWORD = "extra_login_password";

    /**
     * Constants to be used in bundles and intents
     */
    String EXTRA_FRIEND_CHAT_USER_NAME = "extra_friend_chat_user_name";
    String EXTRA_FRIEND_CHAT_DISPLAY_NAME = "extra_friend_chat_display_name";
    String EXTRA_CHAT_MESSAGE = "extra_chat_message";
    String EXTRA_FRIEND_ITEM = "extra_friend_item";
    String EXTRA_CHAT_STATE = "extra_chat_state";
    String EXTRA_MESSAGE_FROM = "extra_message_from";
    String EXTRA_MESSAGE_DELIVARY_STATUS = "extra_message_delivary_status";
    String EXTRA_MESSAGE_ID = "extra_message_id";
    String EXTRA_IS_CONNECTED = "extra_is_connected";
    String EXTRA_LAST_SEEN_SECONDS = "extra_last_seen_seconds";
    String EXTRA_CHAT_MODEL = "extra_chat_model";
    String EXTRA_CHAT_FIRST_NAME = "extra_chat_first_name";
    String EXTRA_CHAT_PROFILE_PIC = "extra_chat_profile_pic";
    String EXTRA_CHAT_DISPLAY_NAME = "extra_chat_display_name";

    /**
     * Local broadcast receivers used for chat states and messages
     */
    String LOCAL_BROADCAST_CHAT_MESSAGE = "local_broadcast_chat_message";
    String LOCAL_BROADCAST_CHAT_STATE = "local_broadcast_chat_state";
    String LOCAL_BROADCAST_MESSAGE_DELIVARY_STATUS = "local_broadcast_message_delivary_status";
    String LOCAL_BROADCAST_CONNECTIVITY_CHANGE = "local_broadcast_connectivity";
    String LOCAL_BROADCAST_LAST_SEEN = "local_broadcast_last_seen";
    String LOCAL_BROADCAST_CHAT_MEDIA_DOWNLOAD = "local_broadcast_chat_media_download";
    String LOCAL_BROADCAST_UPDATE_COUNT = "local_broadcast_update_count";
    String LOCAL_BROADCAST_PRESENCE = "local_broadcast_presence";
    String LOCAL_BROADCAST_GROUP_EMPTY_CREATION = "local_broadcast_group_created";
    String LOCAL_BROADCAST_BLOCK_USER = "local_broadcast_block_user";
    String LOCAL_BROADCAST_GROUP_CREATED_WITH_MEMBERS = "local_broadcast_group_created_with_members";
    String LOCAL_BROADCAST_GROUP_NAME_ICON_UPDATED = "local_broadcast_group_name_icon_updated";
    String LOCAL_BROADCAST_GROUP_MEMBER_CHANGE = "local_broadcast_group_member_change";
    String LOCAL_BROADCAST_DOWNLOAD_PROGRESS = "local_broadcast_download_progress";
    String LOCAL_BROADCAST_CONTACT_SYNC_COMPLETE = "local_broadcast_contact_sync_complete";
    String LOCAL_BROADCAST_CONVERSATION_THEME = "local_broadcast_conversation_theme";
    String LOCAL_BROADCAST_SELF_DESTRUCT_TIMER = "local_broadcast_self_destruct_timer";
    String LOCAL_BROADCAST_PROFILE_DOWNLOADED = "local_broadcast_profile_downloaded";

    /**
     * Message Type constants
     */
    String MESSAGE_TYPE_TEXT = "text";
    String MESSAGE_TYPE_IMAGE = "image";
    String MESSAGE_TYPE_VIDEO = "video";
    String MESSAGE_TYPE_AUDIO = "audio";
    String MESSAGE_TYPE_STICKER = "sticker";
    String MESSAGE_TYPE_LOCATION = "location";
    String MESSAGE_TYPE_CONTACT = "contact";
    String MESSAGE_TYPE_FILE = "file";
    String MESSAGE_TYPE_THEME = "theme";
    String MESSAGE_TYPE_GROUP_STATUS = "status";
    String MESSAGE_TYPE_SELF_DESTRUCT_TIMER = "selfDestructTimer";
    String MESSAGE_TYPE_SCREENSHOT = "screenshot";
    String MESSAGE_TYPE_GROUP_INVITATION = "invitation";
    String MESSAGE_TYPE_CALL = "call";
    String MESSAGE_TYPE_INVITE = "invite";
    String MESSAGE_TYPE_BROADCAST_CREATE = "broadcast_create";

    /**
     * Message Sent Status Constants
     */
    int MESSAGE_STATUS_UNSENT = 0;
    int MESSAGE_STATUS_DELIVERED = 1;
    int MESSAGE_STATUS_DISPLAYED = 2;
    int MESSAGE_STATUS_READ_BY_FRIEND = 3;

    /**
     * Message read Status Constants
     */
    int MESSAGE_STATUS_UNREAD = 1;
    int MESSAGE_STATUS_READ = 0;

    /**
     * Message display type Constants
     */
    int MESSAGE_CHAT = 0;
    int MESSAGE_DATE = 1;
    int MESSAGE_LOAD_MORE = 2;
    /**
     * Chat file upload api params and constants
     */
    String PARAM_OPTION = "option";
    String PARAM_FILE_TYPE = "fileType";
    String PARAM_POST_THUMB = "postThumb";
    String VALUE_UPLOAD_CHAT_FILES = "uploadChatFiles";
    int REQUEST_UPLOAD_FILE_TYPE = 1000;

    /**
     * Request code for activity result
     */
    int GROUP_CREATE_REQUEST = 12354;
    int GROUP_ADD_PARTICPANT = 12456;
    int GROUP_EDIT_NAME = 12556;

    /**
     * file state constants
     */
    int FILE_STATE_IN_PROGRESS = 0;
    int FILE_STATE_ON_DEVICE = 1;
    int FILE_STATE_ON_SERVER = 2;

    /**
     * Values to be used in bundles or intents
     */
    String EXTRA_MESSAGE_ATTACHMENT_URL = "extra_message_attachment_url";
    String EXTRA_CHAT_ITEM_POSITION = "extra_chat_item_position";
    String EXTRA_CHAT_MESSAGE_TYPE = "extra_chat_message_type";
    String EXTRA_CHAT_FILE_NAME = "extra_chat_file_name";
    String EXTRA_CHAT_MESSAGE_ID = "extra_chat_message_id";
    String EXTRA_CHAT_ATTACHMENT_URL = "extra_chat_attachment_url";
    String EXTRA_CHAT_FILE_PATH = "extra_chat_file_path";
    String EXTRA_CHAT_LOCATION = "extra_chat_location";
    String ADD_SER_GROUP = "ADD_GROUP";
    String EXTRA_CHAT_FILE_IS_VIDEO = "extra_chat_file_is_video";
    String EXTRA_FILE_MODE = "extra_file_mode";
    String EXTRA_CHAT_WALLPAPER = "extra_chat_wallpaper";
    String EXTRA_CHAT_WALLPAPER_IS_DRAWABLE = "extra_chat_wallpaper_is_drawable";
    String EXTRA_CHAT_USER_ID = "extra_chat_user_id";
    String EXTRA_CONTACT = "extra_contact";
    String EXTRA_IS_INCOMING = "extra_is_incoming";
    String EXTRA_DOODLE_IMAGE = "extra_doodle_image";
    String EXTRA_CHAT_USER_SHORT_CODE = "extra_chat_user_short_code";
    String EXTRA_GROUP_JID = "extra_group_jid";
    String EXTRA_IS_CONGURATION_SET = "extra_is_conguration_set";
    String EXTRA_GROUP_NAME = "extra_group_name";
    String EXTRA_CHAT_FORWARD_MESSAGES = "extra_chat_forward_messages";
    String EXTRA_CHAT_IS_SAVED_CONTACT = "extra_chat_is_saved_contact";
    String EXTRA_GROUP_SERVER_ID = "extra_group_server_id";
    String EXTRA_GROUP_IMAGE = "extra_group_image";
    // String EXTRA_CHAT_IS_GROUP_CONTACT = "extra_chat_is_group_contact";
    String EXTRA_CHAT_WINDOW_TYPE = "extra_chat_window_type";
    String EXTRA_BLOCK_USER_JID = "extra_block_user_jid";
    String EXTRA_IS_BLOCK = "extra_is_block";
    String EXTRA_CHAT_MEDIA_SELECTED_POSITION = "extra_chat_media_selected_position";
    String EXTRA_IS_GROUP_NAME = "extra_is_group_name";
    String EXTRA_GROUP_NAME_OR_ICON = "extra_group_name_or_icon";
    String EXTRA_SELECTED_IMAGES_TO_SEND = "extra_selected_images_to_send";
    String EXTRA_IS_CONTACT_VIEW_MODE = "extra_is_contact_view_mode";
    String EXTRA_CHAT_FILE_DOWNLOAD_STATUS = "extra_chat_file_download_status";
    String EXTRA_CHAT_DOWNLOAD_PROGRESS_MAP = "extra_chat_download_progress";
    String EXTRA_CHAT_DOWNLOAD_PACKET_ID = "extra_chat_download_packet_id";
    String EXTRA_IMAGE_THUMB_URL_MAP = "extra_image_thumb_url_map";
    String EXTRA_CHAT_PHONE_NUM = "extra_chat_phone_num";

    String EXTRA_PRESENCE_FROM = "extra_presence_from";
    String EXTRA_PRESENCE_MODE = "extra_presence_mode";
    String EXTRA_PRESENCE_DATE = "extra_presence_date";

    String EXTRA_CHAT_THEME_ID = "theme_id";
    // String EXTRA_CHAT_IS_SECRET_CHAT = "extra_chat_is_secret_chat";
    String EXTRA_CHAT_SELF_DESTRUCT_TIMER = "extra_chat_self_destruct_timer";
    String EXTRA_MESSAGE_TIMESTAMP = "extra_message_timestamp";
    String EXTRA_CHAT_IS_SECRET = "extra_chat_is_secret";


    /**
     * Presence modes
     */
    String PRESENCE_MODE_AVAILABLE = "available";
    String PRESENCE_MODE_UNAVAILABLE = "unavailable";
    String PRESENCE_MODE_AWAY = "away";
    String PRESENCE_MODE_ONLINE = "online";

    /**
     * File type constants
     */
    String FILE_TYPE_IMAGE = "1";
    String FILE_TYPE_VIDEO = "2";
    String FILE_TYPE_AUDIO = "3";
    String FILE_TYPE_FILE = "3";

    /**
     * Group status message codes
     */
    String GROUP_STATUS_CREATE = "create";
    String GROUP_STATUS_ADD_PARTICIPANT = "add";
    String GROUP_STATUS_CHANGE_SUBJECT = "groupSubject";
    String GROUP_STATUS_CHANGE_ICON = "groupImage";
    String GROUP_STATUS_REMOVE_MEMBER = "remove";
    String GROUP_STATUS_CREATE_ADMIN = "admin";
    String GROUP_STATUS_LEAVE = "leave";
    String GROUP_STATUS_JOIN = "join";
    String EXTRA_WEARABLE_VOICE_REPLY = "extra_wearable_voice_reply";
    String EXTRA_CHAT_QB_USER_ID = "extra_chat_qb_user_id";


    /**
     * Block user constants
     */
    String BLOCK = "block";
    String UNBLOCK = "unblock";

    int POSITIVE_CONDITION = 1;


//	interface FRIEND_TYPE {
//		int	NORMAL		= 0;
//		int	GROUP		= 1;
//		int	BROADCAST	= 2;
//	}

    interface BlockUser {
        int BLOCK_USER_BY_NONE = 0;
        int BLOCK_USER_BY_ME = 1;
        int BLOCK_USER_BY_OTHER = 2;
        int BLOCK_USER_BY_BOTH = 3;
    }

    interface MessageStatus {
        int NORMAL = 0;
        int EDIT = 1;
        int DELETE = 2;
    }

    interface GROUP_ROLES {
        int NORMAL = 0;
        int ADMIN = 1;
        int OWNER = 2;
    }

    interface ApiConstants {
        int REQUEST_FOURSQUARE_NEARBY_PLACES = 0;
        String URL_USER_LOGIN = obj.quickblox.sample.chat.java.constants.ApiConstants.BASE_URL1 + "login";

    }

    interface Affiliation {
        String NONE = "none";
        String MEMBER = "member";
        String ADMIN = "admin";
        String OWNER = "owner";
    }
}
