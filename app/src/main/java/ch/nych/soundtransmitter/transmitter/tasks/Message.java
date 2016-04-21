package ch.nych.soundtransmitter.transmitter.tasks;

import java.util.Arrays;

import ch.nych.soundtransmitter.transmitter.tasks.modulation.tone.Tone;

/**
 * Created by nych on 4/6/16.
 */
public class Message {
    /**
     * Indicates that there was a problem during the sending task.
     */
    public final static int STATE_ABORT = -1;

    /**
     * Indicates that the message is in preparation or has to wait for an other message is played
     * right now.
     */
    public final static int STATE_PENDING = 0;

    /**
     * Indicates that the message is currently played by the device.
     */
    public final static int STATE_SENDING = 1;

    /**
     * Indicates that the message has been sent successfully.
     */
    public final static int STATE_SENT = 2;

    /**
     * Stores the original data bytes.
     */
    private byte[] originalData = null;

    /**
     * Stores the data prepared for the modulation task.
     */
    private byte[] preparedData = null;

    /**
     * Stores the references to the shared tone set from the
     * {@link ch.nych.soundtransmitter.transmitter.Transmitter}. Therefore it is important, that
     * this objects are not accessed from outside the transmission module.
     */
    private Tone[] toneSequence = null;

    /**
     * The local message state indicates the current state / phase the message is in. During the
     * transmission process, the message passes states.
     */
    private volatile int state = -1;

    /**
     * The default constructor for the {@link Message} class takes the data bytes and sets the
     * message state to pending.
     * @param data a byte array of the data to transmit
     */
    public Message(final byte[] data) {
        this.originalData = data;
        this.state = Message.STATE_PENDING;
    }

    /**
     * This method is only intended for internal usage and should not be accessed from outside the
     * module.
     * @return a byte array containing the data
     */
    public byte[] getOriginalData() {
        return this.originalData;
    }

    /**
     * This method is only intended for internal usage and should not be accessed from outside the
     * module.
     * @return a byte array containing the information prepared to send
     */
    public byte[] getPreparedData() {
        return this.preparedData;
    }

    /**
     * This method is only intended for internal usage and should not be accessed from outside the
     * module.
     * @param preparedData a byte array containing the information prepared to send
     */
    public void setPreparedData(final byte[] preparedData) {
        this.preparedData = preparedData;
    }

    /**
     * For accessing the modulated signal from outside the transmission module, set the internal
     * flag to false.
     * @param internal indicates whether the array is accessed from an internal or an external
     *                 method.
     * @return if the internal flag is true, the return value is a reference to the original data.
     * Otherwise, the method will return a copy of the tone set.
     */
    public Tone[] getToneSequence(final boolean internal) {
        Tone[] toneSequence = null;
        if(internal) {
            toneSequence = this.toneSequence;
        } else {
            toneSequence = Arrays.copyOf(this.toneSequence, this.toneSequence.length);
        }
        return toneSequence;
    }

    /**
     * This method is only intended for internal usage and should not be accessed from outside the
     * module.
     * @param toneSequence an array of tones
     */
    public void setToneSequence(final Tone[] toneSequence) {
        this.toneSequence = toneSequence;
    }

    /**
     * Getter for the Message state. The Message state indicates the transmission phase in which the
     * message currently is.
     * @return either Message.STATE_PENDING, Message.STATE_SENDING, Message.STATE_SENT or
     * Message.STATE_ABORT
     */
    public int getState() {
        return this.state;
    }

    /**
     * This method is only intended for internal usage and should not be accessed from outside the
     * module.
     * @param state the state to set, see static variables of the {@link Message} class.
     */
    public void setState(final int state) {
        this.state = state;
    }
}
