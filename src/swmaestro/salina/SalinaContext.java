package swmaestro.salina;

import org.salina.android.widget.FeedbackLabelService;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SalinaContext {
	private Context appContext;
	private static SalinaContext salinaContext;
	private SalinaContext (Context appContext){
		this.appContext = appContext;
	}
	
	
	
	
	/**
	 * SalinaContext ��ü�� ���� <br/>
	 * ���� ��Ƽ��Ƽ���� ������ ���� �ڵ带 ���� <br/>
	 * <p>
	 * <code>SalinaContext salinaContext = SalinaContext.getInstance(this);</code>
	 * </p>
	 * @param context Application Context Instance
	 * @return SalinaContext
	 */
	public static SalinaContext getInstance(Context context){
		if(salinaContext == null){
			salinaContext = new SalinaContext(context);
		}
		
		return salinaContext;
	}
}
