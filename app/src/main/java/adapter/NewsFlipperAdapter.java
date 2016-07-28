package adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import activity.NewsDetailWebviewActivity;
import activity.ViewFlipperActivity;
import models.cmearticlemodel.Output;
import sharePreference.SharedPreference;

/**
 * Created by atul on 22/7/16.
 */
public class NewsFlipperAdapter extends BaseAdapter implements View.OnClickListener {

    public interface NewsCallback{
        public void onPageRequested(int page);
    }

    String ip,token;
    private LayoutInflater inflater;
    private NewsCallback callback;
    Context context;
    private List<models.news_model.Response> items = new ArrayList<models.news_model.Response>();

    public NewsFlipperAdapter(Context context,List<models.news_model.Response> items) {
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items=items;
        this.context=context;
        SharedPreference sp = new SharedPreference();
        ip = sp.getServerIP_Preference(context);
        token = sp.getTokenPreference(context);
    }

    public void setCallback(NewsCallback callback) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
//        try {
            if (convertView == null) {
                holder = new ViewHolder();
                switch (items.get(position).getItemCount()) {
                    case 1:
                        Log.v("ITEMS", "POSITION :" + items.get(position).getItemCount());
                        convertView = inflater.inflate(R.layout.news_item3, null);
                        //TODO set a text with the id as well
                        holder.imgContentSingle = (ImageView) convertView.findViewById(R.id.imgContentSingle);
                        holder.txtHeaderSingle = (TextView) convertView.findViewById(R.id.txtHeaderSingle);
                        holder.txtHeadlinesSingle = (TextView) convertView.findViewById(R.id.txtHeadlinesSingle);

                        break;
                    case 2:
                        Log.v("ITEMS", "POSITION :" + items.get(position).getItemCount());
                        convertView = inflater.inflate(R.layout.news_item1, null);
                        //TODO set a text with the id as well
                        holder.imgContent = (ImageView) convertView.findViewById(R.id.imgContent);
                        holder.txtHeader = (TextView) convertView.findViewById(R.id.txtHeader);
                        holder.txtHeadlines = (TextView) convertView.findViewById(R.id.txtHeadlines);
                        holder.imgContent1 = (ImageView) convertView.findViewById(R.id.imgContent1);
                        holder.txtHeader1 = (TextView) convertView.findViewById(R.id.txtHeader1);
                        holder.txtHeadlines1 = (TextView) convertView.findViewById(R.id.txtHeadlines1);
                        break;
                    case 3:
                        Log.v("ITEMS", "POSITION :" + items.get(position).getItemCount());
                        convertView = inflater.inflate(R.layout.news_item2, null);
                        //TODO set a text with the id as well
                        holder.imgContent2 = (ImageView) convertView.findViewById(R.id.imgContent2);
                        holder.txtHeader2 = (TextView) convertView.findViewById(R.id.txtHeader2);
                        holder.txtHeadlines2 = (TextView) convertView.findViewById(R.id.txtHeadlines2);
                        holder.imgContent3 = (ImageView) convertView.findViewById(R.id.imgContent3);
                        holder.txtHeader3 = (TextView) convertView.findViewById(R.id.txtHeader3);
                        holder.txtHeadlines3 = (TextView) convertView.findViewById(R.id.txtHeadlines3);
                        holder.imgContent4 = (ImageView) convertView.findViewById(R.id.imgContent4);
                        holder.txtHeader4 = (TextView) convertView.findViewById(R.id.txtHeader4);
                        holder.txtHeadlines4 = (TextView) convertView.findViewById(R.id.txtHeadlines4);
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
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
            switch (items.get(position).getItemCount()) {
                case 1:
                    holder.txtHeaderSingle.setText(items.get(position).getItems().get(0).getTitle()+"");
                    holder.txtHeadlinesSingle.setText(items.get(position).getItems().get(0).getHeadline()+"");
                    try {
                        new Picasso.Builder(context).loggingEnabled(BuildConfig.DEBUG)
                                .downloader(new OkHttpDownloader(picassoClient1)).build()
                                .load("" + items.get(position).getItems().get(0).getImageUrl())
                                .fit()
                                .error(R.drawable.username)
                                .into(holder.imgContentSingle);
                    }catch (Exception e){

                    }
                    holder.imgContentSingle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            callIntent(items.get(position).getItems().get(0).getDetails() + "", items.get(position).getItems().get(0).getId() + "", context);
                        }
                    });
                    break;
                case 2:
                    holder.txtHeader.setText(items.get(position).getItems().get(0).getTitle() + "");
                    holder.txtHeadlines.setText(items.get(position).getItems().get(0).getHeadline() + "");
                    try {
                        new Picasso.Builder(context).loggingEnabled(BuildConfig.DEBUG)
                                .downloader(new OkHttpDownloader(picassoClient1)).build()
                                .load("" + items.get(position).getItems().get(0).getImageUrl())
                                .fit()
                                .error(R.drawable.username)
                                .into(holder.imgContent);
                    }catch (Exception e){

                    }
                    holder.imgContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            callIntent(items.get(position).getItems().get(0).getDetails() + "", items.get(position).getItems().get(0).getId() + "", context);
                        }
                    });

                    holder.txtHeader1.setText(items.get(position).getItems().get(1).getHeadline() + "");
                    holder.txtHeadlines1.setText(items.get(position).getItems().get(1).getHeadline() + "");
                    try {
                    new Picasso.Builder(context).loggingEnabled(BuildConfig.DEBUG)
                            .downloader(new OkHttpDownloader(picassoClient1)).build()
                            .load("" + items.get(position).getItems().get(1).getImageUrl())
                            .fit()
                            .error(R.drawable.username)
                            .into(holder.imgContent1);
                    }catch (Exception e){

                    }
                    holder.imgContent1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            callIntent(items.get(position).getItems().get(1).getDetails() + "", items.get(position).getItems().get(1).getId() + "", context);
                        }
                    });
                    break;
                case 3:
                    holder.txtHeader2.setText(items.get(position).getItems().get(0).getTitle() + "");
                    holder.txtHeadlines2.setText(items.get(position).getItems().get(0).getHeadline() + "");
                    try{
                    new Picasso.Builder(context).loggingEnabled(BuildConfig.DEBUG)
                            .downloader(new OkHttpDownloader(picassoClient1)).build()
                            .load("" + items.get(position).getItems().get(0).getImageUrl())
                            .fit()
                            .error(R.drawable.username)
                            .into(holder.imgContent2);
            }catch (Exception e){

        }
                    holder.imgContent2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            callIntent(items.get(position).getItems().get(0).getDetails() + "", items.get(position).getItems().get(0).getId() + "", context);
                        }
                    });

                    holder.txtHeader3.setText(items.get(position).getItems().get(1).getTitle() + "");
                    holder.txtHeadlines3.setText(items.get(position).getItems().get(1).getHeadline() + "");
                   try{
                    new Picasso.Builder(context).loggingEnabled(BuildConfig.DEBUG)
                            .downloader(new OkHttpDownloader(picassoClient1)).build()
                            .load("" + items.get(position).getItems().get(1).getImageUrl())
                            .fit()
                            .error(R.drawable.username)
                            .into(holder.imgContent3);
                   }catch (Exception e){

                   }
                    holder.imgContent3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            callIntent(items.get(position).getItems().get(1).getDetails() + "", items.get(position).getItems().get(1).getId() + "", context);
                        }
                    });

                    holder.txtHeader4.setText(items.get(position).getItems().get(2).getTitle() + "");
                    holder.txtHeadlines4.setText(items.get(position).getItems().get(2).getHeadline() + "");
                    try{
                    new Picasso.Builder(context).loggingEnabled(BuildConfig.DEBUG)

                            .downloader(new OkHttpDownloader(picassoClient1)).build()
                            .load("" + items.get(position).getItems().get(2).getImageUrl())
                            .fit()
                            .error(R.drawable.username)
                            .into(holder.imgContent4);
                    }catch (Exception e){

                    }
                    holder.imgContent4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            callIntent(items.get(position).getItems().get(2).getDetails() + "", items.get(position).getItems().get(2).getId() + "", context);
                        }
                    });
                    break;
            }

            return convertView;
//        }catch (Exception e){
//            return null;
//        }
    }

    public static class ViewHolder{
       ImageView imgContent,imgContent1,imgContent2,imgContent3,imgContent4,imgContentSingle;
        TextView txtHeader,txtHeadlines,txtHeader1,txtHeadlines1,txtHeader2,
                txtHeadlines2,txtHeader3,txtHeadlines3,txtHeader4,txtHeadlines4,txtHeaderSingle,txtHeadlinesSingle;
    }

    public void callIntent(String url,String news_id,Context context){
        Intent intent=new Intent(context, NewsDetailWebviewActivity.class);
        intent.putExtra("WEB_URL",url+"");
        intent.putExtra("NEWS_ID",news_id+"");
        context.startActivity(intent);
    }
    @Override
    public void onClick(View v) {

    }


}
