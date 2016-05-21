package ch.nych.soundtransceiver.transmitter;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ch.nych.TransmitterListener;
import ch.nych.soundtransceiver.util.Message;
import ch.nych.soundtransceiver.transmitter.tasks.TransmissionTask;
import ch.nych.soundtransceiver.transmitter.tasks.modulation.ModulationTask;
import ch.nych.soundtransceiver.transmitter.tasks.modulation.SingleChannelModulationTask;
import ch.nych.soundtransceiver.transmitter.tasks.modulation.ThreeChannelModulation;
import ch.nych.soundtransceiver.transmitter.tasks.modulation.TwoChannelModulationTask;
import ch.nych.soundtransceiver.transmitter.tasks.modulation.tone.Tone;
import ch.nych.soundtransceiver.transmitter.tasks.modulation.tone.ToneFactory;
import ch.nych.soundtransceiver.util.Configuration;

/**
 * This class is the sending interface of the sBridgeAPI. It is responsible
 * for the modulation and  sending of the data bytes passed in transmitData().
 * The class offers the possibility to register yourself as a listener for
 * transmission events. If you registered yourself with the addListener()
 * method, you will receive a notification if the message was sent. Before
 * messages can be sent, the transmitter needs to be initialized successfully
 * with initTransmitter(). If this is not possible, there might be a fault in
 * the {@link Configuration}
 * or the {@link AudioTrack} can't be initialized.
 *
 * Created by nych on 4/6/16.
 */
public class Transmitter implements Runnable {

    /**
     * Local log tag
     */
    private final static String LOG_TAG = Configuration.GLOBAL_LOG_TAG +
			":transmitter";

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
     * The {@link AudioTrack} instances is used for the sending / playing of
	 * the modulated tone
     * sequences.
     */
    private AudioTrack audioTrack = null;

    /**
     * Responsible for the parallel execution of the different tasks
     */
    private ExecutorService[] executorServices = null;


    /**
     * Stores all the registered listeners
     */
    private final List<TransmitterListener> transmitterListeners = new
            ArrayList<TransmitterListener>();

    /**
     * Getter for the local {@link Configuration} instance. Be aware of
	 * changing the configuration while the transmitter is running. You will
	 * need to shutdown and reinitialize the transmitter.
     * @return the {@link Configuration} instance
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * Getter for the local tone set. The {@link Tone} objects are internally
	 * used for the signal modulation and its not recommended to access them.
     * @return a {@link Tone} array
     */
    public Tone[] getToneSet() {
        return this.toneSet;
    }

    /**
     * Getter for the local {@link AudioTrack} instance. This object is used
	 * internally for the transmission / playing of the tone sequences. It's
	 * not ment to be accessed from the outside of the module.
     * @return the {@link AudioTrack} instance
     */
    public AudioTrack getAudioTrack() {
        return this.audioTrack;
    }

    /**
     * This method register a {@link TransmitterListener} instance for
	 * notifications.
     * @param  transmitterListener {@link TransmitterListener} instance
	 *                                to register
     */
    public void addTransmitterListener(
            final TransmitterListener transmitterListener) {
		this.transmitterListeners.add(transmitterListener);
    }

    /**
     * This method tries to initialize all required components of the
	 * {@link Transmitter} module. Therefore a {@link Configuration} instance
	 * is needed to load the corresponding preferences for the signal
	 * modulation.
     * @param configuration Unless its necessary for a specific reason, try
	 *                         to use a standard configuration, generated by
	 *                         Configuration.getNew<Definition>Configuration()
     * @return The method returns true if all components could be initialized
	 * successfully. If this is not the case, the {@link Configuration}
	 * instance could be corrupted. If the argument is null, the method will
	 * return false.
     */
    public boolean initTransmitter(final Configuration configuration) {
        Log.i(Transmitter.LOG_TAG, "Initialize Transmitter");

        if(configuration == null) {
            Log.e(Transmitter.LOG_TAG, "Invalid Configuration, Transmitter is" +
					" not ready");
            return false;
        } else {
            this.configuration = configuration;
        }

        this.toneSet = ToneFactory.getToneSet(this.configuration);
        int minBufferSize = AudioTrack.getMinBufferSize(
                this.configuration.getSampleRate().getSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        this.audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                this.configuration.getSampleRate().getSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize,
                AudioTrack.MODE_STREAM);
        if(audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            return false;
        }
        this.executorServices = new ExecutorService[]{
                Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor()
        };
        this.initialized = true;
        return true;
    }

