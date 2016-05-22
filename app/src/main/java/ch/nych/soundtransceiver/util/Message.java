package ch.nych.soundtransceiver.util;

import android.util.Log;

import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import ch.nych.soundtransceiver.transmitter.tasks.modulation.tone.Tone;

/**
 * This class is a container for the dataBytes to transmit. It stores the
 * original bytes, as well as the data in modulated form. Besides, the
 * Message class also contains a messageState, indicates if the message was
 * sent successfully or if it is still in the sending process.
 * Created by nych on 4/6/16.
 */
public class Message {

	private final static String LOG_TAG = Configuration.GLOBAL_LOG_TAG +
			":Message";

    /**
     * Message states
     */
    public enum MessageState {
		IN_PROGRESS,
		SENDING,
		SENT,
		SENDING_ABORT,
		RECEIVED,
		VALID,
		INVALID_PREAMBLE,
		INVALID_CHECKSUM
	};

	/**
	 *
	 */
	private Checksum crc32 = null;

    /**
     * Stores the original data bytes.
     */
    private byte[] dataBytes = null;

	/**
	 *
	 */
	private byte[] checksumBytes = null;

    /**
     * Stores the references to the shared tone set from the
     * {@link ch.nych.soundtransceiver.transmitter.Transmitter}. Therefore it
	 * is important, that this objects are not accessed from outside the
	 * transmission module.
     */
    private Tone[] timeDomainData = null;

	/**
	 *
	 */
	private double[][] frequencyDomainData = null;

    /**
     * The local message messageState indicates the current messageState /
	 * phase the message is in. During the transmission process, the message
	 * passes states.
     */
    private volatile MessageState messageState = null;

    /**
     * The default constructor for the {@link Message} class takes the data
	 * bytes and sets the message messageState to pending.
     */
    public Message(final MessageState messageState) {
        this.messageState = messageState;
		this.crc32 = new CRC32();
    }

	/**
	 *
	 */
	private byte[] checksumToBytes() {
		short mask = 0b0000000011111111;
		byte[] checksumBytes = new byte[4];
		this.crc32.reset();
		this.crc32.update(dataBytes, 0, dataBytes.length);
		long checksum = this.crc32.getValue();
		for(int i = (checksumBytes.length - 1); i >= 0; i--) {
			checksumBytes[i] = (byte) (checksum & mask);
			checksum = checksum >>> 8;
		}
		return checksumBytes;
	}

    /**
     * This method is only intended for internal usage and should not be
	 * accessed from outside the module.
     * @return a byte array containing the data
     */
    public byte[] getDataBytes(final boolean includeChecksum) {
		if(includeChecksum) {
			byte[] concatenatedData =
					new byte[this.dataBytes.length + this.checksumBytes.length];
			System.arraycopy(this.dataBytes, 0, concatenatedData, 0,
					this.dataBytes.length);
			System.arraycopy(this.checksumBytes, 0, concatenatedData,
					this.dataBytes.length, this.checksumBytes.length);
			return concatenatedData;
		} else {
			return this.dataBytes;
		}
    }

	/**
	 *
	 * @param dataBytes
	 */
	public void setDataBytes(final byte[] dataBytes) {
		if(dataBytes == null) {
			Log.d(Message.LOG_TAG, "Passed invalid argument to setDataBytes()");
			return;
		}

		if(this.messageState == MessageState.IN_PROGRESS) {
			this.dataBytes = dataBytes;
			this.checksumBytes = this.checksumToBytes();
		} else if(this.messageState ==
				MessageState.RECEIVED && dataBytes.length > 4) {
			this.dataBytes =
					Arrays.copyOfRange(dataBytes, 0, dataBytes.length - 4);
			this.checksumBytes =
					Arrays.copyOfRange(dataBytes,
							dataBytes.length - 4,
							dataBytes.length);
			if(Arrays.equals(this.checksumToBytes(), this.checksumBytes)) {
				this.messageState = MessageState.VALID;
			} else {
				this.messageState = MessageState.INVALID_CHECKSUM;
			}
		}
	}

	/**
	 *
	 * @return
	 */
	public Tone[] getCopyOfTimeDomainData() {
		return Arrays.copyOf(this.timeDomainData, this.timeDomainData.length);

	}

    /**
     * For accessing the modulated signal from outside the transmission
	 * module, set the internal flag to false.
     * @return if the internal flag is true, the return value is a reference
	 * to the original data. Otherwise, the method will return a copy of the
	 * tone set.
     */
    public Tone[] getTimeDomainData() {
        return this.timeDomainData;
    }

    /**
     * This method is only intended for internal usage and should not be
	 * accessed from outside the
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
     * Getter for the Message messageState. The Message messageState
	 * indicates the transmission phase in which the message currently is.
     * @return either Message.STATE_PENDING, Message.STATE_SENDING,
	 * Message.STATE_SENT or Message.STATE_ABORT
     */
    public MessageState getMessageState() {
        return this.messageState;
    }

    /**
     * This method is only intended for internal usage and should not be
	 * accessed from outside the module.
     * @param messageState    The messageState to set, see static variables
	 *                           of the {@link Message} class.
     */
    public void setMessageState(final MessageState messageState) {
        this.messageState = messageState;
    }
}
