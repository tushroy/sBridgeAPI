package ch.nych.soundtransceiver.transmitter.tasks.modulation;

import android.util.Log;

import ch.nych.soundtransceiver.transmitter.Transmitter;
import ch.nych.soundtransceiver.util.Message;
import ch.nych.soundtransceiver.transmitter.tasks.modulation.tone.Tone;

/**
 * Created by nych on 5/11/16.
 */
public class TwoChannelModulationTask extends ModulationTask {

    public TwoChannelModulationTask(Transmitter transmitter, Message message) {
        super(transmitter, message);
    }

    @Override
    protected Tone[] modulateData() {
		Log.d(ModulationTask.LOG_TAG, "modulate data");
        Tone[] modulatedData = new Tone[this.messageSize];
        int nullTone = 1;
        int oneTone = 2;
        int index = 0;

        modulatedData[index++] = this.toneSet[0];

        for(int i = 0; i < this.preambleBytes.length; i++) {
            if(this.preambleBytes[i] == 0) {
                modulatedData[index++] = this.toneSet[nullTone];
                nullTone = nullTone == 1 ? 3 : 1;
            } else if(this.preambleBytes[i] == 1) {
                modulatedData[index++] = this.toneSet[oneTone];
                oneTone = oneTone == 2 ? 4 : 2;
            }
        }

        for(int i = 0; i < this.dataBytes.length; i++) {
            for(int j = 0; j < 8; j++) {
                if((this.dataBytes[i] & this.mask) == 0) {
                    modulatedData[index++] = toneSet[nullTone];
                    nullTone = nullTone == 1 ? 3 : 1;
                } else {
                    modulatedData[index++] = this.toneSet[oneTone];
                    oneTone = oneTone == 2 ? 4 : 2;
                }
                this.mask = (short) (this.mask >> 1);
            }
            this.mask = 128;
        }

        modulatedData[index] = this.toneSet[0];

        return modulatedData;
    }
}
