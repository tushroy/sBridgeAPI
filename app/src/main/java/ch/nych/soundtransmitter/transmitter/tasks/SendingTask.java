package ch.nych.soundtransmitter.transmitter.tasks;

import android.util.Log;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.message.Message;
import ch.nych.soundtransmitter.transmitter.tone.Tone;

/**
 * Created by nych on 4/6/16.
 */
public class SendingTask extends TransmissionTask {

    public SendingTask(Transmitter transmitter, Message message) {
        super(transmitter, message, TransmissionTask.SENDING_TASK);
    }

    @Override
    protected TransmissionTask getNextTask() {
        return null;
    }

    @Override
    public void run() {
        Log.i("MyTag", "sending tone sequence");
        int samplesSent = 0;
        this.transmitter.getAudioTrack().play();
        for(Tone tone : this.message.getToneSequence()) {
            samplesSent += this.transmitter.getAudioTrack().write(tone.getSamples(), 0, tone.getLength());
        }
        this.message.setState(Message.STATE_SENT);
        this.transmitter.getAudioTrack().stop();
        Log.i("MyTag", "sent a total of " + samplesSent + " samples");
    }
}
