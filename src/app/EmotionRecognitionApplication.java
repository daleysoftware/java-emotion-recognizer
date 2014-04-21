package erprj.app;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.media.jai.*;
import javax.media.jai.widget.*;

import erprj.db.*;
import erprj.image.*;

/**
 * This is the main-class for the distributable jar. It can be invoked using
 * several different modes. All these modes are described by invoking the usage.
 * Usage can be invoked by running the jar with no arguments.
 */
public class EmotionRecognitionApplication
{
    // This gets rid of the warning for not using native acceleration.
    static
    {
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
    }

    /**
     * Usage function for the main-class of the jar
     */
    private static void usage()
    {
        System.out.println(
            "\nEmotion Recognition Application\n\n" +
            "Arguments:\n" +
            "<--fdet-<op>|--<app>-<type>-<nodes>-<feats>> <arg0> <arg1>\n\n" +
            " --fdet mode:\n" +
            "     When running in feature detect mode, the input is:\n" +
            "         arg0 = imageFile (PNG or JPEG format)\n" +
            "         arg1 = emotionType (int)\n" +
            "     The output is the formatted image feature, i.e. the DRO.\n" +
            "     The format is: <fileHash>,<emotionType>,x1,x2,...,xN\n\n" +
            "     The op (operation) can be one of full|mouth|edge, where\n" +
            "     full does face/edge detection, mouth applies rescaling\n" +
            "     and normalization filters, and edge does only edge\n" +
            "     detection.\n\n" +
            " --exec-<type> mode:\n" + 
            "     When running execute mode, the input is:\n" +
            "         arg0 = train.feat filename\n" +
            "         arg1 = test.feat filename\n" +
            "     Both files should contain 1 DRO signature per line.\n\n" +
            " --permute-<type> mode:\n" + 
            "     When running permuation mode, the input is:\n" +
            "         arg0 = train.feat filename\n" +
            "         arg1 = test.feat filename\n" +
            "     Both files should contain 1 DRO signature per line. These\n" +
            "     two training files will be combined into several in-memory\n" +
            "     sets Yk = {xi in X for all i in [1,N] and i != k}, for \n" +
            "     all k in [1,N]. Then DRO signature k will be evaluated\n" +
            "     given a neural network trained with Yk.\n\n" +
            " --check-<type> mode:\n" + 
            "     Same as permutation mode, except training is only \n" +
            "     applied once for all the data sets.\n\n" +
            "The <type> argument to the exec and permute modes specifies\n" +
            "the type of neural network to use. Options are:\n\n" +
            "     1. backprop (back propagation with one hidden layer)\n" +
            "     2. kohonen (Kohonen self organizing map)\n");
    }

    /**
     * Main entry point of the application
     */
    public static void main(String[] args)
        throws NeuralNetworkTypeException, FileNotFoundException,
               IOException, DatabaseParserException, ImageConversionException
    {
        if (args.length != 3)
        {
            usage();
            return;
        }

        ISubApplication sub;

        if (args[0].startsWith("--fdet"))
        {
            if (args[0].split("-").length != 4)
            {
                usage();
                return;
            }

            String op = args[0].split("-")[3];

            String imageFile = args[1];
            int emotionType = Integer.parseInt(args[2]);

            if (op.equals("mouth"))
            {
                sub = new FeatureDetectionMouthApplication(imageFile,
                        emotionType);
            }
            else if (op.equals("edge"))
            {
                sub = new FeatureDetectionEdgeApplication(imageFile,
                        emotionType);
            }
            else
            {
                sub = new FeatureDetectionFullApplication(imageFile,
                        emotionType);
            }
        }
        else if (args[0].startsWith("--exec") ||
                 args[0].startsWith("--permute") ||
                 args[0].startsWith("--check"))
        {
            String trainFile = args[1];
            String testFile = args[2];

            if (args[0].split("-").length != 6)
            {
                usage();
                return;
            }

            String nntype = args[0].split("-")[3];
            int nodes = Integer.parseInt(args[0].split("-")[4]);
            int features = Integer.parseInt(args[0].split("-")[5]);

            if (args[0].startsWith("--exec"))
            {
                sub = new ExecuteRecognitionApplication(nntype, trainFile,
                        testFile, nodes, features);
            }
            else if (args[0].startsWith("--permute"))
            {
                sub = new PermuteRecognitionApplication(nntype, trainFile,
                        testFile, nodes, features);
            }
            else
            {
                sub = new CheckRecognitionApplication(nntype, trainFile,
                        testFile, nodes, features);
            }
        }
        else
        {
            usage();
            return;
        }

        sub.run();
    }
}
