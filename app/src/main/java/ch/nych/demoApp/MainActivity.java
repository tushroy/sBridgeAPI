package ch.nych.demoApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ch.nych.soundtransmitter.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_receiver = (Button) this.findViewById(R.id.btn_receiver);
        Button btn_sender = (Button) this.findViewById(R.id.btn_sender);

        if (btn_receiver != null) {
            btn_receiver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(v.getContext(), Receiving.class));
                }
            });
        }

        if (btn_sender != null) {
            btn_sender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(v.getContext(), Transmission.class));
                }
            });
        }
    }
}
