package Utils;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by webwerks on 18/9/15.
 */
public class FileUtils {

    public static File getStorageDir() {
        File file = new File(Environment.getExternalStorageDirectory(),"/HCircle");
        if(!file.isDirectory()) {
            file.mkdir();
        }
        return file;
    }

    public static File getImageFile() {
        String timeStamp   = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File photoFile   = new File(getStorageDir(), "ck_img_"+timeStamp+".jpg");
        return  photoFile;
    }

    public static void deleteAllFile() {
        deleteFiles(getStorageDir());
    }

    private static void deleteFiles(File file) {
        if(file.isDirectory()) {
            for(File fileToRemove : file.listFiles()) {
                deleteFiles(fileToRemove);
            }
            file.delete();
        } else {
            file.delete();
        }
    }

}