package ch.nych.soundtransmitter.transmitter.tasks.modulation;

import android.util.Log;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.tasks.Message;
import ch.nych.soundtransmitter.transmitter.tasks.sending.SendingTask;
import ch.nych.soundtransmitter.transmitter.tasks.TransmissionTask;
import ch.nych.soundtransmitter.transmitter.tasks.modulation.tone.Tone;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Map bits of the dataBytes to the tones.
 */
public class ModulationTask extends TransmissionTask {

    /**
     *
     */
    private final String logTag = Configuration.LOG_TAG + ":Mod";

    /**
     * 10000000
     */
    private short mask = 128;

    /**
     *
     */
    private Tone[] toneSet = null;

    /**
     *
     */
    private byte[] preamble = null;

    /**
     *
     */
    private byte[] dataBytes = null;

    /**
     *
     * @param transmitter
     * @param message
     */
    public ModulationTask(final Transmitter transmitter, final Message message) {
        super(transmitter, message, TransmissionTask.MODULATION_TASK);
        this.toneSet = this.transmitter.getToneSet();
        this.preamble = this.configuration.getPreamble();
        this.dataBytes = this.message.getDataBytes();
    }

    /**
     *
     * @return
     */
    private Tone[] singleChannelModulation() {
        int size = this.dataBytes.length * 8 + this.preamble.length + 2;
        Tone[] modulatedData = new Tone[size];
        int index = 0;

        modulatedData[index++] = this.toneSet[0];
        for(int i = 0; i < this.preamble.length; i++) {
            if(this.preamble[i] == 0) {
                modulatedData[index++] = this.toneSet[1];
            } else if(this.preamble[i] == 1) {
                modulatedData[index++] = this.toneSet[2];
            }
        }
        for(int i = 0; i < this.dataBytes.length; i++) {
            Log.d(this.logTag, "Modulate byte: " + Integer.toHexString(this.dataBytes[i]));
            for(int j = 0; j < 8; j++) {
                if((this.dataBytes[i] & this.mask) == 0) {
                    modulatedData[index++] = this.toneSet[1];
                } else {
                    modulatedData[index++] = this.toneSet[2];
                }
                this.mask = (short) (this.mask >> 1);
            }
            this.mask = 128;
        }
        modulatedData[index] = this.toneSet[0];

        return modulatedData;
    }

    /**
     *
     */
    private Tone[] twoChannelModulation() {
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

    /**
     *
     * @return
     */
    private Tone[] threeChannelModulation() {
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

    @Override
    protected TransmissionTask getNextTask() {
        return new SendingTask(this.transmitter, this.message);
    }

    @Override
    public void run() {
        if(this.configuration.getTransmissionMode() == Configuration.SINGLE_CHANNEL_TRANSMISSION) {
            this.message.setModulatedData(this.singleChannelModulation());
        } else if(this.configuration.getTransmissionMode() ==
                Configuration.TWO_CHANNEL_TRANSMISSION) {
            this.message.setModulatedData(this.twoChannelModulation());
        } else if(this.configuration.getTransmissionMode() ==
                Configuration.THREE_CHANNEL_TRANSMISSION) {
            this.message.setModulatedData(this.threeChannelModulation());
        }
        this.transmitterCallback();
    }
}
