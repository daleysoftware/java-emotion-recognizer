package erprj.nn;

public interface INeuralNetwork
{
    // Handled by the parent class. Allow the neural network code to be dynamic
    // (useful for testing).
    public void configure(int numTestVecs, int numFeatures, int numEmotions,
            int numNodes);

    // Called right after configure. Can serve as a constructor of sorts.
    public void postConfigure();

    // Perform one training iteration given an array of features. The values
    // will not have a definitive max/min but will be centered around 0.0.
    //
    // emotionType will is a zero-based index less than numEmotions.
    public void train(float[] reduced, int emotionType);

    // Called when all training vectors have been passed. Do final training
    // steps here. (May not be required).
    public void finishTraining();

    // Perform one test, and return the formatted result. See FormatResult.java
    // for details.
    public FormatResult test(float[] reduced);
}
