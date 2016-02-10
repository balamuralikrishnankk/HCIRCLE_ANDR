package readData;

/**
 * Created by SALAI on 2/8/2016.
 */
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
public class ReadFile {
    public String getFilePath(Uri uri,Context context){
        try {
            if (uri == null)
                return null;
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            return uri.getPath();
        }catch (Exception e){
            System.out.println("File Selection Exception: "+e.toString());
            return null;
        }
    }
}
