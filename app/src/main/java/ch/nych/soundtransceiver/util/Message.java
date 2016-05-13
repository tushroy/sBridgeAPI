package ch.nych.soundtransceiver.util;

import java.util.Arrays;

import ch.nych.soundtransceiver.transmitter.tasks.modulation.tone.Tone;

/**
 * This class is a container for the dataBytes to transmit. It stores the original bytes, as well as
 * the data in modulated form. Besides, the Message class also contains a messageState, indicates if the
 * message was sent successfully or if it is still in the sending process.
 * Created by nych on 4/6/16.
 */
public class Message {

    /**
     * Message states
     */
    public enum MessageState {SENDING_ABORT, WAIT_FOR_SENDING, SENDING,
		SENT, IN_PROGRESS, INTERPRETED_SUCCESSFULLY, CORRUPTED};

    /**
     * Stores the original data bytes.
     */
    private byte[] dataBytes = null;

    /**
     * Stores the references to the shared tone set from the
     * {@link ch.nych.soundtransceiver.transmitter.Transmitter}. Therefore it is important, that
     * this objects are not accessed from outside the transmission module.
     */
    private Tone[] timeDomainData = null;

	/**
	 *
	 */
	private double[][] frequencyDomainData = null;

    /**
     * The local message messageState indicates the current messageState / phase the message is in. During the
     * transmission process, the message passes states.
     */
    private volatile MessageState messageState = null;

    /**
     * The default constructor for the {@link Message} class takes the data bytes and sets the
     * message messageState to pending.
     * @param data    A byte array of the data to transmit
     */
    public Message(final byte[] data) {
        this.dataBytes = data;
        this.messageState = MessageState.WAIT_FOR_SENDING;
    }

    /**
     * This method is only intended for internal usage and should not be accessed from outside the
     * module.
     * @return a byte array containing the data
     */
    public byte[] getDataBytes() {
        return this.dataBytes;
    }

	public void setDataBytes(final byte[] dataBytes) {
		this.dataBytes = dataBytes;
	}

    /**
     * For accessing the modulated signal from outside the transmission module, set the internal
     * flag to false.
     * @param internal    Indicates whether the array is accessed from an internal or an external
     *                    method.
     * @return if the internal flag is true, the return value is a reference to the original data.
     * Otherwise, the method will return a copy of the tone set.
     */
    public Tone[] getTimeDomainData(final boolean internal) {
        Tone[] toneSequence = null;
        if(internal) {
            toneSequence = this.timeDomainData;
        } else {
            toneSequence = Arrays.copyOf(this.timeDomainData, this.timeDomainData.length);
        }
        return toneSequence;
    }

    /**
     * This method is only intended for internal usage and should not be accessed from outside the
     * module.
     * @param timeDomainData     An array of tones
     */
    public void setTimeDomainData(final Tone[] timeDomainData) {
        this.timeDomainData = timeDomainData;
    }

	public double[][] getFrequencyDomainData() {
		return this.frequencyDomainData;
	}

	public void setFrequencyDomainData(final double[][] frequencyDomainData) {
		this.frequencyDomainData = frequencyDomainData;
	}

    /**
     * Getter for the Message messageState. The Message messageState indicates the transmission phase in which the
     * message currently is.
     * @return either Message.STATE_PENDING, Message.STATE_SENDING, Message.STATE_SENT or
     * Message.STATE_ABORT
     */
    public MessageState getMessageState() {
        return this.messageState;
    }

    /**
     * This method is only intended for internal usage and should not be accessed from outside the
     * module.
     * @param messageState    The messageState to set, see static variables of the {@link Message} class.
     */
    public void setMessageState(final MessageState messageState) {
        this.messageState = messageState;
    }
}
