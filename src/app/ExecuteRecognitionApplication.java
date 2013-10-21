package erprj.app;

import erprj.db.*;
import erprj.nn.*;
import erprj.dro.*;

import java.io.*;
import java.util.*;

public class ExecuteRecognitionApplication
    extends NeuralNetworkExecutor
    implements ISubApplication
{
    protected String m_trainFile;
    protected String m_testFile;

    ExecuteRecognitionApplication(String nntype, String trainFile,
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
        Vector<DatabaseParser> trainvec = new Vector<DatabaseParser>();
        Vector<DatabaseParser> testvec = new Vector<DatabaseParser>();
        int numEmotions = 0;

        // Grab the training vectors.
        FileInputStream fs1 = new FileInputStream(m_trainFile);
        DataInputStream in1 = new DataInputStream(fs1);
        BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));

        String strLine;
        while ((strLine = br1.readLine()) != null)
        {
            DatabaseParser parser = new DatabaseParser(strLine, numFeatures());
            trainvec.add(parser);

            if (parser.emotionType() > numEmotions)
            {
                numEmotions = parser.emotionType();
            }
        }

        fs1.close();

        // And now the testing ones.
        FileInputStream fs2 = new FileInputStream(m_testFile);
        DataInputStream in2 = new DataInputStream(fs2);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));

        while ((strLine = br2.readLine()) != null)
        {
            DatabaseParser parser = new DatabaseParser(strLine, numFeatures());
            testvec.add(parser);
        }

        fs2.close();

        // Perform PCA. Updates the reduced array in all the dro objects.
        Vector<DimensionReducedObject> drovec =
            new Vector<DimensionReducedObject>();

        for (int i = 0; i < trainvec.size(); i++)
        {
            drovec.add(trainvec.get(i).dimReduced());
        }

        for (int i = 0; i < testvec.size(); i++)
        {
            drovec.add(testvec.get(i).dimReduced());
        }

        int numFeatures = DimensionReducedObject.prinCompAnalysis(drovec);

        // Configure.
        m_nn.configure(trainvec.size(), numFeatures, numEmotions+1, numNodes());
        m_nn.postConfigure();

        for (int i = 0; i < trainvec.size(); i++)
        {
            DatabaseParser parser = trainvec.get(i);
            m_nn.train(parser.dimReduced().getReducedArray(),
                    parser.emotionType());
        }

        // Only finish training once all the dro's have been reduced.
        m_nn.finishTraining();

        // And finally perform the testing.
        for (int i = 0; i < testvec.size(); i++)
        {
            DatabaseParser parser = testvec.get(i);
            FormatResult res = m_nn.test(parser.dimReduced().getReducedArray());

            // And print the result for the user to collect.
            System.out.println(
                    parser.fileID() + "," +
                    parser.emotionType() + "," + 
                    res.toString());
        }

        return true;
    }
}
