package adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nganthoi.salai.tabgen.BuildConfig;
import com.nganthoi.salai.tabgen.R;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import models.cmearticlemodel.Output;
import sharePreference.SharedPreference;

public class FlipAdapter extends BaseAdapter implements OnClickListener {

	public interface Callback{
		public void onPageRequested(int page);
	}

   String ip,token;
	private LayoutInflater inflater;
	private Callback callback;
	Context context;
	private List<Output> items = new ArrayList<Output>();
	
	public FlipAdapter(Context context,List<Output> items) {
		inflater = LayoutInflater.from(context);
		this.items=items;
		this.context=context;
		SharedPreference sp = new SharedPreference();
		ip = sp.getServerIP_Preference(context);
		token = sp.getTokenPreference(context);
//		for(int i = 0 ; i<10 ; i++){
//			items.add(new Item());
//		}
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
//		items.get(position).getId()
		return 0;
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.flip_item, parent, false);
			holder.txtHeader = (TextView) convertView.findViewById(R.id.txtHeader);
			holder.imgContent=(ImageView)convertView.findViewById(R.id.imgContent);
			holder.txtDetails=(TextView) convertView.findViewById(R.id.txtDetails);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		//TODO set a text with the id as well
		holder.txtHeader.setText(items.get(position).getName()+"");
		holder.txtDetails.setText(items.get(position).getTextualContent()+"");
//		Log.v("VALUE","VALUE::")
		OkHttpClient picassoClient1 = new OkHttpClient();
		picassoClient1.networkInterceptors().add(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Request newRequest = chain.request().newBuilder()
						.addHeader("Content-Type", "application/json")
						.addHeader("Accept", "application/json")
						.addHeader("Authorization", "Bearer " + token)
						.build();
				return chain.proceed(newRequest);
			}
		});

		new Picasso.Builder(context).loggingEnabled(BuildConfig.DEBUG)
				.downloader(new OkHttpDownloader(picassoClient1)).build()
				.load("http://"+ip+"/TabGenAdmin/"+items.get(position).getImages())
				.error(R.drawable.username)
				.into(holder.imgContent);

		return convertView;
	}

	static class ViewHolder{
		TextView txtHeader,txtDetails;
		ImageView imgContent,imgFirst,imgSecond;
		Button firstPage;
		Button lastPage;
	}

	@Override
	public void onClick(View v) {

	}


}
