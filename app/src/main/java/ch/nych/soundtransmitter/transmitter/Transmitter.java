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
import ch.nych.soundtransmitter.transmitter.tone.ToneFactory;
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
        this.toneSet = ToneFactory.getToneSet(this.config);
        this.executorServices = new ExecutorService[]{
                Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor()
        };
        this.initAudioTrack();
        return 0;
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
            //// TODO: 4/12/16
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
