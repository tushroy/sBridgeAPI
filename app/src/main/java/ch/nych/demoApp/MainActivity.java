package ch.nych.demoApp;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ch.nych.soundtransmitter.R;
import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.transmitter.Transmitter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.init();

    }

    private void init() {
        final Transmitter transmitter = new Transmitter(Transmitter.FOUR_STATE_TRANSMITTER);
        final Receiver receiver = new Receiver();
        /*final byte[] defaultMessage = new byte[]{'A','n','d','r','o','i','d'};
        final EditText txt_message = (EditText) this.findViewById(R.id.txt_message);
        Button btn_send = (Button) this.findViewById(R.id.btn_send);
        final boolean run = false;
        if (btn_send != null) {
            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*byte[] message = null;
                    if(txt_message.length() <= 0) {
                        message = defaultMessage;
                    } else {
                        char[] carray = txt_message.getText().toString().toCharArray();
                        message = new byte[carray.length];
                        for(int i = 0; i < message.length; i++) {
                            message[i] = (byte) carray[i];
                        }
                    }

                    transmitter.transmitData(message);
                    if(run) {
                        receiver.shutdownReceiver();
                    } else {
                        receiver.startReceiver();
                    }
                }
            });
        }*/
    }


}
