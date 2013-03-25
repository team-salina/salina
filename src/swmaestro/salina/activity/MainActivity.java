package swmaestro.salina.activity;

import org.salina.android.Salina;
import org.salina.android.SalinaSession;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	public static final String LOCALYTICS_APP_KEY = "e3c179706700b7e6fc82e13-5a09d386-879c-11e2-3339-008e703cf207";
	private SalinaSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Salina.init(this, "org.salina.android.test", "cafebabe");
        
        Log.d("MainActivity", "onCreate()");
        
        session = new SalinaSession(this);
        session.open();
        
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	
    	Salina.attachFeedbackLabel();
    	session.open();
    }
    
    
    @Override
    protected void onPause() {
    	Salina.releaseFeedbackLabel();
    	session.close();
    	session.upload();
    	super.onPause();
    }


    
}
