package chattingEngine;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nganthoi.salai.tabgen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import connectServer.ConnectAPIs;
import sharePreference.SharedPreference;

/**
 * Created by SALAI on 2/23/2016.
 */
public class FileAdapter extends BaseAdapter{
    private List<String> fileList;
    private Activity context;

    public FileAdapter(List<String> list,Activity _context){
        this.fileList = new ArrayList<String>();
        this.fileList = list;
        this.context = _context;
    }
    @Override
    public int getCount() {
        //return 0;
        return fileList.size();
    }

    @Override
    public String getItem(int position) {
        if(fileList!=null)
        {
            return fileList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileViewHolder holder;
        String file_info = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView = vi.inflate(R.layout.file_display_layout,null);
            holder=createViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (FileViewHolder) convertView.getTag();
        }
        SharedPreference sp = new SharedPreference();
        String ip = sp.getServerIP_Preference(context);
        String token = sp.getTokenPreference(context);
        ConnectAPIs connectAPIs = new ConnectAPIs("http://" + ip + ":8065/api/v1/files/get_info/"+ file_info, token);
        InputStream isr = connectAPIs.getData();
        String fileInfo;
        try {
            //System.out.println("File Information: "+convertInputStreamToString(isr));
            JSONObject jsonfileInfo = new JSONObject(connectAPIs.convertInputStreamToString(isr));
            String file_name = jsonfileInfo.getString("filename");
            int lastOccurance = file_name.lastIndexOf('/');
            String only_filename = file_name.substring(lastOccurance+1);
            fileInfo = "File name: "+only_filename+" \n"+
                    "Type: "+jsonfileInfo.getString("mime")+" \n"+
                    "Size: "+jsonfileInfo.getString("size")+" Bytes\n";
            holder.file_details.setText(fileInfo);
        }catch(JSONException e){
            System.out.println("JSON Exception in FileAdapter: "+e.toString());
            holder.file_details.setText(null);
        }
        return convertView;
    }
    private FileViewHolder createViewHolder(View view){
        FileViewHolder fileViewHolder = new FileViewHolder();
        fileViewHolder.attachmentView = (ImageView) view.findViewById(R.id.attachmentView);
        fileViewHolder.file_details = (TextView) view.findViewById(R.id.fileInformation);
        return fileViewHolder;
    }
    public static class FileViewHolder{
        ImageView attachmentView;
        TextView file_details;
    }

}