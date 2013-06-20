package io.salina.android.feedbacks.model;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import com.google.gson.annotations.SerializedName;

public class Screen {
	@Attribute(name="name")
	private String name;
	
	@Attribute(name="activity")
	private String activity;
	
	@ElementList(name="functions")
	private List<String> function;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public List<String> getFunction() {
		return function;
	}

	public void setFunction(List<String> functions) {
		this.function = functions;
	}
}
