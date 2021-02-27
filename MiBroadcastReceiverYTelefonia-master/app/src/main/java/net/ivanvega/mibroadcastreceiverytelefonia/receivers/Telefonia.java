package net.ivanvega.mibroadcastreceiverytelefonia.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class Telefonia extends BroadcastReceiver {

    public TelephonyManager mTelephonyManager;
    public PhoneStateListener mPhoneStateListener;

    public static boolean isListening = false;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        mTelephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

        mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Toast.makeText(context, "RECEIVER> LLAMADA FINALIZADA", Toast.LENGTH_SHORT).show();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        Toast.makeText(context, "RECEIVER> LLAMADA ENTRANTE DE: " + incomingNumber + "<", Toast.LENGTH_SHORT).show();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Toast.makeText(context, "RECEIVER> LLAMADA CONTESTADA", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        if (!isListening) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            isListening = true;
        }
    }

}
