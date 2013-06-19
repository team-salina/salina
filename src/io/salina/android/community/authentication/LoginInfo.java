package io.salina.android.community.authentication;

import com.google.gson.annotations.SerializedName;

public class LoginInfo {
	@SerializedName("username")
	private String mId;
	
	@SerializedName("password")
	private String mPassword;
	
	public LoginInfo(String id, String password) {
		this.mId = id;
		this.mPassword = password;
	}

	public String getmId() {
		return mId;
	}

	public void setmId(String mId) {
		this.mId = mId;
	}

	public String getmPassword() {
		return mPassword;
	}

	public void setmPassword(String mPassword) {
		this.mPassword = mPassword;
	}
	
	
}
