package ch.nych.soundtransmitter.transmitter;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ch.nych.soundtransmitter.transmitter.message.Message;
import ch.nych.soundtransmitter.transmitter.tasks.PreparationTask;
import ch.nych.soundtransmitter.transmitter.tasks.TransmissionTask;
import ch.nych.soundtransmitter.transmitter.tone.SineTone;
import ch.nych.soundtransmitter.transmitter.tone.Tone;
import ch.nych.soundtransmitter.util.Config;

/**
 * Created by nych on 4/6/16.
 */
public class Transmitter {

    private Config config = null;
    private Tone[] toneSet = null;
    private ExecutorService[] executorServices = null;
    private AudioTrack audioTrack = null;

    public int initTransmitter(Config config) {
        if(config == null) {
            return -1;
        } else {
            this.config = config;
        }
        this.initToneSet();
        //init toneset
        //init exec
        //init audiotrack
        return 0;
    }
    private void initExecutors() {
        this.executorServices = new ExecutorService[]{
                Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor()
        };
    }

    private void initToneSet() {
        if(this.config.getTransmissionMode() == Config.TWO_STATE_TRANSMISSION) {
            Log.i("MyTag", "Init TWO_STATE_TRANSMITTER");
            this.toneSet = new Tone[]{new SineTone(19800, 480, 48000, 1),
                    new SineTone(19900, 480, 48000, 1),
                    new SineTone(20000, 480, 48000, 1),
                    new SineTone(20100, 480, 48000, 1),
                    new SineTone(20200, 480, 48000, 1)};
        } else if(this.config.getTransmissionMode() == Config.FOUR_STATE_TRANSMISSION) {
            Log.i("MyTag", "Init FOUR_STATE_TRANSMITTER");
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

    private void initAudioTrack() {
        int minBufferSize = AudioTrack.getMinBufferSize(this.config.getSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                this.config.getSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize,
                AudioTrack.MODE_STREAM);
        Log.d("MyTag", "minbuffersize: " + minBufferSize);
    }

    public Message transmitData(byte[] data) {
        Message message = null;
        if(data != null && data.length > 0) {
            message = new Message(data);
            this.executorServices[0].execute(new PreparationTask(this, message));
        }
        return message;
    }

    public void callback(TransmissionTask task) {
        if(task == null) {
            //Transmission done
        } else if(task.getTaskType() == TransmissionTask.MODULATION_TASK) {
            this.executorServices[1].execute(task);
        } else if(task.getTaskType() == TransmissionTask.SENDING_TASK) {
            this.executorServices[2].execute(task);
        } else {
            System.err.println("Take a look at the callback method");
        }
    }

    public void shutdownAndAwaitTermination() {
        System.out.println("Start shutdown transmitter");
        for(ExecutorService executor : this.executorServices) {
            this.shutdownExecutor(executor);
        }
        System.out.println("Transmitter down");
    }

    private void shutdownExecutor(ExecutorService executor) {
        System.out.println("Start shutdown executor");
        executor.shutdown();
        try {
            if(!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if(!executor.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Executor did not terminate");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Executor down");
    }

    public Tone[] getToneSet() {
        return this.toneSet;
    }

    public AudioTrack getAudioTrack() {
        return this.audioTrack;
    }

    public Config getConfig() {
        return this.config;
    }
}
