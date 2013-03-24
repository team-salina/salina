package swmaestro.salina.activity;

import org.salina.android.Salina;
import org.salina.android.SalinaSession;

import swmaestro.salina.SalinaContext;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.localytics.android.LocalyticsSession;

public class MainActivity extends Activity {
	public static final String LOCALYTICS_APP_KEY = "e3c179706700b7e6fc82e13-5a09d386-879c-11e2-3339-008e703cf207";
	private SalinaSession session;
	private LocalyticsSession localyticsSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Salina.init(this, "org.salina.android.test", "cafebabe");
        
        Log.d("MainActivity", "onCreate()");
        
        this.localyticsSession = new LocalyticsSession (
        		this.getApplicationContext(),
        		MainActivity.LOCALYTICS_APP_KEY);
        
        this.localyticsSession.open();
        this.localyticsSession.upload();
        
        session = new SalinaSession(this);
        session.open();
        
        TextView tvText = (TextView)findViewById(R.id.btText);
        tvText.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		localyticsSession.tagEvent("Test Button");
        	}
        });
        
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	
    	localyticsSession.open();
    	Salina.attachFeedbackLabel();
    	session.open();
    }
    
    
    @Override
    protected void onPause() {
    	Salina.releaseFeedbackLabel();
    	localyticsSession.close();
    	localyticsSession.upload();
    	session.close();
    	session.upload();
    	super.onPause();
    }


    
}
