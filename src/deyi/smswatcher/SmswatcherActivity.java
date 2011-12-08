package deyi.smswatcher;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class SmswatcherActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView tv = new TextView(this);
        tv.setText("SmsWatcher v1.3");
        setContentView(tv);
        /**
        * IntentFilter filter = new IntentFilter( "android.provider.Telephony.SMS_RECEIVED" );
        * filter.setPriority( IntentFilter.SYSTEM_HIGH_PRIORITY );
        * registerReceiver( new SmsReciever(), filter );
        */
    }
}