package ch.nych.soundtransmitter.transmitter.tasks.modulation;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.tasks.Message;
import ch.nych.soundtransmitter.transmitter.tasks.modulation.tone.Tone;

/**
 * Created by nych on 5/11/16.
 */
public class TwoChannelModulationTask extends ModulationTask {

    public TwoChannelModulationTask(Transmitter transmitter, Message message) {
        super(transmitter, message);
    }

    @Override
    protected Tone[] modulateData() {
        int size = this.dataBytes.length * 8 + this.preamble.length + 2;
        Tone[] modulatedData = new Tone[size];
        int nullTone = 1;
        int oneTone = 2;
        int index = 0;

        modulatedData[index++] = this.toneSet[0];
        for(int i = 0; i < this.preamble.length; i++) {
            if(this.preamble[i] == 0) {
                modulatedData[index++] = this.toneSet[nullTone];
                nullTone = nullTone == 1 ? 3 : 1;
            } else if(this.preamble[i] == 1) {
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
