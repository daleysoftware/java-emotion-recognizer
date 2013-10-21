package erprj.app;

import erprj.nn.*;

public class NeuralNetworkExecutor
{
    public enum Type
    {
        BACKPROP, KOHONEN
    }

    private Type m_type;
    protected INeuralNetwork m_nn;
    protected int m_numNodes;
    protected int m_numFeatures;

    NeuralNetworkExecutor(String nntype, int numNodes, int numFeatures)
        throws NeuralNetworkTypeException
    {
        m_numNodes = numNodes;
        m_numFeatures = numFeatures;

        if (nntype.equals("backprop"))
        {
            m_type = Type.BACKPROP;
        }
        else if (nntype.equals("kohonen"))
        {
            m_type = Type.KOHONEN;
        }
        else
        {
            throw new NeuralNetworkTypeException("unknown network type.");
        }

        ResetNN();
    }

    protected int numNodes()
    {
        return m_numNodes;
    }

    protected int numFeatures()
    {
        return m_numFeatures;
    }

    protected void ResetNN()
        throws NeuralNetworkTypeException
    {
        switch (type())
        {
            case BACKPROP:
                m_nn = new NeuralNetworkBackPropagation();
                break;
            case KOHONEN:
                m_nn = new NeuralNetworkKohonen();
                break;
            default:
                throw new NeuralNetworkTypeException("unknown network type.");
        }
    }

    protected Type type()
    {
        return m_type;
    }
}
