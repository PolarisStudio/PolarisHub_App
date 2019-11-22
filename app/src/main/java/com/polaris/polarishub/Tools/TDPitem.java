package com.polaris.polarishub.Tools;

import android.annotation.SuppressLint;

import java.io.File;

@SuppressLint("DefaultLocale") public class TDPitem {
	
	private String title;
	
	private String briefDetail;
	
	private int imageId;
	
	private String log;

	private File file;
	
	int state;
	
	public static final int SHOW=0;
	
	public static final int MANAGE=1;
	
	public TDPitem(File file, String title, String detail, int imageId, String log, int state){
		this.file=file;
		this.title=brief(title,10);
		this.briefDetail=brief(detail,50);
		this.imageId=imageId;
		this.log=processLog(log);
		this.state=state;
	}
	
	public String processLog(String origionalLog){
		return ".";
	}
	
	public String brief(String detail,int a){
		
		detail = detail.replaceAll("(\r\n|\r|\n|\n\r)", ""); 
		int length=detail.length();
		if(length>=a){
			detail=detail.substring(0,a);
			briefDetail=detail+" ...";
		}else{
			briefDetail=detail;
		}
		return briefDetail;
	}
	
	public File getFile(){
		return file;
	}
	public String getTitle(){
		return title;
	}
	public String getBriefDetail(){
		return briefDetail;
	}
	public int getimageId(){
		return imageId;
	}
	public String getLog(){
		return log;
	}
	public int getState(){
		return state;
	}
	public void setState(int state){
		this.state=state;
	}

}
