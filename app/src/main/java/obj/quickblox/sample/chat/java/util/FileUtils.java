package obj.quickblox.sample.chat.java.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.jivesoftware.smack.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class FileUtils {

    public static final String bitmapExtension = ".png";
    /**
     * Constant for image loader directory
     */
    public static final String APP_FILE_DIRECTORY = "/TuDime";
    // public static final String IMAGE_CACHE_DIRECTORY_STRUCTURE =
    // "MyCityMedia/myCity";
    public static final String APP_VIDEOS_DIRECTORY = APP_FILE_DIRECTORY + "/videos";
    public static final String APP_IMAGE_DIRECTORY = APP_FILE_DIRECTORY + "/captured";
    public static final String APP_VOICE_DIRECTORY = APP_FILE_DIRECTORY + "/voice";
    public static final String APP_OTHER_FILES_DIRECTORY = APP_FILE_DIRECTORY + "/File";
    public static final String FILE_SENT_DIRECTORY = APP_FILE_DIRECTORY + "/sent";
    public static final String SENT_DIRECTORY_IMAGE = FILE_SENT_DIRECTORY + "/Images";
    public static final String APP_DIRECTORY_CHATS_EMAIL = APP_FILE_DIRECTORY + "/Notes";
    public static final String SENT_DIRECTORY_FILE = FILE_SENT_DIRECTORY + "/File";
    public static final String SENT_DIRECTORY_VIDEO = FILE_SENT_DIRECTORY + "/Video";
    public static final String SENT_DIRECTORY_VOICE = FILE_SENT_DIRECTORY + "/Voice";
    public static final String DOODLE_DIRECTORY = APP_FILE_DIRECTORY + "/Doodle";
    public static final String WALLPAPER_DIRECTORY = APP_FILE_DIRECTORY + "/Wallpaper";
    public static final String APP_PROFILE_DIRECTORY = APP_FILE_DIRECTORY + "/profile photos";

    public static String saveImage(Context context, Bitmap finalBitmap, String bitmapName) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + APP_IMAGE_DIRECTORY);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        File file = new File(myDir, bitmapName);
        Log.e("saved image name : ", bitmapName);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String createChatTextFile(String pFileName, String pChats) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), APP_DIRECTORY_CHATS_EMAIL);
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, pFileName + ".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(pChats);
            writer.flush();
            writer.close();
            return gpxfile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Bitmap getBitmapByName(String bitmapName) {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            Log.e("fetched image name : ", bitmapName + bitmapExtension);
            File f = new File(root + APP_IMAGE_DIRECTORY + "/" + bitmapName + bitmapExtension);
            return BitmapFactory.decodeFile(f.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Bitmap getProfilePhotoByChatUserName(String chatUsername, boolean isGroup) {
        String fileName = chatUsername;
        if (chatUsername.contains("_"))
            fileName = new UserInfo(chatUsername).setAsGroup(isGroup).getPhoneNum();
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            Log.e("fetched image name : ", fileName + bitmapExtension);
            File f = new File(root + APP_PROFILE_DIRECTORY + "/" + fileName + bitmapExtension);
            if (!f.exists()) {
                return null;
            }
            return BitmapUtils.getScaledBitmap(f.getAbsoluteFile(), 1000);
//			return BitmapFactory.decodeFile(f.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void deleteProfilePhotoByChatUserName(String chatUsername, boolean isGroup) {
//        String fileName = chatUsername.split("_")[1];
        String fileName = new UserInfo(chatUsername).getPhoneNum();
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + APP_PROFILE_DIRECTORY);
        if (myDir.exists()) {
            File file = new File(myDir, fileName + bitmapExtension);
            if (file.exists())
                file.delete();
        }
    }

    public static void deleteImage(String bitmapName) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + APP_IMAGE_DIRECTORY);
        if (myDir.exists()) {
            File file = new File(myDir, bitmapName + bitmapExtension);
            if (file.exists())
                file.delete();
        }
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean copyFile(String from, String to) {
        try {
//			if (from.equals(to)){
//				return true;
//			}
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                int end = from.toString().lastIndexOf("/");
                String str1 = from.toString().substring(0, end);
                String str2 = from.toString().substring(end + 1, from.length());
                File source = new File(str1, str2);
                File destination = new File(to, str2);
                if (destination.exists()){
                    Log.i("FileUtils", " File already exist");
                    return true;
                }
                if (source.exists()) {
                    FileInputStream fileInputStream = new FileInputStream(source);
                    FileOutputStream fileOutputStream = new FileOutputStream(destination);
                    FileChannel src = fileInputStream.getChannel();
                    FileChannel dst = fileOutputStream.getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    fileInputStream.close();
                    fileOutputStream.close();
                    Log.i("Fileutils", "File copied successfully");
                    return true;
                }
            }
            Log.i("Fileutils", "File copying failed");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Fileutils", "File copying failed");
            return false;
        }
    }

    public static void createMediaDirectoriesIfNotExist() {
        createDirectoryIfNotExist(SENT_DIRECTORY_IMAGE, SENT_DIRECTORY_VIDEO, SENT_DIRECTORY_VOICE,
                APP_VIDEOS_DIRECTORY, APP_IMAGE_DIRECTORY, APP_VOICE_DIRECTORY, DOODLE_DIRECTORY,
                WALLPAPER_DIRECTORY, SENT_DIRECTORY_FILE, APP_OTHER_FILES_DIRECTORY, APP_PROFILE_DIRECTORY);
    }

    public static void createDirectoryIfNotExist(String... requiredDirectory) {
        for (int i = 0; i < requiredDirectory.length; i++) {
            File file = new File(Environment.getExternalStorageDirectory() + requiredDirectory[i]);
            if (!file.exists()) {
                file.mkdirs();
                System.out.println(file.getAbsolutePath() + " file created");
            }
            System.out.println(file.getAbsolutePath() + " file outer");
        }
    }

    public static void walkdir(File dir) {

        String pdfPattern = ".pdf";

        File[] listFile = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkdir(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(pdfPattern)) {
                        System.out.println(listFile[i].getName());
                        // Do what ever u want
                    }
                }
            }
        }
    }

    public static String getRelevantFileSize(String byteString) {
        String fileSizeString = "";
        if (StringUtils.isNullOrEmpty(byteString)) {
            return fileSizeString;
        }
        double bytes = Double.parseDouble(byteString);
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        if (bytes >= 1 && bytes < 1024) {
            fileSizeString = (int) (Math.floor(bytes)) + " B";
        } else if (kilobytes >= 1 && kilobytes < 1024) {
            fileSizeString = (int) (Math.floor(kilobytes)) + " KB";
        } else if (megabytes >= 1 && megabytes < 1024) {
            fileSizeString = (int) (Math.floor(megabytes)) + " MB";
        }
        return fileSizeString;
    }

    public static File writeBitmapToCache(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        // create a file to write bitmap data
        File f = new File(context.getCacheDir().getAbsolutePath(), "profileImage.jpeg");

        // Convert bitmap to byte array
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

//    public static void deleteLoggerFile() {
//        File logFile = new File(Environment.getExternalStorageDirectory().toString() + "/log.txt");
//        if (logFile.exists()) {
//            logFile.delete();
//        }
//    }
//
//    public static void appendLog(String text) {
//        String root = Environment.getExternalStorageDirectory().toString();
//
//        File logFile = new File(root + "/log.txt");
//        if (!logFile.exists()) {
//            try {
//                logFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            //BufferedWriter for performance, true to set append to file flag
//            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
//            buf.append(text);
//            buf.newLine();
//            buf.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
}
