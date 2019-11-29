package com.polaris.polarishub;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
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

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
		getSupportActionBar().hide();
		getWindow().setStatusBarColor(0xFF000000);

		//filelist = MainActivity.filelist;
        
        TextView test=(TextView)findViewById(R.id.textView2);
        test.setText("Polarishub");
        
        initItems();
        TdpAdapter adapter=new TdpAdapter(FileList.this,R.layout.tdp_item,items,mListener1,mListener2);

        Button selectFile = (Button)findViewById(R.id.select_file_butt );
        selectFile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("*/*");//无类型限制
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				startActivityForResult(intent, 1);
			}
		}) ;


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
				final String titleString = item.getFile().getName();
				selectedFile = item.getFile();
				String filename = selectedFile.getName();
				//生成二维码
				final String Url = "http://"+ IpManager.getIpAddress(FileList.this) +":8080/files/"+filename;
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

				final Button copyUri = (Button)dialogView.findViewById(R.id.copy_uri_butt);
				//downloadConfirm.setVisibility(View.GONE) ;
				copyUri.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						System.out.println("try to copy");
						ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);// 创建普通字符型ClipData
						ClipData mClipData = ClipData.newPlainText("PolarisHub file Url", Url);// 将ClipData内容放到系统剪贴板里。
						cm.setPrimaryClip(mClipData);
						Toast.makeText(FileList.this,"已复制到剪贴板,\n电脑端可粘贴uri至浏览器自动下载",Toast.LENGTH_LONG).show();
					}
				});
				customizeDialog.show();
        	}
        });
        dailyList.setOnItemLongClickListener(new OnItemLongClickListener(){
        	@Override
        	public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id){
        		selectedPosition=position;
        		
        		TDPitem item=items.get(position);
        		item.setState(TDPitem.MANAGE);
        		selectedFile=item.getFile();
        		
        		TdpAdapter adapter=new TdpAdapter(FileList.this,R.layout.tdp_item,items,mListener1,mListener2);
                ListView dailyList=(ListView)findViewById(R.id.daily_list);
                dailyList.setAdapter(adapter);


                return true;
        	}
        });
        
    }
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			try{
				String originPath;
				String fileName;
				Uri uri = data.getData();
				System.out.println(uri);
				originPath = getPath(this, uri);
				fileName = originPath.substring(originPath.lastIndexOf("/") + 1);
				Toast.makeText(this,"已将"+fileName+"拷贝至同步文件夹",Toast.LENGTH_LONG).show();
				File originFile = new File(originPath);
				File polarisHubFolder =new File(Environment.getExternalStorageDirectory(),"PolarisHub");
				while(!polarisHubFolder.exists()){
					polarisHubFolder.mkdir();
					//if(com.des.butler.MainActivity.DeveloperState==true){Toast.makeText(ItemEdit.this, "����Butler��"+Environment.getExternalStorageDirectory(), Toast.LENGTH_SHORT).show();}
				}
				FileInputStream fileInputStream = new FileInputStream(originFile);
				FileOutputStream fileOutputStream = new FileOutputStream(polarisHubFolder+"/"+fileName);
				byte[] buffer = new byte[1024];
				int byteRead;
				while (-1 != (byteRead = fileInputStream.read(buffer))) {
					fileOutputStream.write(buffer, 0, byteRead);
				}
				fileInputStream.close();
				fileOutputStream.flush();
				fileOutputStream.close();
				reinitItems();
				TdpAdapter adapter=new TdpAdapter(FileList.this,R.layout.tdp_item,items,mListener1,mListener2);
				ListView dailyList=(ListView)findViewById(R.id.daily_list);
				dailyList.setAdapter(adapter);
			}catch(Exception e){

			}
		}
	}
	public String getPath(final Context context, final Uri uri) {//处理文件路径

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{split[1]};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}
	public String getDataColumn(Context context, Uri uri, String selection,
								String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {column};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
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
		        	 selectedFile.delete();
					 reinitItems();
					 TdpAdapter adapter=new TdpAdapter(FileList.this,R.layout.tdp_item,items,mListener1,mListener2);
					 ListView dailyList=(ListView)findViewById(R.id.daily_list);
					 dailyList.setAdapter(adapter);
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
			String type = currentFile .getName().substring(currentFile .getName().lastIndexOf(".") + 1);
			String detail="文件类型："+type;
			int imageId=0;
			String time="no time";
			if (imageId==R.mipmap.basic_file){
				imageId=0;
			}
			TDPitem item=new TDPitem(currentFile,title,detail,imageId,time,TDPitem.SHOW);
				
			items.add(item);
			current ++;
		}
    }
	
    public void reinitItems(){
		File polarisHubFolder =new File(Environment.getExternalStorageDirectory(),"PolarisHub");
		while(!polarisHubFolder.exists()){
			System.out.println("Download:"+polarisHubFolder.exists() );
			polarisHubFolder.mkdir();
		}
		filelist = polarisHubFolder.listFiles();//重新扫描文件夹
		initItems();
	}

}
