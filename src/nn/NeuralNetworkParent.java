package erprj.nn;

public class NeuralNetworkParent
{
    // Configure params
    protected int m_numTestVecs;
    protected int m_numFeatures;
    protected int m_numEmotions;
    protected int m_numNodes;

    // Cache of the formatted training data.
    protected double[][] m_trainingVectors;
    protected double[][] m_emotions;

    // Counter for the current training vector.
    protected int m_trainVecCounter;
 
    // Corresponds to the activation function we are using.
    protected static final double m_emotionLow = -1.0;
    protected static final double m_emotionHigh = 1.0;

    // Training params.
    protected static final int m_maxEpochs = 10000;
    protected static final int m_minEpochs = 1000;
    protected static final double m_desiredError = 0.00001;
 
    public void configure(int numTestVecs, int numFeatures, int numEmotions,
            int numNodes)
    {
        m_numTestVecs = numTestVecs;
        m_numFeatures = numFeatures;
        m_numEmotions = numEmotions;
        m_numNodes = numNodes;
    }

    public void train(float[] reduced, int emotionType)
    {
        // Re-format the input data for this specific API.
        for (int i = 0; i < numFeatures(); i++)
        {
            m_trainingVectors[m_trainVecCounter][i] = reduced[i];
        }

        for (int i = 0; i < numEmotions(); i++)
        {
            m_emotions[m_trainVecCounter][i] = m_emotionLow;

            if (i == emotionType)
            {
                m_emotions[m_trainVecCounter][i] = m_emotionHigh;
            }
        }

        m_trainVecCounter++;
    }

    public String toString()
    {
        if (numTestVecs() == 0)
        {
            return "";
        }

        String result = new String();
        for (int i = 0; i < numTestVecs(); i++)
        {
            result += "{";
            for (int j = 0; j < numFeatures(); j++)
            {
                result += m_trainingVectors[i][j] + ", ";
            }

            result += "\b\b} => {";
            for (int j = 0; j < numEmotions(); j++)
            {
                result += m_emotions[i][j] + ", ";
            }
            result += "\b\b}";

            if (i+1 < numTestVecs())
            {
                result += "\n";
            }
        }
        return result;
    }
    
    public int numTestVecs()
    {
        return m_numTestVecs;
    }

    public int numFeatures()
    {
        return m_numFeatures;
    }

    public int numEmotions()
    {
        return m_numEmotions;
    }

    public int numNodes()
    {
        return m_numNodes;
    }
}
