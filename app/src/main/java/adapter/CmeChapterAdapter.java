package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nganthoi.salai.tabgen.R;

import java.util.ArrayList;
import java.util.List;

import activity.CmeChapterDetail;
import models.cme_chapter_model.Filename;
import sharePreference.SharedPreference;

/**
 * Created by atul on 28/7/16.
 */
public class CmeChapterAdapter extends ArrayAdapter {


    ArrayList<Filename> emiList;
    Context context;
    String ip,token;
    public CmeChapterAdapter(Context context, int resource,
                             List<Filename> objects) {
        super(context, resource, objects);
        this.context=context;
        this.emiList=(ArrayList<Filename>) objects;
        SharedPreference sp = new SharedPreference();
        ip = sp.getServerIP_Preference(context);
        token = sp.getTokenPreference(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder=null;
        final LayoutInflater inflater = (LayoutInflater) context
                .getApplicationContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            // This a new view we inflate the new layout
            viewHolder=new ViewHolder();

            convertView = inflater.inflate(R.layout.cme_chapter_item,
                    parent, false);
            viewHolder.txtChapterItem=(TextView)convertView.findViewById(R.id.txtChapterItem);
//            viewHolder.txtMonths=(TextView)convertView.findViewById(R.id.txtMonths);
//            viewHolder.txtInterest=(TextView)convertView.findViewById(R.id.txtInterest);
//            viewHolder.txtInterestAmount=(TextView)convertView.findViewById(R.id.txtInterestAmount);
//            viewHolder.txtProcessing=(TextView)convertView.findViewById(R.id.txtProcessing);
//            viewHolder.txtEmi=(TextView)convertView.findViewById(R.id.txtEmi);
//            viewHolder.txtTrAmount=(TextView)convertView.findViewById(R.id.txtTrAmount);
//            viewHolder.txtTotalAmount=(TextView)convertView.findViewById(R.id.txtTotalAmount);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtChapterItem.setText(emiList.get(position).getFileName()+"");
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, CmeChapterDetail.class);
                intent.putExtra("FILE_TYPE",""+emiList.get(position).getFileType());
                intent.putExtra("ATTATCHMENT_URL",""+emiList.get(position).getAttachmentUrl());
                intent.putExtra("TOKEN",""+token);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
    static class ViewHolder {

        TextView txtChapterItem;
    }


}



