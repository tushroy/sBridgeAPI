package ch.nych.soundtransmitter.receiver.tasks;

import java.util.Arrays;

import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/20/16.
 */
public class Frame {
    public final static int IN_PROGRESS = 0;
    public final static int ANALYZED_SUCCESSFULLY = 1;
    public final static int FRAME_CORRUPTED = -1;

    private int state = 0;

    /**
     * Number of rows in the original data. This is used to for the frame sealing as well as for the
     * the gap in front of the rows. The first two rows of processedData are initialized with
     * zeroes. The reason for this is that the absorbEnergy() method of
     * {@link ch.nych.soundtransmitter.receiver.tasks.analyzation.AnalyzationTask} doesn't have to
     * check against the the data border.
     */
    private int index = 2;

    /**
     * The raw unprocessed data (magnitudes of the different frequencies). These data should never
     * be changed.
     */
    private double[][] originalData = null;

    /**
     * The processed data are reduced on the frequency peaks.
     */
    private double[][] processedData = null;

    /**
     *
     * @return
     */
    public int getState() {
        return this.state;
    }

    /**
     * The frame class represent a container class for the recorded frame data. It contains the
     * original magnitude values, the data reduced on its peaks and the mapped bytes.
     * @param configuration
     */
    public Frame(final Configuration configuration) {
        this.originalData =
                new double[configuration.getTransmissionMode()][configuration.getMaxFrameSize()];
    }

    /**
     * Getter for the original data set. This data are intended as read only and shouldn't be
     * changed.
     * @return The raw magnitudes of the different frequencies. Each column represents a frequency,
     * whereas each raw represents the magnitude over time. orginialData[Frequency][Magnitudes]
     */
    public double[][] getOriginalData() {
        return this.originalData;
    }

    /**
     * Getter for the processed data set. This data are intended as read only and shouldn't be
     * changed. This data are reduced to the frequency peaks.
     * @return The peak magnitudes of the different frequencies. Each column represents a frequency,
     * whereas each raw contains the magnitude peaks over time. orginialData[Frequency][Magnitudes]
     */
    public double[][] getProcessedData() {
        return this.processedData;
    }

    /**
     * This method adds a set of magnitudes to the original data set. As the frame has a maximum
     * size (Can be set in the {@link Configuration} class), the set is only added if there is
     * space left.
     * @param dataSet a double array of magnitudes. The length of this array needs to be equal to
     *                the configured number of frequencies (see {@link Configuration}
     *                transmissionMode).
     */
    public void addDataSet(final double[] dataSet) {
        if(index < this.originalData.length) {
            for(int i = 0; i < dataSet.length; i++) {
                this.originalData[i][index] = dataSet[i];
            }
            index++;
        }
    }

    /**
     * The orginalData are reduced to the added magnitudes. From this reduced data a copy is done,
     * so the processing of the data in processedData don't affect the originalData.
     */
    public void sealFrame() {
        this.processedData = new double[originalData.length][];
        if(this.index > 0) {
            for(int i = 0; i < this.originalData.length; i++) {
                /*
                  The additional space at the end is necessary, so the edges don't have to be
                  checked all the time in AnalyzationTask.absorbEnergy()
                 */
                this.originalData[i] = Arrays.copyOf(this.originalData[i], index + 2);
                this.processedData[i] = Arrays.copyOf(this.originalData[i], this.originalData[i].length);
            }
        }
    }
}
