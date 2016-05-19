package ch.nych.demoApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ch.nych.TransmitterListener;
import ch.nych.soundtransceiver.R;
import ch.nych.soundtransceiver.transmitter.Transmitter;
import ch.nych.soundtransceiver.util.Message;
import ch.nych.soundtransceiver.util.Configuration;

public class BroadcastActivity extends AppCompatActivity
        implements TransmitterListener {

	private Transmitter transmitter;
	private Button sendButton = null;
	private EditText editText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_transmission);

		this.transmitter = new Transmitter();
        this.transmitter.addTransmitterListener(this);

		this.sendButton = (Button) this.findViewById(R.id.sendButton);
		this.editText = (EditText) this.findViewById(R.id.editText);

        if (this.sendButton != null) {
            this.sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte[] message = null;
                    char[] chars =
                            editText.getText().toString().toCharArray();
                    message = new byte[chars.length];
                    for (int i = 0; i < message.length; i++) {
                        message[i] = (byte) chars[i];
                    }
                    transmitter.transmitData(message);
                }
            });
        }
    }

	@Override
	protected void onStart() {
		super.onStart();
		if(!this.transmitter.initTransmitter(Configuration
				.newUltrasonicConfiguration())) {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Couldn't initialize Transmitter",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			});
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.transmitter.shutdownAndAwaitTermination();
	}

    @Override
    public void messageSent(final Message message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "BroadcastActivity terminated with state: " +
                        message.getMessageState(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}
