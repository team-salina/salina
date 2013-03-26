package org.salina.android.model;

import java.util.ArrayList;
import java.util.List;

import org.salina.android.SalinaSession;
import org.salina.android.core.DeviceInfo;

import com.google.gson.annotations.SerializedName;

/**
 * Chunk Data
 * @author nnoco
 *
 */
public class ControlLog {
	@SerializedName("Session")
	private List<SalinaSession> sessions;
	
	@SerializedName("Device")
	private DeviceInfo[] deviceInfo;
	
	@SerializedName("Crash")
	private List<Crash> crash;
	
	public void addSession(SalinaSession session) {
		if(null == this.sessions) {
			this.sessions = new ArrayList<SalinaSession>();
		}
		this.sessions.add(session);
	}
	
	public void setDeviceInfo(DeviceInfo deviceInfo) {
		if(null == this.deviceInfo) {
			this.deviceInfo = new DeviceInfo[1];
		}
		
		this.deviceInfo[1] = deviceInfo;
	}
	
	public void addCrash(Crash crash) {
		if(null == this.crash) {
			this.crash = new ArrayList<Crash>();
		}
		
		this.crash.add(crash);
	}
}
