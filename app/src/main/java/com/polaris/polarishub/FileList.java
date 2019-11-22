package com.polaris.polarishub;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.polaris.polarishub.Tools.IpManager;
import com.polaris.polarishub.Tools.TdpAdapter;
import com.polaris.polarishub.Tools.TDPitem;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileList extends AppCompatActivity {
	
	private int selectedPosition;
	private File selectedFile;
    private List<TDPitem> items=new ArrayList<TDPitem>();
	transient File[] filelist ;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list);

		filelist = MainActivity.filelist;
        
        TextView test=(TextView)findViewById(R.id.textView2);
        test.setText("Polarishub");
        
        initItems();
        TdpAdapter adapter=new TdpAdapter(FileList.this,R.layout.tdp_item,items,mListener1,mListener2);
        ListView dailyList=(ListView)findViewById(R.id.daily_list);
        dailyList.setAdapter(adapter);
        
        dailyList.setOnItemClickListener(new OnItemClickListener(){
        	@Override
        	public void onItemClick(AdapterView<?> parent,View view,int position,long id){
        		//点击打开单个文件的分享页面
				//获取被选择的元素
				TDPitem item=items.get(position);
				//显示弹窗
				AlertDialog.Builder customizeDialog = new AlertDialog.Builder(FileList.this);
				final View dialogView = LayoutInflater.from(FileList.this).inflate(R.layout.share_dialog,null);
				customizeDialog.setTitle("分享文件");
				customizeDialog.setView(dialogView);
				TextView title;
				ImageView QRcodeImage;
				title = (TextView) dialogView.findViewById(R.id.download_status ) ;
				QRcodeImage = (ImageView) dialogView.findViewById(R.id.qr_for_share) ;
				//从选择的元素获取文件名等数据
				final String titleString = item.getTitle();
				selectedFile = item.getFile();
				String filename = selectedFile.getName();
				//生成二维码
				String Url = "http://"+ IpManager.getIpAddress(FileList.this) +":8080/files/"+filename;
				System.out.println(Url);
				Bitmap qr = MainActivity.createQRcodeImage(Url,1000,1000);
				if(null!=qr){
					QRcodeImage.setImageBitmap(qr);
				}else{
					System.out.println("fail to get qrCodeImage");
					//Toast.makeText(this,"fail to create qrcode",Toast.LENGTH_LONG).show();
					//qrForTest.setImageBitmap(qr);
				}
				//绘制界面
				title.setText(titleString);

				final Button Quit = (Button)dialogView.findViewById(R.id.download_confirm);
				//downloadConfirm.setVisibility(View.GONE) ;
				Quit.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//点击关闭dialogue（未完成）
					}
				});
				customizeDialog.show();
        	}
        });
        dailyList.setOnItemLongClickListener(new OnItemLongClickListener(){
        	@Override
        	public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id){
        		//暂且保留这段代码！！
				//长按进入编辑状态，但是还没想好要编辑啥
        		/*selectedPosition=position;
        		
        		TDPitem item=items.get(position);
        		item.setState(TDPitem.MANAGE);
        		selectedId=item.getItemId();
        		
        		TdpAdapter adapter=new TdpAdapter(FileList.this,R.layout.tdp_item,items,mListener1,mListener2);
                ListView dailyList=(ListView)findViewById(R.id.daily_list);
                dailyList.setAdapter(adapter);

        		 */
                
                return true;
        	}
        });
        
    }
	private TdpAdapter.MyClickListener mListener1 = new TdpAdapter.MyClickListener() {
		@Override
		public void myOnClick(/*int position,*/ View v) {
		        	
		            TDPitem item=items.get(selectedPosition);
	        		item.setState(TDPitem.SHOW);
	        		selectedFile=item.getFile() ;//没什么实际意义
		            
		            TdpAdapter adapter=new TdpAdapter(FileList.this,R.layout.tdp_item,items,mListener1,mListener2);
	                ListView dailyList=(ListView)findViewById(R.id.daily_list);
	                dailyList.setAdapter(adapter);
		         }
		     };
    private TdpAdapter.MyClickListener mListener2 = new TdpAdapter.MyClickListener() {
		         @Override
		       public void myOnClick(View v) {
		        	 //实现按钮 1 的逻辑
		         }
		     };
	private void initItems(){
		items.clear();
		int current = 0;
		while(current<=filelist.length-1){
			System.out.println("start to get filename");
			File currentFile = filelist[current];
			int id=-1;//cursor.getInt(cursor.getColumnIndex("id"));//没有用
			String title=currentFile.getName();
			String detail="detail";
			int imageId=0;
			String time="no time";
			if (imageId==R.drawable.basic_file){
				imageId=0;
			}
			TDPitem item=new TDPitem(currentFile,title,detail,imageId,time,TDPitem.SHOW);
				
			items.add(item);
			current ++;
		}
    }
	


}
