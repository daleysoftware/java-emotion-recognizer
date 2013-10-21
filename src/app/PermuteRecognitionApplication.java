package erprj.app;

import java.util.*;
import java.io.*;
import erprj.db.*;
import erprj.nn.*;
import erprj.dro.*;

public class PermuteRecognitionApplication
    extends NeuralNetworkExecutor
    implements ISubApplication
{
    // The number of times to run.
    private static final int m_numRepeat = 1;

    protected String m_trainFile;
    protected String m_testFile;

    PermuteRecognitionApplication(String nntype, String trainFile,
        String testFile, int numNodes, int numFeatures)
        throws NeuralNetworkTypeException
    {
        super(nntype, numNodes, numFeatures);

        m_trainFile = trainFile;
        m_testFile = testFile;
    }

    public boolean run()
        throws FileNotFoundException, IOException, DatabaseParserException,
               NeuralNetworkTypeException
    {
        Vector<DatabaseParser> vec = new Vector<DatabaseParser>();
        int numEmotions = 0;

        // Add the training entries to the parser vector.
        FileInputStream fs1 = new FileInputStream(m_trainFile);
        DataInputStream in1 = new DataInputStream(fs1);
        BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));

        String strLine;
        while ((strLine = br1.readLine()) != null)
        {
            DatabaseParser parser = new DatabaseParser(strLine, numFeatures());
            vec.add(parser);

            if (parser.emotionType() > numEmotions)
            {
                numEmotions = parser.emotionType();
            }
        }
        fs1.close();

        // Add the testing entries to the parser vector.
        FileInputStream fs2 = new FileInputStream(m_testFile);
        DataInputStream in2 = new DataInputStream(fs2);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));

        while ((strLine = br2.readLine()) != null)
        {
            DatabaseParser parser = new DatabaseParser(strLine, numFeatures());
            vec.add(parser);

            if (parser.emotionType() > numEmotions)
            {
                numEmotions = parser.emotionType();
            }
        }
        fs2.close();

        // Perform PCA. Updates the reduced array in all the dro objects.
        Vector<DimensionReducedObject> drovec =
            new Vector<DimensionReducedObject>();

        for (int i = 0; i < vec.size(); i++)
        {
            drovec.add(vec.get(i).dimReduced());
        }

        int numFeatures = DimensionReducedObject.prinCompAnalysis(drovec);

        // And now the testing phase. Exclude on parser vector entry each time
        // from training, and test on that particular entry.
        for (int i = 0; i < vec.size(); i++)
        {
            FormatResult res = new FormatResult(numEmotions+1);
            for (int j = 0; j < m_numRepeat; j++)
            {
                m_nn.configure(vec.size()-1, numFeatures, numEmotions+1,
                        numNodes());
                m_nn.postConfigure();

                for (int k = 0; k < vec.size(); k++)
                {
                    if (k != i)
                    {
                        DatabaseParser parser = vec.get(k);
                        m_nn.train(parser.dimReduced().getReducedArray(),
                                parser.emotionType());
                    }
                }

                m_nn.finishTraining();

                res.add(m_nn.test(vec.get(i).dimReduced().getReducedArray()));
                ResetNN();
            }

            res.divide(m_numRepeat);
            System.out.println(
                    vec.get(i).fileID() + "," +
                    vec.get(i).emotionType() + "," +
                    res.toString());
        }

        return true;
    }
}
