package org.salina.android.model;

import java.util.Calendar;
import java.util.Date;

import org.salina.android.DatapointHelper;
import org.salina.android.Salina;
import org.salina.android.SalinaUtils;

import com.google.gson.annotations.SerializedName;


/**
 * 피드백 모델 클래스
 * @author 이준영
 *
 */
public class Feedback {
	@SerializedName("seq")
	private int pk = -1;
	private String user_id = "";
	private String device_key;
	private Category category;
	private String app_id;
	private String pub_date;
	private String contents;
	
	public Feedback(){ }
	
	public Feedback(Category category, String contents) {
		this.device_key = DatapointHelper.getAndroidIdHashOrNull(Salina.getContext());
		this.contents = contents;
		this.category = category;
		
		this.app_id = Salina.getAppId();
		this.pub_date = SalinaUtils.dateToStringNow();
	}
	
	public Feedback(int pk, String user_id, String device_key,
			Category category, String app_id, String pub_date, String contents) {
		this.pk = pk;
		this.user_id = user_id;
		this.device_key = device_key;
		this.category = category;
		this.app_id = app_id;
		this.pub_date = pub_date;
		this.contents = contents;
	}
	public int getPk() {
		return pk;
	}
	public void setPk(int pk) {
		this.pk = pk;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getDevice_key() {
		return device_key;
	}
	public void setDevice_key(String device_key) {
		this.device_key = device_key;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public String getApp_id() {
		return app_id;
	}
	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}
	public String getPub_date() {
		return pub_date;
	}
	public void setPub_date(Date pub_date) {
		this.pub_date = SalinaUtils.dateToString(pub_date);
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}

	@Override
	public String toString() {
		return "Feedback [pk=" + pk + ", user_id=" + user_id + ", device_key="
				+ device_key + ", category=" + category + ", app_id=" + app_id
				+ ", pub_date=" + pub_date + ", contents=" + contents + "]";
	}
	
	
	
	
}
