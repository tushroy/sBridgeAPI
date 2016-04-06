package ch.nych.soundtransmitter.transmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.nych.soundtransmitter.transmitter.message.Message;
import ch.nych.soundtransmitter.transmitter.tasks.TransmissionTask;
import ch.nych.soundtransmitter.transmitter.tone.SineTone;
import ch.nych.soundtransmitter.transmitter.tone.Tone;

/**
 * Created by nych on 4/6/16.
 */
public class Transmitter {
    public final static int TWO_STATE_TRANSMITTER = 2;
    public final static int FOUR_STATE_TRANSMITTER = 4;

    public int transmissionMode = 0;
    private int idPool = 0;
    private Tone[] toneSet = null;
    private Map<Integer, Message> messages = new HashMap<Integer, Message>();

    private ExecutorService task1 = Executors.newSingleThreadExecutor();
    private ExecutorService task2 = Executors.newSingleThreadExecutor();
    private ExecutorService task3 = Executors.newSingleThreadExecutor();

    public Transmitter(int states) {
        if(states == Transmitter.TWO_STATE_TRANSMITTER) {
            this.transmissionMode = Transmitter.TWO_STATE_TRANSMITTER;
        } else if(states == Transmitter.FOUR_STATE_TRANSMITTER) {
            this.transmissionMode = Transmitter.FOUR_STATE_TRANSMITTER;
        } else {
            //ToDo
        }
        this.initToneSet();
    }

    private void initToneSet() {
        if(this.transmissionMode == Transmitter.TWO_STATE_TRANSMITTER) {
            this.toneSet = new Tone[]{new SineTone(19800, 480, 48000, 1),
                    new SineTone(19900, 480, 48000, 1),
                    new SineTone(20000, 480, 48000, 1),
                    new SineTone(20100, 480, 48000, 1),
                    new SineTone(20200, 480, 48000, 1)};
        } else if(this.transmissionMode == Transmitter.FOUR_STATE_TRANSMITTER) {
            this.toneSet = new Tone[]{new SineTone(19600, 480, 48000, 1),
                    new SineTone(19700, 480, 48000, 1),
                    new SineTone(19800, 480, 48000, 1),
                    new SineTone(19900, 480, 48000, 1),
                    new SineTone(20000, 480, 48000, 1),
                    new SineTone(20100, 480, 48000, 1),
                    new SineTone(20200, 480, 48000, 1),
                    new SineTone(20300, 480, 48000, 1),
                    new SineTone(20400, 480, 48000, 1)};
        }
    }

    public int transmitData(byte[] data) {
        return 0;
    }

    public void callback(TransmissionTask task) {

    }

    public void shutdownAndAwaitTermination() {

    }

    private void shutdownExecutor(ExecutorService executor) {

    }

    public int getState(int id) {
        Message m = this.messages.get(id);
        if(m == null)
            return -1;
        return m.getState();
    }

    public Tone[] getToneSet() {
        return this.toneSet;
    }
}
