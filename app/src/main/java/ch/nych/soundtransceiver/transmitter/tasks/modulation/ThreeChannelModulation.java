package ch.nych.soundtransceiver.transmitter.tasks.modulation;

import ch.nych.soundtransceiver.transmitter.Transmitter;
import ch.nych.soundtransceiver.util.Message;
import ch.nych.soundtransceiver.transmitter.tasks.modulation.tone.Tone;

/**
 * Created by nych on 5/11/16.
 */
public class ThreeChannelModulation extends ModulationTask {

    public ThreeChannelModulation(Transmitter transmitter, Message message) {
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
                if(nullTone == 1) {
                    nullTone = 3;
                } else if(nullTone == 3) {
                    nullTone = 5;
                } else if(nullTone == 5) {
                    nullTone = 1;
                }
            } else if(this.preamble[i] == 1) {
                modulatedData[index++] = this.toneSet[oneTone];
                if(oneTone == 2) {
                    oneTone = 4;
                } else if(oneTone == 4) {
                    oneTone = 6;
                } else if(oneTone == 6) {
                    oneTone = 2;
                }
            }
        }
        for(int i = 0; i < this.dataBytes.length; i++) {
            for(int j = 0; j < 8; j++) {
                if((this.dataBytes[i] & this.mask) == 0) {
                    modulatedData[index++] = toneSet[nullTone];
                    if(nullTone == 1) {
                        nullTone = 3;
                    } else if(nullTone == 3) {
                        nullTone = 5;
                    } else if(nullTone == 5) {
                        nullTone = 1;
                    }
                } else {
                    modulatedData[index++] = this.toneSet[oneTone];
                    if(oneTone == 2) {
                        oneTone = 4;
                    } else if(oneTone == 4) {
                        oneTone = 6;
                    } else if(oneTone == 6) {
                        oneTone = 2;
                    }
                }
                this.mask = (short) (this.mask >> 1);
            }
            this.mask = 128;
        }
        modulatedData[index] = this.toneSet[0];

        return modulatedData;
    }
}
