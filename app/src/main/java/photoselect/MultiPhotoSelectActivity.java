//package photoselect;
//
//import java.io.File;
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.text.Html;
//import android.util.SparseBooleanArray;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.BaseAdapter;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
//
//
//
//
//public class MultiPhotoSelectActivity extends BaseActivity {
//
//	private ArrayList<String> imageUrls;
//	private DisplayImageOptions options;
//	private ImageAdapter imageAdapter;
//	private FolderAdapter folderAdapter;
//	private String single_image_path;
//
//	private GridView gridView;
//	private ArrayList<String> folderNames;
//	private int value=0;
//
//	private ArrayList<String> pathName;
//
//	private ScaleImageView single_imageView;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.ac_image_grid);
//		folderNames=new ArrayList<String>();
//		pathName=new ArrayList<String>();
//
//
//		single_imageView=(ScaleImageView) findViewById(R.id.single_image_layout);
//		single_imageView.setVisibility(View.GONE);
//
//		final String[] columns = { MediaStore.Images.Media.DATA,
//				MediaStore.Images.Media._ID };
//		final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
//		Cursor imagecursor = managedQuery(
//				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
//				null, orderBy + " DESC");
//
//		this.imageUrls = new ArrayList<String>();
//
//		for (int i = 0; i < imagecursor.getCount(); i++) {
//			imagecursor.moveToPosition(i);
//			int dataColumnIndex = imagecursor
//					.getColumnIndex(MediaStore.Images.Media.DATA);
//			String path=imagecursor.getString(dataColumnIndex);
//
//			String folderName=path.substring(0,path.lastIndexOf("/"));
//
//			if(!folderNames.contains(folderName.trim())){
//				folderNames.add(folderName.trim());
//				pathName.add(path);
//			}
//
//
//
//
//			imageUrls.add(imagecursor.getString(dataColumnIndex));
//
//
//		}
//
//		options = new DisplayImageOptions.Builder()
//		.showStubImage(R.drawable.zonefoldernew)
//		.showImageForEmptyUri(R.drawable.zoned_app_icon)
//		.cacheInMemory().cacheOnDisc().build();
//
//		folderAdapter=new FolderAdapter(getApplicationContext(), pathName);
//
//
//
//
//		gridView = (GridView) findViewById(R.id.gridview);
//		gridView.setAdapter(folderAdapter);
//
//		gridView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				value=1;
//				String path=folderNames.get(position);
//				imageAdapter = new ImageAdapter(getApplicationContext(), imageUrls, path);
//				gridView.setAdapter(imageAdapter);
//
//
//
//
//
//			}
//		});
//
//
//	}
//
//
//
//
//	@Override
//	protected void onStop() {
//		imageLoader.stop();
//		super.onStop();
//	}
//
//
//
//	@Override
//	public void onBackPressed()
//	{
//		if (value == 0)
//		{
//			super.onBackPressed();
//			Intent it= new Intent();
//			it.putExtra("imagepath", "single_image_path");
//
//			setResult(RESULT_OK, it);
//
//			return;
//		}
//		if (value == 1)
//		{
//			value = 0;
//			folderAdapter = new FolderAdapter(getApplicationContext(), pathName);
//			gridView.setAdapter(folderAdapter);
//			return;
//		} else
//		{
//			value = 1;
//			gridView.setVisibility(View.VISIBLE);
//			single_imageView.setVisibility(View.GONE);
//			return;
//		}
//
//	}
//
//
//	public class ImageAdapter extends BaseAdapter {
//
//		ArrayList<String> mList;
//		LayoutInflater mInflater;
//		Context mContext;
//		SparseBooleanArray mSparseBooleanArray;
//		String imagePath;
//
//		public ImageAdapter(Context context, ArrayList<String> imageList, String path) {
//			// TODO Auto-generated constructor stub
//			mContext = context;
//			mInflater = LayoutInflater.from(mContext);
//			mSparseBooleanArray = new SparseBooleanArray();
//			mList = new ArrayList<String>();
//			mList.clear();
//
//			this.imagePath=path;
//
//			for(int i=0;i<imageUrls.size();i++){
//				if(imageUrls.get(i).contains(imagePath)){
//					mList.add(imageUrls.get(i));
//				}
//			}
//
//		}
//
//		public ArrayList<String> getCheckedItems() {
//			ArrayList<String> mTempArry = new ArrayList<String>();
//
//			for (int i = 0; i < mList.size(); i++) {
//				if (mSparseBooleanArray.get(i)) {
//					mTempArry.add(mList.get(i));
//				}
//			}
//
//			return mTempArry;
//		}
//
//		@Override
//		public int getCount() {
//			return mList.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(final int position, View convertView, ViewGroup parent) {
//
//			if (convertView == null) {
//				convertView = mInflater.inflate(R.layout.row_multiphoto_item,
//						null);
//			}
//
//
//			CheckBox mCheckBox = (CheckBox) convertView
//					.findViewById(R.id.checkBox1);
//			final ImageView imageView = (ImageView) convertView
//					.findViewById(R.id.imageView1);
//
//
//			imageView.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//
//					gridView.setVisibility(View.GONE);
//					single_imageView.setVisibility(View.VISIBLE);
//					value = 2;
//					single_image_path = (String)mList.get(position);
//
//
//
//
//
//
//
//
//
//					Intent intent=new Intent();
//					intent.putExtra("PATHOFIMG",single_image_path);
//					setResult(RESULT_OK ,intent);
//					finishWithResult();
//
//
//					single_imageView.setImageBitmap(MultiPhotoSelectActivity.decodeSampledBitmapFromResource((String)mList.get(position), 1000, 1000));
//
//				}
//			});
//
//
//
//
//
//
//
//
//
//
//			imageLoader.displayImage("file://" + mList.get(position),
//					imageView, options, new SimpleImageLoadingListener(){
//
//				public void onLoadingComplete(String imageUri,
//						View view, Bitmap loadedImage) {
//					// TODO Auto-generated method stub
//					Animation anim = AnimationUtils.loadAnimation(MultiPhotoSelectActivity.this, R.anim.fade_in);
//					imageView.setAnimation(anim);
//					anim.start();
//				}
//			});
//
//			mCheckBox.setTag(position);
//			mCheckBox.setChecked(mSparseBooleanArray.get(position));
//			mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
//
//
//			return convertView;
//		}
//
//		OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				// TODO Auto-generated method stub
//				mSparseBooleanArray.put((Integer) buttonView.getTag(),
//						isChecked);
//			}
//		};
//	}
//
//
//
//	private void finishWithResult()
//	{
//		Bundle conData = new Bundle();
//		conData.putString("PATHOFIMG", single_image_path);
//
//
//		Intent intent = new Intent();
//		intent.putExtras(conData);
//		setResult(RESULT_OK, intent);
//		finish();
//	}
//
//	public class FolderAdapter extends BaseAdapter {
//
//		ArrayList<String> mList;
//		LayoutInflater mInflater;
//		Context mContext;
//		SparseBooleanArray mSparseBooleanArray;
//		TextView folderHeading;
//
//		public FolderAdapter(Context context, ArrayList<String> imageList) {
//			// TODO Auto-generated constructor stub
//			mContext = context;
//			mInflater = LayoutInflater.from(mContext);
//			mSparseBooleanArray = new SparseBooleanArray();
//			mList = new ArrayList<String>();
//			this.mList = imageList;
//
//		}
//
//
//
//		@Override
//		public int getCount() {
//			return mList.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//
//			if (convertView == null) {
//				convertView = mInflater.inflate(R.layout.folderrow,
//						null);
//			}
//
//			folderHeading= (TextView) convertView.findViewById(R.id.textviewhading);
//			final ImageView imageView = (ImageView) convertView
//					.findViewById(R.id.imageView1);
//
//			String [] name=mList.get(position).split("/");
//
//
//			folderHeading.setText(name[name.length-2]);
//			imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
//
//			imageView.setImageResource(R.drawable.zonefoldernew);
//
//				imageLoader.displayImage("file://" + mList.get(position),
//			imageView, options, new SimpleImageLoadingListener(){
//
//		public void onLoadingComplete(String imageUri,
//				View view, Bitmap loadedImage) {
//			// TODO Auto-generated method stub
//			Animation anim = AnimationUtils.loadAnimation(MultiPhotoSelectActivity.this, R.anim.fade_in);
//			imageView.setAnimation(anim);
//			anim.start();
//		}
//	});
//
//
//
//
//			return convertView;
//		}
//
//	}
//
//
//
//
//	public static Bitmap decodeSampledBitmapFromResource(String s, int i, int j)
//	{
//		BitmapFactory.Options options1 = new BitmapFactory.Options();
//		options1.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(s, options1);
//		options1.inSampleSize = calculateInSampleSize(options1, i, j);
//		options1.inJustDecodeBounds = false;
//		return BitmapFactory.decodeFile(s, options1);
//	}
//
//
//	public static int calculateInSampleSize(
//			BitmapFactory.Options options, int reqWidth, int reqHeight) {
//		// Raw height and width of image
//		final int height = options.outHeight;
//		final int width = options.outWidth;
//		int inSampleSize = 1;
//
//		if (height > reqHeight || width > reqWidth) {
//
//			final int halfHeight = height / 2;
//			final int halfWidth = width / 2;
//
//			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
//			// height and width larger than the requested height and width.
//			while ((halfHeight / inSampleSize) > reqHeight
//					&& (halfWidth / inSampleSize) > reqWidth) {
//				inSampleSize *= 2;
//			}
//		}
//
//		return inSampleSize;
//	}
//
//
//	private void shareImage()
//	{
//		if (value == 0)
//		{
//			Toast.makeText(getApplicationContext(), "Please select image first to share", 1).show();
//
//		} else{ArrayList<String> selectedItems = imageAdapter.getCheckedItems();
//		ArrayList<Uri> files = new ArrayList<Uri>();
//		files.clear();
//		if (value == 2)
//		{
//			selectedItems.clear();
//			selectedItems.add(single_image_path);
//		}
//
//
//
//
//		if(selectedItems.size()>0){
//			Toast.makeText(MultiPhotoSelectActivity.this,
//					"Total photos selected: " + selectedItems.size(),
//					Toast.LENGTH_SHORT).show();
//
//
//
//
//
//			for(String imagespath : selectedItems /* List of the files you want to send */) {
//				File file = new File(imagespath);
//				Uri uri = Uri.fromFile(file);
//				files.add(uri);
//			}
//
//			Intent share = new Intent(Intent.ACTION_SEND);
//			share.setType("image/*");
//			share.putExtra(Intent.EXTRA_TEXT,
//					Html.fromHtml("<p>Share by B4u Snap .</p>"));
//			share.setAction(Intent.ACTION_SEND_MULTIPLE);
//			share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
//
//			share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//			startActivity(Intent.createChooser(share, "Share Image!"));
//
//
//		} else{
//			Toast.makeText(MultiPhotoSelectActivity.this,
//					"You have not selected any pictures",
//					Toast.LENGTH_SHORT).show();
//		}
//
//
//		}
//
//	}
//
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//
//		default:
//			break;
//
//		}
//		return true;
//	}
//
//
//
//}