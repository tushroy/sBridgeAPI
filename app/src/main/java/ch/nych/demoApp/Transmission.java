package ch.nych.demoApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ch.nych.soundtransmitter.R;
import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.util.Configuration;

public class Transmission extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmission);
        final Transmitter transmitter = new Transmitter();
        transmitter.initTransmitter(Configuration.newUltrasonicConfiguration());

        final Button btn_send = (Button) this.findViewById(R.id.btn_send);
        final EditText txt_message = (EditText) this.findViewById(R.id.editText);

        if (btn_send != null) {

            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte[] message = null;
                    char[] carray = txt_message.getText().toString().toCharArray();
                    message = new byte[carray.length];
                    for (int i = 0; i < message.length; i++) {
                        message[i] = (byte) carray[i];
                    }
                    transmitter.transmitData(message);
                }
            });
        }
    }
}
