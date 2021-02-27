package net.ivanvega.mibroadcastreceiverytelefonia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.provider.Telephony;
import android.view.View;
import android.widget.TextView;

import net.ivanvega.mibroadcastreceiverytelefonia.receivers.MiReceiverTelefonia;
import net.ivanvega.mibroadcastreceiverytelefonia.receivers.MyBroadcastReceiver;


public class MainActivity extends AppCompatActivity {
    MyBroadcastReceiver myBroadcastReceiver=
            new MyBroadcastReceiver();

    MiReceiverTelefonia miReceiverTelefonia = new MiReceiverTelefonia();

    Button btnS;
    TextView lbl;
    EditText txtTel, txtMessage;



    private void enviarSMS(String tel, String msj) {
        SmsManager smsManager =  SmsManager.getDefault();

        smsManager.sendTextMessage(tel,null, msj,
                null, null);



        Toast.makeText(
                this, "Mensaje enviado",
                Toast.LENGTH_LONG
        ).show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(myBroadcastReceiver);
    }



    ///////////////////////

    private TelephonyManager mTelephonyManager;

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1; //CONSTANTE DE PERMISO CONSEDIDO PARA SMS Y LLAMADAS


    /* WIDGETS */
    public EditText number; //EditText donde se ingresa el Numero a donde se espera la llamada para enviar SMS de respuesta automatica
    public EditText message; // EditText donde escribe el mensaje de respuesta automatica
    public Button send; // Boton para guardar los datos (no sirve xd)

    /*VARIABLES*/
    public String txtMensaje; //Cadena que sirve como almacen del texto del EditText de mensaje de respuesta
    public String txtNumero; //Cadena que almacena el numero ingresado en el EditText para compararlo con el numero de llamada de entrada
    public String numLlamada; //Cadena donde se almacena el inComingNumber para compararlo con el ingresado en el EditText y enviar sms



    PhoneStateListener mPhoneStateListener = new PhoneStateListener() { //EventListener del estado de la callamada recibida en el celular
        @Override
        public void onCallStateChanged(int state, String incomingNumber) { //Verifica el estado de la llamada
            super.onCallStateChanged(state, incomingNumber);

            numLlamada = incomingNumber;

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: //Llamada Terminada/Finalizada/Colgada
                    break;
                case TelephonyManager.CALL_STATE_RINGING: //Llamada entrante (sonando)
                    send();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: //Llamada contestada
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);

        btnS = findViewById(R.id.btnSend);
        lbl = findViewById(R.id.lbl);
        txtTel = findViewById(R.id.numero);
        txtMessage = findViewById(R.id.sms);



        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent broadcast = new Intent();
                //net.ivanvega.mibroadcastreceiverytelefonia.MIBROACAST

                broadcast.setAction(getString(R.string.action_broadcast));
                broadcast.putExtra("key1", "parametro de la difusion");
                sendBroadcast(broadcast);
            }
        });

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(getString(R.string.action_broadcast));
        this.registerReceiver(myBroadcastReceiver, filter);





        //Telephony.Sms .Intents.SMS_RECEIVED_ACTION

        IntentFilter intentFilterTel = new IntentFilter(Telephony.Sms .Intents.SMS_RECEIVED_ACTION);

        getApplicationContext().registerReceiver(miReceiverTelefonia,
                intentFilterTel  );


        number = findViewById(R.id.numero);
        message = findViewById(R.id.sms);
        send = findViewById(R.id.btnSend);

        mTelephonyManager = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE); //Administrador de la telefonia

        send.setEnabled(false);
        if(checkPermission(Manifest.permission.SEND_SMS)
                && checkPermission(Manifest.permission.READ_PHONE_STATE)) // Se verifica que los permisos hayan sido consedidos
        {
            send.setEnabled(true);
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE); //Pide el permiso para mandar sms
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 1); // Pide el permiso para checar el estado de llamadas
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE); //Escuchador del estatus de la llamada

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);// ""

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE); // ""

    }

    public void send() //Método Para responder un sms al número que se encuentra en la caja de texto por default
    {
        txtNumero = number.getText().toString(); //Se obtienens los datos de las
        txtMensaje = message.getText().toString(); //cajas de texto, proporcionadas por el usuario :v

        String phoneNumber = this.txtNumero;
        String smsMessage = this.txtMensaje;

        if(phoneNumber == null || phoneNumber.length() == 0 ||     //Compara que el contenido de los
                smsMessage == null || smsMessage.length()==0)      // campos no sean null  o con lenght ==0
        {
            Toast.makeText(this, "No se pudo responder con un mensaje", Toast.LENGTH_SHORT).show();
            return; //retorna el método sin respuesta, mostrando como aclaración que no se pudo responder con un mensaje
        }

        if(checkPermission(Manifest.permission.SEND_SMS)) //Si los permisos han sido consedidos...
        {
            if(phoneNumber.equals(numLlamada)) //... compara que el número del EditText coincida con el de la llamada entrante ...
            {
                SmsManager smsManager = SmsManager.getDefault(); // ... y comienza el proceso de administrador de mensajes sms
                smsManager.sendTextMessage(phoneNumber,null,smsMessage, null,null); // Pasa los parametros necesarios
                Toast.makeText(this, "Mensaje enviado...!", Toast.LENGTH_SHORT).show(); //Y al enviarse muestra un mensaje exitoso
            }
            else
            {           // Si no coincide entonces mostrara el numero de la llamada entrante :v
                Toast.makeText(this, "Mi mami: " + txtNumero, Toast.LENGTH_SHORT).show();
            }

        }
        else //Si los permisos no han sido coincididos muestra el mensaje de denegado
        {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPermission(String permission) //Este metodo permite checar el estado del permiso y devuelve el valor booleano
    {                                                   //de acuerdo al permiso que se pasa como parametro (permission)
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);  //Si el valor coincide con el valor de GRANTED, devuelve true
    }

}
