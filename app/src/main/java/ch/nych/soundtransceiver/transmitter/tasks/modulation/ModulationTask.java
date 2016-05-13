package ch.nych.soundtransceiver.transmitter.tasks.modulation;

import ch.nych.soundtransceiver.transmitter.Transmitter;
import ch.nych.soundtransceiver.util.Message;
import ch.nych.soundtransceiver.transmitter.tasks.modulation.tone.Tone;
import ch.nych.soundtransceiver.transmitter.tasks.sending.SendingTask;
import ch.nych.soundtransceiver.transmitter.tasks.TransmissionTask;
import ch.nych.soundtransceiver.util.Configuration;

/**
 * This class is responsible for the modulation of the dataBytes. The
 * modulation happens through the  mapping of the single bits to the
 * corresponding tone. As there are different transmission modes, there are
 * multiple modulation implementations.
 */
public abstract class ModulationTask extends TransmissionTask {

    /**
     * The local log tag
     */
    protected final static String LOG_TAG = Configuration.GLOBAL_LOG_TAG +
            ":ModTask";

    /**
     * The mask is used to get the single bits out of a byte
     * 0000 0000 1000 0000
     */
    protected short mask = 128;

    /**
     * The local reference to the shared toneSet resource
     */
    protected Tone[] toneSet = null;

    /**
     * The preamble of the message
     */
    protected byte[] preamble = null;

    /**
     * The bytes to modulate
     */
    protected byte[] dataBytes = null;

    /**
     * Default constructor
     * @param transmitter    the reference to the calling {@link Transmitter} instance is used for
     *                       the shared resources and the callback.
     * @param message        The {@link Message} instance, containing the data to modulate
     */
    public ModulationTask(final Transmitter transmitter, final Message message) {
        super(transmitter, message, TaskType.MODULATION);
        this.toneSet = this.transmitter.getToneSet();
        this.preamble = this.configuration.getPreamble();
        this.dataBytes = this.message.getDataBytes();
    }

    protected abstract Tone[] modulateData();

    @Override
    protected TransmissionTask getNextTask() {
        return new SendingTask(this.transmitter, this.message);
    }

    @Override
    public void run() {
        this.message.setTimeDomainData(this.modulateData());
        this.transmitterCallback();
    }
}
