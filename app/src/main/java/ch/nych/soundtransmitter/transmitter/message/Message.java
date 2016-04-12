package ch.nych.soundtransmitter.transmitter.message;

import ch.nych.soundtransmitter.transmitter.tone.Tone;

/**
 * Created by nych on 4/6/16.
 */
public class Message {
    public final static int STATE_ABORT = -1;
    public final static int STATE_PENDING = 0;
    public final static int STATE_SENDING = 1;
    public final static int STATE_SENT = 2;

    private byte[] originalData = null;
    private byte[] preparedData = null;
    private Tone[] toneSequence = null;
    private volatile int state = -1;

    public Message(byte[] data) {
        this.originalData = data;
        this.state = Message.STATE_PENDING;
    }

    public byte[] getOriginalData() {
        return this.originalData;
    }

    public byte[] getPreparedData() {
        return this.preparedData;
    }

    public void setPreparedData(byte[] preparedData) {
        this.preparedData = preparedData;
    }

    public Tone[] getToneSequence() {
        return this.toneSequence;
    }

    public void setToneSequence(Tone[] toneSequence) {
        this.toneSequence = toneSequence;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
