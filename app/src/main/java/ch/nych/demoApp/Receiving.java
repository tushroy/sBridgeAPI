package ch.nych.demoApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ch.nych.soundtransmitter.R;
import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.util.Configuration;

public class Receiving extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        Receiver receiver = new Receiver();
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
}
