package ch.nych.demoApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ch.nych.soundtransmitter.R;

public class Receiving extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        ch.nych.soundtransmitter.receiver.Receiver receiver = new ch.nych.soundtransmitter.receiver.Receiver();

        receiver.startReceiver();
    }
}
