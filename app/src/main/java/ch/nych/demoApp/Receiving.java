package ch.nych.demoApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import ch.nych.BridgeListener;
import ch.nych.soundtransmitter.R;
import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.Frame;
import ch.nych.soundtransmitter.transmitter.tasks.Message;
import ch.nych.soundtransmitter.util.Configuration;

public class Receiving extends AppCompatActivity implements BridgeListener {

    private TextView textView = null;
    private Receiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        this.receiver = new Receiver();
        receiver.addListener(this);
        this.textView = (TextView) this.findViewById(R.id.textView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!receiver.initReceiver(Configuration.newUltrasonicConfiguration())) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(), "Could not initialize Receiver", Toast.LENGTH_SHORT);
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
    public void messageSent(Message message) {

    }

    @Override
    public void frameReceived(final Frame frame) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = null;

                if(frame.getState() == Frame.ANALYZED_SUCCESSFULLY) {
                    toast = Toast.makeText(
                            getApplicationContext(),
                            "Frame received: ",
                            Toast.LENGTH_SHORT);
                    char[] message = new char[frame.getDataBytes().length];
                    for (int i = 0; i < message.length; i++) {
                        message[i] = (char) frame.getDataBytes()[i];
                    }
                    textView.setText(message, 0, message.length);
                } else {
                    toast = Toast.makeText(
                            getApplicationContext(),
                            "Received Frame is corrupted",
                            Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });
    }
}