	/**
	 *
	 */
	private void exitTransmitter() {
		this.initialized = false;
		this.toneSet = null;
		this.audioTrack = null;
		this.executorServices = null;
		this.transmitterListeners.clear();
	}

    /**
     * The transmitData() method is broadcasting the data over sound waves.
	 * The transmission rate is low, so don't try to send huge amounts of data.
     * @param data a simple byte array containing your data which might be
	 *                some ascii values as an example.
     * @return the reference to the {@link Message} instance containing your
	 * data. This allows you to query the status of the message and more. If
	 * the transmitter is not initialized or the  argument is null, the
	 * method will return null.
     */
    public Message transmitData(final byte[] data) {
        Message message = null;
        if(!this.initialized) {
            Log.w(Transmitter.LOG_TAG, "Transmitter not initialized. Couldn't" +
					" transmit data");
        } else if(data != null && data.length > 0) {
            message = new Message(data);
            ModulationTask modulationTask = null;
            if(this.configuration.getTransmissionMode() ==
                    Configuration.TransmissionMode.SINGLE_CHANNEL) {
                modulationTask = new SingleChannelModulationTask(this, message);
            } else if(this.configuration.getTransmissionMode() ==
                    Configuration.TransmissionMode.TWO_CHANNEL) {
                modulationTask = new TwoChannelModulationTask(this, message);
            } else if(this.configuration.getTransmissionMode() ==
                    Configuration.TransmissionMode.THREE_CHANNEL) {
                modulationTask = new ThreeChannelModulation(this, message);
            }
            this.executorServices[0].execute(modulationTask);
        } else {
            Log.w(Transmitter.LOG_TAG, "Invalid arguments on transmitData(), " +
					"could not transmit message");
        }
        return message;
    }

    /**
     * This is the callback() method for the single tasks. The abstract
	 * {@link TransmissionTask} class always return another transmission task.
	 * The callback() method is responsible for the control of the single
	 * transmission jobs. This allows to easily exceed the process of data
     * transmission with other tasks.
     * @param task    the next task to execute
     */
    public void callback(final TransmissionTask task) {
        if(task.getTaskType() == TransmissionTask.TaskType.SENDING) {
            this.executorServices[1].execute(task);
        } else if(task.getTaskType() == TransmissionTask.TaskType.NOTIFICATION){
            this.notifyTransmitterListeners(task.getMessage());
        }

    }

    /**
     * This method notifies all registered {@link TransmitterListener} about
	 * the state of the message transmission.
     * @param message    the sent {@link Message}
     */
    private void notifyTransmitterListeners(final Message message) {
		for(int i = 0; i < this.transmitterListeners.size(); i++) {
			this.transmitterListeners.get(i).messageSent(message);
		}
    }

    /**
     * This method tries to shutdown all tasks and await their termination.
	 * This  is an important call, as the receiver is using native audio
	 * resources. Always stop the transmitter instance before shutting down
	 * your application!
     */
    public void shutdownAndAwaitTermination() {
        Log.i(Transmitter.LOG_TAG, "Try to shutdown Transmitter");
        ExecutorService executorService = null;
        try {
            for(int i = 0; i < this.executorServices.length; i++ ) {
                Log.d(Transmitter.LOG_TAG, "Shutdown executor");
                executorService = this.executorServices[i];
                executorService.shutdown();
                if(!executorService.awaitTermination(60,
                        TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    if(!executorService.awaitTermination(60,
                            TimeUnit.SECONDS)) {
                        Log.e(Transmitter.LOG_TAG,
                                "Executor did not terminate");
                    }
                }
            }
        } catch(InterruptedException e) {
            Log.w(Transmitter.LOG_TAG, e.getMessage());
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            Log.d(Transmitter.LOG_TAG, "Release AudioTrack");
            this.audioTrack.release();
        }
    }

	@Override
	public void run() {
		Log.d(Transmitter.LOG_TAG, "Start Transmitter");
		try {
			while(true) {
				Thread.sleep(10);
			}
		} catch(InterruptedException e) {
			Log.d(Transmitter.LOG_TAG, "Thread interrupted");
		} finally {
			this.shutdownAndAwaitTermination();
			this.exitTransmitter();
			Log.d(Transmitter.LOG_TAG, "Shutdown Transmitter");
		}
	}
}
