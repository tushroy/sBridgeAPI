package ch.nych.soundtransmitter.transmitter;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ch.nych.BridgeListener;
import ch.nych.soundtransmitter.transmitter.tasks.Message;
import ch.nych.soundtransmitter.transmitter.tasks.preparation.PreparationTask;
import ch.nych.soundtransmitter.transmitter.tasks.TransmissionTask;
import ch.nych.soundtransmitter.transmitter.tasks.modulation.tone.Tone;
import ch.nych.soundtransmitter.transmitter.tasks.modulation.tone.ToneFactory;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/6/16.
 */
public class Transmitter {
    /**
     * Local log tag
     */
    private final String logTag = Configuration.LOG_TAG;

    /**
     * Indicates if the Transmitter is ready
     */
    private boolean initialized = false;

    /**
     * The Configuration instance is shared between all tasks
     */
    private Configuration configuration = null;

    /**
     * The toneSet contains the different tones for the signal modulation
     */
    private Tone[] toneSet = null;

    /**
     * The {@link AudioTrack} instances is used for the sending / playing of the modulated tone
     * sequences.
     */
    private AudioTrack audioTrack = null;

    /**
     * Responsible for the parallel execution of the different tasks
     */
    private ExecutorService[] executorServices = null;


    /**
     * TODO: 4/24/16 comment
     */
    private final List<BridgeListener> bridgeListeners = new ArrayList<BridgeListener>();

    /**
     * Getter for the local {@link Configuration} instance. Be aware of changing the configuration
     * while the transmitter is running. You will need to shutdown and reinitialize the transmitter.
     * @return the {@link Configuration} instance
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * Getter for the local tone set. The {@link Tone} objects are internally used for the signal
     * modulation and its not recommended to access them.
     * @return a {@link Tone} array
     */
    public Tone[] getToneSet() {
        return this.toneSet;
    }

    /**
     * Getter for the local {@link AudioTrack} instance. This object is used internally for the
     * transmission / playing of the tone sequences. It's not ment to be accessed from the outside
     * of the module.
     * @return the {@link AudioTrack} instance
     */
    public AudioTrack getAudioTrack() {
        return this.audioTrack;
    }

    /**
     *
     * @param bridgeListener
     */
    public void addListener(final BridgeListener bridgeListener) {
        this.bridgeListeners.add(bridgeListener);
    }

    /**
     * This method tries to initialize all required components of the {@link Transmitter} module.
     * Therefore a {@link Configuration} instance is needed to load the corresponding preferences
     * for the signal modulation.
     * @param configuration Unless its necessary for a specific reason, try to use a standard
     *                      configuration, generated by
     *                      Configuration.getNew<Definition>Configuration()
     * @return The method returns true if all components could be initialized successfully. If this
     * is not the case, the {@link Configuration} instance could be corrupted. If the argument is
     * null, the method will return false.
     */
    public boolean initTransmitter(Configuration configuration) {

        Log.i(this.logTag, "Initialize Transmitter");

        if(configuration == null) {
            Log.e(this.logTag, "Invalid Configuration, Transmitter is not ready");
            return false;
        } else {
            this.configuration = configuration;
        }

        this.toneSet = ToneFactory.getToneSet(this.configuration);
        int minBufferSize = AudioTrack.getMinBufferSize(
                this.configuration.getSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        this.audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                this.configuration.getSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize,
                AudioTrack.MODE_STREAM);
        if(audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            return false;
        }
        this.executorServices = new ExecutorService[]{
                Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor()
        };
        this.initialized = true;
        return true;
    }

    /**
     * The transmitData() method is broadcasting the data over sound waves. The transmission rate
     * is low, so don't try to send huge amounts of data.
     * @param data a simple byte array containing your data which might be some ascii values as an
     *             example.
     * @return the reference to the {@link Message} instance containing your data. This allows you
     * to query the status of the message and more. If the transmitter is not initialized or the
     * argument is null, the method will return null.
     */
    public Message transmitData(final byte[] data) {
        Message message = null;
        if(!this.initialized) {
            Log.w(this.logTag, "Transmitter not initialized. Couldn't transmit data");
        } else if(data != null && data.length > 0) {
            message = new Message(data);
            this.executorServices[0].execute(new PreparationTask(this, message));
        } else {
            Log.w(this.logTag, "Invalid arguments on transmitData(), could not transmit message");
        }
        return message;
    }

    /**
     * This is the callback() method for the single tasks. The abstract {@link TransmissionTask}
     * class always return another transmission task. The callback() method is responsible for the
     * control of the single transmission jobs. This allows to easily exceed the process of data
     * transmission with another task.
     * @param task the next task to execute
     */
    public void callback(TransmissionTask task) {
        if(task.getTaskType() == TransmissionTask.MODULATION_TASK) {
            this.executorServices[1].execute(task);
        } else if(task.getTaskType() == TransmissionTask.SENDING_TASK) {
            this.executorServices[2].execute(task);
        } else if(task.getTaskType() == TransmissionTask.NOTIFICATION_TASK){
            this.notifyBridgeListeners(task.getMessage());
        }
    }

    private void notifyBridgeListeners(final Message message) {
        for(BridgeListener bridgeListener : this.bridgeListeners) {
            bridgeListener.messageSent(message);
        }
    }

    /**
     * This method tries to shutdown all tasks and await their termination. This is an important
     * call, as the receiver is using native audio resources. Always stop the transmitter instance
     * before shutting down your application!
     */
    public void shutdownAndAwaitTermination() {
        Log.i(this.logTag, "Try to shutdown Transmitter");
        for(ExecutorService executor : this.executorServices) {
            Log.d(this.logTag, "Shutdown executor");
            executor.shutdown();
            try {
                if(!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    if(!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        Log.e(this.logTag, "Executor did not terminate");
                    }
                }
            } catch (InterruptedException e) {
                Log.w(this.logTag, e.getMessage());
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
