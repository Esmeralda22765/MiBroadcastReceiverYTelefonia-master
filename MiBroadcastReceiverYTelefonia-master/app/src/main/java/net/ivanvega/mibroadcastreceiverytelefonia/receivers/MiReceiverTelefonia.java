package net.ivanvega.mibroadcastreceiverytelefonia.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class MiReceiverTelefonia extends BroadcastReceiver {
    private static final String TAG = "MiReceiverTelefonia";

    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = sb.toString();
        Log.d(TAG, log);
        Toast.makeText(context, log, Toast.LENGTH_LONG).show();

        if(intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){
            Toast.makeText(context, "Me llego un mensaje", Toast.LENGTH_LONG).show();
            
            //RECUPERAR EL MENSAJE RECIBIDO
            Bundle bundle = intent.getExtras();
            SmsMessage[] smss;
            String str;

            if(bundle != null){
                Object[] opdus = (Object[])bundle.get("pdus");
                smss = new SmsMessage[opdus.length];
                for (int i = 0; i < smss.length; i++) {
                    smss [i] = SmsMessage.createFromPdu((byte[])opdus[i]);
                    str = "Mensaje de: " + smss [i].getOriginatingAddress() +
                            "\n" +
                            smss [i].getMessageBody().toString();
                    Toast.makeText(context, str, Toast.LENGTH_LONG).show();
                }
            }
            
        }

    }

}
