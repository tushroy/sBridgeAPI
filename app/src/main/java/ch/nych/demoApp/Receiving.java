package ch.nych.demoApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ch.nych.soundtransmitter.R;
import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.util.Configuration;

public class Receiving extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        Receiver receiver = new Receiver();
        receiver.initReceiver(Configuration.newUltrasonicConfiguration());
        receiver.startReceiver();
    }
}
