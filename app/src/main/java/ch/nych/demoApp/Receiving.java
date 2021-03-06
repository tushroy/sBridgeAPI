package ch.nych.demoApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import ch.nych.ReceiverListener;
import ch.nych.soundtransceiver.R;
import ch.nych.soundtransceiver.receiver.Receiver;
import ch.nych.soundtransceiver.util.Message;
import ch.nych.soundtransceiver.util.Configuration;

public class Receiving extends AppCompatActivity implements ReceiverListener {

    private TextView textView = null;
    private Receiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_receiver);
        this.receiver = new Receiver();
        this.receiver.addReceiverListener(this);
        this.textView = (TextView) this.findViewById(R.id.textView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!receiver.initReceiver(Configuration.newUltrasonicConfiguration())) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Could not initialize Receiver",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
        receiver.startReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.receiver.stopReceiver();
    }

    @Override
    public void messageReceived(final Message message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = null;

                if(message.getMessageState() ==
                        Message.MessageState.INTERPRETED_SUCCESSFULLY) {
                    toast = Toast.makeText(
                            getApplicationContext(),
                            "Message received: ",
                            Toast.LENGTH_SHORT);

                    char[] text = new char[message.getDataBytes().length];
                    for (int i = 0; i < text.length; i++) {
                        text[i] = (char) message.getDataBytes()[i];
                    }
                    textView.setText(text, 0, text.length);
                    toast.show();
                } else {
                    textView.setText("Message corrupted");
                }
            }
        });
    }
}
