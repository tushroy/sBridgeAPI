package ch.nych.soundtransmitter.transmitter.tasks.sending;

import android.util.Log;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.tasks.Message;
import ch.nych.soundtransmitter.transmitter.tasks.TransmissionTask;
import ch.nych.soundtransmitter.transmitter.tasks.modulation.tone.Tone;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/6/16.
 */
public class SendingTask extends TransmissionTask {
    /**
     *
     */
    private final String logTag = Configuration.LOG_TAG;

    /**
     *
     * @param transmitter
     * @param message
     */
    public SendingTask(final Transmitter transmitter, final Message message) {
        super(transmitter, message, TransmissionTask.SENDING_TASK);
    }

    @Override
    protected TransmissionTask getNextTask() {
        return null;
    }

    @Override
    public void run() {
        Log.d("MyTag", "sending tone sequence");
        int samplesSent = 0;
        try {
            this.transmitter.getAudioTrack().play();
            this.message.setState(Message.STATE_SENDING);
            for(Tone tone : this.message.getToneSequence(true)) {
                Log.d(this.logTag, "Sending tone: " + tone.getFrequency());
                samplesSent += this.transmitter.getAudioTrack().write(
                        tone.getSamples(),
                        0,
                        (int) tone.getLength());
            }
        } catch (IllegalStateException e) {
            Log.e(this.logTag, e.getMessage());
            this.message.setState(Message.STATE_ABORT);
        }
        this.message.setState(Message.STATE_SENT);
        this.transmitter.getAudioTrack().stop();
        Log.i("MyTag", "sent a total of " + samplesSent + " samples");
    }
}
