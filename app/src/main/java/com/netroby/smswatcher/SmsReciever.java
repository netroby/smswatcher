package com.netroby.smswatcher;


import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SmsReciever extends BroadcastReceiver {
	// All available column names in SMS table
	// [_id, thread_id, address,
	// person, date, protocol, read,
	// status, type, reply_path_present,
	// subject, body, service_center,
	// locked, error_code, seen]

	public static final String DEBUG_TAG = "SmsPost";
	public static final String SMS_EXTRA_NAME = "pdus";
	public static final String SMS_URI = "content://sms";

	public static final String ADDRESS = "address";
	public static final String PERSON = "person";
	public static final String DATE = "date";
	public static final String READ = "read";
	public static final String STATUS = "status";
	public static final String TYPE = "type";
	public static final String BODY = "body";
	public static final String SEEN = "seen";

	public static final int MESSAGE_TYPE_INBOX = 1;
	public static final int MESSAGE_TYPE_SENT = 2;

	public static final int MESSAGE_IS_NOT_READ = 0;
	public static final int MESSAGE_IS_READ = 1;

	public static final int MESSAGE_IS_NOT_SEEN = 0;
	public static final int MESSAGE_IS_SEEN = 1;

	// Change the password here or give a user possibility to change it
	public static final byte[] PASSWORD = new byte[] { 0x20, 0x32, 0x34, 0x47,
			(byte) 0x84, 0x33, 0x58 };

	public void onReceive(Context context, Intent intent) {
		// Get SMS map from Intent
		Bundle extras = intent.getExtras();

		String messages = "";

		if (extras != null) {
			// Get received SMS array
			Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);

			// Get ContentResolver object for pushing encrypted SMS to incoming
			// folder
			ContentResolver contentResolver = context.getContentResolver();

			for (int i = 0; i < smsExtra.length; ++i) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);

				String body = sms.getMessageBody();
				String address = sms.getOriginatingAddress();

				messages += "SMS from " + address + " :\n";
				messages += body + "\n";

				// Here you can add any your code to work with incoming SMS
				// I added encrypting of all received SMS

				putSmsToDatabase(context, contentResolver, sms);
			}

			// Display SMS message
			Toast.makeText(context, messages, Toast.LENGTH_SHORT).show();
		}

		// WARNING!!!
		// If you uncomment next line then received SMS will not be put to
		// incoming.
		// Be careful!
		// this.abortBroadcast();
	}

	private void putSmsToDatabase(Context context, ContentResolver contentResolver,
			SmsMessage sms) {

		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            String address = sms.getOriginatingAddress();
            String body = sms.getMessageBody();
            try {
                String url = "http://192.168.1.123/android_api.php?sender="
                        + address + "&body=" + java.net.URLEncoder.encode(body, "UTF-8");
				String response = requestUrl(url);
				Toast.makeText(context, "Response result: " + response, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Can not connect the network", Toast.LENGTH_SHORT).show();
        }

	}

	private String requestUrl(String myurl) throws IOException {
		InputStream is = null;
		int len = 65535;
		try {
			URL url = new URL(myurl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			int response = conn.getResponseCode();
			Log.d(DEBUG_TAG, "The response is: " + response);
			is = conn.getInputStream();
			return readIt(is, len);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return "";
	}
	public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}
}