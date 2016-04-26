package photoselect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nganthoi.salai.tabgen.R;


public class MainActivity extends Activity {

	private static final int IMAGE_PATH_CUSTOMGALLAERY = 56;
	String picturePathh = null;
	private Bitmap bm;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoselect);
        Button btn=(Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent in = new Intent(MainActivity.this,
//						MultiPhotoSelectActivity.class);
//				startActivityForResult(in, IMAGE_PATH_CUSTOMGALLAERY);
			}
		});
    }

   
    
    protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( requestCode == IMAGE_PATH_CUSTOMGALLAERY) {
			switch (requestCode) {

			 case IMAGE_PATH_CUSTOMGALLAERY:
				Log.e("I am getting in","Here it is");
				picturePathh = data.getStringExtra("PATHOFIMG");
				if(picturePathh!=null){
				File fileName = new File(picturePathh);
				bm = decodeFile(fileName);
				bm = imageOreintationValidator(bm,
						picturePathh);
			
				}
				
		
		}

	}
    }
    
    private Bitmap decodeFile(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 400;

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}
    
    private Bitmap imageOreintationValidator(Bitmap bitmap, String path) {

		ExifInterface ei;
		try {
			ei = new ExifInterface(path);
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				bitmap = rotateImage(bitmap, 90);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				bitmap = rotateImage(bitmap, 180);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				bitmap = rotateImage(bitmap, 270);
				break;
			}
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT)
			.show();
			e.printStackTrace();
		}

		return bitmap;
	}
    
    private Bitmap rotateImage(Bitmap source, float angle) {

		Bitmap bitmap = null;
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		try {
			bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
					source.getHeight(), matrix, true);
		} catch (OutOfMemoryError err) {
			Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT)
			.show();
			err.printStackTrace();
		}
		return bitmap;
	}
}
