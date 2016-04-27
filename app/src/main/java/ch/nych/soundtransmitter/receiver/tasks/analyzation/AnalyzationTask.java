package ch.nych.soundtransmitter.receiver.tasks.analyzation;

import java.util.Arrays;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.Frame;
import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * The class inherits from ReceiverTask but is slightly different to the other receiver tasks. This
 * task is terminated when the work is done, whereas the Recording- and TransformationTask running
 * as long they are not terminated manually.
 */
public class AnalyzationTask extends ReceiverTask {

    /**
     * Local log tag
     */
    private final String logTag = Configuration.LOG_TAG;

    /**
     * The frame to analyze
     */
    private Frame frame = null;

    /**
     * Local reference to the originalData array of the frame
     */
    private double[][] originalData = null;

    /**
     * Local reference to the processedData array of the frame
     */
    private double[][] processedData = null;

    /**
     * This buffer is used for the calculation of the median
     */
    private double[] tempBuffer = null;

    /**
     * Standard constructor for the AnalyzationTask class.
     * @param receiver reference of the receiver that initialized the analyzation task. Is used for
     *                 the callback.
     * @param frame The frame to process.
     */
    public AnalyzationTask(final Receiver receiver, final Frame frame) {
        super(receiver);
        this.frame = frame;
        this.originalData = frame.getOriginalData();
        this.processedData = frame.getProcessedData();
        this.tempBuffer = new double[this.originalData[0].length];
    }

    /**
     * Calculated the threshold of the input data. The threshold is defined by the median of the
     * data set. As the data must be sorted for the median, a copy is done into the local
     * tempBuffer.
     * @param data the magnitudes of a frequency (column of the processedData)
     * @return the threshold (median) of the magnitudes
     */
    private double getThreshold(final double[] data) {
        double threshold = 0;
        System.arraycopy(data, 0, this.tempBuffer, 0, data.length);
        Arrays.sort(this.tempBuffer);
        int m = this.tempBuffer.length / 2;
        if(this.tempBuffer.length % 2 == 0) {
            threshold = this.tempBuffer[m];
            threshold += this.tempBuffer[m - 1];
            threshold /= 2.0;
        } else {
            threshold = this.tempBuffer[m];
        }
        return threshold;
    }

    /**
     * Search for the maximum magnitude in the column (frequency) passed in data. The detected local
     * maximum needs to be bigger than the threshold.
     * @param data the magnitudes of a frequency over time (column of processedData)
     * @param threshold the calculated threshold for the frequency to process
     * @return the index of the maximum magnitude above the threshold or -1 if no more values are
     * available.
     */
    private int getMaxInColumn(final double[] data, final double threshold) {
        int maxIndex = 0;
        for(int i = 0; i < data.length; i++) {
            if(data[i] > data[maxIndex]) {
                maxIndex = i;
            }
        }
        if(data[maxIndex] < threshold) {
            maxIndex = -1;
        }
        return maxIndex;
    }

    /**
     * Checks if the maximum magnitude of a column (frequency) is also the maximum value in the row.
     * This means that the maximum for a frequency (columnIndex) at a given time (rowIndex) is also
     * the maximum magnitude compared to all other analyzed frequencies.
     * @param columnIndex the magnitude set of a specific frequency (column of processedData)
     * @param rowIndex the index of the possible maximum magnitude (row of processedData)
     * @return true if the maximum magnitude in the column is also the maximum value in the whole
     * row, false if another frequency's magnitude is higher
     */
    private boolean isMaxInRow(final int columnIndex, final int rowIndex) {
        for(int i = 0; i < this.originalData.length; i++) {
            if(this.originalData[columnIndex][rowIndex] < this.originalData[i][rowIndex]) {
                return false;
            }
        }
        return true;
    }

    /**
     * If a maximum magnitude is found, this method is used to "absorb" the energy of all other
     * frequencies at the given point in time (rowIndex). Besides the energy of the other,
     * frequencies the peak also absorbs the magnitudes before and after its occurrence (the edges
     * of the detected peak). This values are set to zero and the peak is negated. During this
     * process, only the values of the column at columnIndex (the frequency) are manipulated. All
     * changes are done in processedData.
     * @param columnIndex the frequency where the maximum was detected
     * @param rowIndex the index of the maximum magnitude
     */
    private void absorbEnergy(final int columnIndex, final int rowIndex) {
        for(int i = 0; i < this.originalData.length; i++) {
            this.processedData[columnIndex][rowIndex] += this.originalData[i][rowIndex];
        }
        this.processedData[columnIndex][rowIndex] += this.originalData[columnIndex][rowIndex - 1];
        this.processedData[columnIndex][rowIndex] += this.originalData[columnIndex][rowIndex - 2];
        this.processedData[columnIndex][rowIndex - 1] = 0.0;
        this.processedData[columnIndex][rowIndex - 2] = 0.0;
        this.processedData[columnIndex][rowIndex] += this.originalData[columnIndex][rowIndex + 1];
        this.processedData[columnIndex][rowIndex] += this.originalData[columnIndex][rowIndex + 2];
        this.processedData[columnIndex][rowIndex + 1] = 0.0;
        this.processedData[columnIndex][rowIndex + 2] = 0.0;
        this.processedData[columnIndex][rowIndex] = -this.processedData[columnIndex][rowIndex];
    }

    /**
     * In Progress
     */
    private void processFrame() {
        double threshold = 0.0;
        int index = 0;

        for(int i = 0; i < this.processedData.length; i++) {
            threshold = this.getThreshold(this.processedData[i]);
            while((index = this.getMaxInColumn(this.processedData[i], threshold)) > 0) {
                if(this.isMaxInRow(i, index)) {
                    this.absorbEnergy(i, index);
                } else {
                    this.processedData[i][index] = 0.0;
                    this.processedData[i][index - 1] = 0.0;
                    this.processedData[i][index + 1] = 0.0;
                }
            }
        }

        for(int i = 0; i < this.processedData.length; i++) {
            for(int j = 0; j < this.processedData[i].length; j++) {
                if(this.processedData[i][j] >= 0.0) {
                    this.processedData[i][j] = 0.0;
                } else {
                    this.processedData[i][j] = -this.processedData[i][j];
                }
            }
        }
    }


    @Override
    public boolean initTask() {
        return true;
    }

    @Override
    public void run() {
        this.processFrame();
        this.receiver.callback(this.frame);
    }
}
