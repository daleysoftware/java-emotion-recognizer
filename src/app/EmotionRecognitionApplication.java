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
 * This is the main-class for the distributable jar.
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
            "Emotion Recognition Application\n\n" +
            "Usage: <mode> <args...>\n\n" +
            "Mode 1: fdet\n" +
            "    Feature detection mode. When running in this mode, the args are:\n" +
            "        arg0 = operation\n" +
            "        arg1 = image_file (PNG or JPEG format)\n" +
            "        arg2 = emotion_type (int)\n" +
            "     The output format is: <file_hash>,<emotion_type>,x1,x2,...,xN\n\n" +
            "     The operation can be one of full|mouth|edge, where full does face\n" +
            "     detection, mouth applies rescaling and normalization filters,\n" +
            "     and edge does only edge detection.\n\n" +
            "Mode 2: recognize\n" +
            "     Recognition mode. When running in this mode, the args are:\n" +
            "        arg0 = type (backprop or kohonen)\n" +
            "        arg1 = num_network_nodes (int)\n" +
            "        arg2 = num_features (int)\n" +
            "        arg3 = the name of the training file\n" +
            "        arg4 = the name of the testing file\n" +
            "     The output format is: <file_hash>,<emotion_type>,p1,p2,...,pN");
    }

    /**
     * Main entry point of the application
     */
    public static void main(String[] args)
        throws NeuralNetworkTypeException, FileNotFoundException,
               IOException, DatabaseParserException, ImageConversionException
    {
        if (args.length < 1)
        {
            usage();
            return;
        }

        ISubApplication sub;

        if (args[0].equals("fdet"))
        {
            if (args.length != 4)
            {
                usage();
                return;
            }

            String op = args[1];
            String imageFile = args[2];
            int emotionType = Integer.parseInt(args[3]);

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
        else if (args[0].equals("recognize"))
        {
            if (args.length != 6)
            {
                usage();
                return;
            }
            
            String nntype = args[1];
            int nodes = Integer.parseInt(args[2]);
            int features = Integer.parseInt(args[3]);
            String trainFile = args[4];
            String testFile = args[5];

            sub = new ExecuteRecognitionApplication(nntype, trainFile,
                    testFile, nodes, features);
        }
        else
        {
            usage();
            return;
        }

        sub.run();
    }
}
