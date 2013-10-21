package erprj.nn;

import java.util.*;
import org.encog.mathutil.matrices.*;
import org.encog.ml.data.*;
import org.encog.ml.data.basic.*;
import org.encog.neural.som.*;
import org.encog.neural.som.training.basic.*;
import org.encog.neural.som.training.basic.neighborhood.*;
import org.encog.mathutil.rbf.*;
import org.encog.neural.data.*;
import org.encog.util.arrayutil.*;
import org.encog.util.normalize.target.*;
import org.encog.util.normalize.*;
import org.encog.util.normalize.input.*;
import org.encog.util.normalize.output.*;

public class NeuralNetworkKohonen
    extends NeuralNetworkParent
    implements INeuralNetwork
{
    // Map cluster
    private HashMap m_clusterMap;

    // SOM network
    SOM m_network;

    public void postConfigure()
    {
        m_trainingVectors = new double[numTestVecs()][numFeatures()];
        m_emotions = new double[numTestVecs()][numEmotions()];
        m_trainVecCounter = 0;
    }

    public void finishTraining()
    {
        MLDataSet training = new BasicMLDataSet(m_trainingVectors, null);
        m_network = new SOM(numFeatures(), numEmotions());
        m_network.reset();

        BasicTrainSOM train =
            new BasicTrainSOM(m_network, 0.7, training,
                    new NeighborhoodSingle());
                    //new NeighborhoodBubble(numFeatures()/2));
        train.setForceWinner(false);

        int epoch = 1;
        do
        {
            // Decrease Learning rate according to Kohonen
            double learningRate = 0.7*(1 - epoch/m_maxEpochs);
            //double radius = numEmotions()*(1 - epoch/m_maxEpochs);
            double radius = numFeatures() * Math.pow(Math.E,-(double)epoch/m_maxEpochs);
            //train.setLearningRate(learningRate);
            train.setParams(learningRate,radius);

            train.iteration();
            epoch++;
        }
        while ((train.getError() > m_desiredError && epoch < m_maxEpochs) ||
                epoch < m_minEpochs);

        int clusterGenerator[][] = new int[numEmotions()][numEmotions()];

        // Map results to actual emotion.
        m_clusterMap = new HashMap();
        for (int i = 0; i < m_trainVecCounter; i++)
        {
            BasicMLData input = new BasicMLData(m_trainingVectors[i]);
            int result = m_network.winner(input);

            int realEmotion = 0;
            for (int j = 0; j < numEmotions();j++)
            {
                if (m_emotions[i][j] == m_emotionHigh)
                {
                    realEmotion = j;
                }
            }

            // Used to generate the realEmotion->resultIndex mapping.
            clusterGenerator[realEmotion][result]++;
        }

        // Create the cluster mapping using the cluster generator.
        for (int i = 0; i < numEmotions(); i++)
        {
            int maxJ = -1;
            int maxK = -1;
            int max = -1;

            // Find the max value in the whole 2D array.
            for (int j = 0; j < numEmotions(); j++)
            {
                for (int k = 0; k < numEmotions(); k++)
                {
                    if (clusterGenerator[j][k] > max)
                    {
                        maxJ = j;
                        maxK = k;
                        max = clusterGenerator[j][k];
                    }
                }
            }

            // Zero out that row and column.
            for (int j = 0; j < numEmotions(); j++)
            {
                clusterGenerator[maxJ][j] = -1;
            }
            for (int j = 0; j < numEmotions(); j++)
            {
                clusterGenerator[j][maxK] = -1;
            }

            // Set the cluster entry.
            m_clusterMap.put(maxJ, maxK);
        }
    }

    public FormatResult test(float[] reduced)
    {
        double[] inputArray = new double[reduced.length];
        for (int i = 0; i < reduced.length; i++)
        {
            inputArray[i] = (double) reduced[i];
        }

        MLData output = m_network.compute(new BasicMLData(inputArray));
        FormatResult format = new FormatResult(numEmotions());

        // Need to normalize the output.
        float[] normal = new float[output.size()];
        float min = 0.0f;
        float max = 0.0f;

        for (int i = 0; i < output.size(); i++)
        {
            int resultIndex = (Integer) m_clusterMap.get(i);
            float data = (float) output.getData(resultIndex);

            if (data < min)
            {
                min = data;
            }
            if (data > max)
            {
                max = data;
            }

            normal[i] = data;
        }

        float absmax = Math.abs(min) + max;
        float frac = 1.0f/absmax;

        for (int i = 0; i < numEmotions(); i++)
        {
            float val = ((normal[i]+Math.abs(min))*frac);
            format.set(i, val);
        }

        return format;
    }
}
