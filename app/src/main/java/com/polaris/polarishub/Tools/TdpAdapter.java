package com.polaris.polarishub.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.polaris.polarishub.R;

import java.util.List;

import static android.view.View.GONE;

@SuppressLint("ViewHolder") public class TdpAdapter extends ArrayAdapter<TDPitem> {

	private int resourceId;
	private LinearLayout Manage;
	private View view;
	private TDPitem tdpitem;
	private MyClickListener mListener1;
	private MyClickListener mListener2;
	private Button cancelManage;
	private Button deleteItem;
	public ImageView pic_image;
	private Context Context;
	
	public TdpAdapter(Context context, int textViewResourceId, List<TDPitem> objects, MyClickListener listener1, MyClickListener listener2){
		super(context,textViewResourceId,objects);
		Context=context;
		resourceId=textViewResourceId;
		mListener1 = listener1;
		mListener2 = listener2;

	}
	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		
		tdpitem=getItem(position);
		
		view=LayoutInflater.from(getContext()).inflate(resourceId, null);
		Manage=(LinearLayout)view.findViewById(R.id.manage_item);
		TextView log=(TextView)view.findViewById(R.id.log);
		TextView title=(TextView)view.findViewById(R.id.t_item);
		TextView briefDetail=(TextView)view.findViewById(R.id.d_item);
		pic_image=(ImageView)view.findViewById(R.id.p_item);
		cancelManage=(Button)view.findViewById(R.id.cancel_manage);
		deleteItem=(Button)view.findViewById(R.id.delete_item);
		
		log.setText(tdpitem.getLog());
		log.setVisibility(GONE);
		title.setText(tdpitem.getTitle());
		briefDetail.setText(tdpitem.getBriefDetail());
		setImage(tdpitem.getimageId());
		
		cancelManage.setOnClickListener(mListener1);
		deleteItem.setOnClickListener(mListener2);
		
		if(tdpitem.getState()==TDPitem.SHOW){
			Manage.setVisibility(GONE);
		}else if(tdpitem.getState()==TDPitem.MANAGE){
			Manage.setVisibility(View.VISIBLE);
		}
		
		return view;
    }

	public static abstract class MyClickListener implements OnClickListener {
        
        @Override
        public void onClick(View v) {
            myOnClick(v);
        }
        public abstract void myOnClick(View v);
    }
	public void setImage(int picId){
		pic_image.setImageResource(R.mipmap.basic_file);
	}
}
