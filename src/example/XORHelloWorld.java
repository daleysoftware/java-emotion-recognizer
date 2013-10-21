package erprj.examples;

import org.encog.Encog;
import org.encog.engine.network.activation.*;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.error.ATanErrorFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class XORHelloWorld
{
	public static double XOR_INPUT[][] =
    {
        { 1.0, 1.0 }, { -1.0, 1.0 }, { 1.0, -1.0 }, { -1.0, -1.0 }
    };

	public static double XOR_IDEAL[][] =
    {
        { 1.0 }, { -1.0 }, { -1.0 }, { 1.0 }
    };

	public static void main(final String args[])
    {
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,2));
		network.addLayer(new BasicLayer(new ActivationTANH(),true,3));
		network.addLayer(new BasicLayer(new ActivationTANH(),false,1));
		network.getStructure().finalizeStructure();
		network.reset();

		MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);
		
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

		int epoch = 1;
        int min = 10000;
        int max = 1000000;

		do
        {
			train.iteration();

            if (epoch % 100 == 0)
            {
			    System.out.println(
                        "Epoch #" + epoch + " Error:" + train.getError());
            }

			epoch++;
		}
        while ((train.getError() > 0.00001 || epoch < min) && epoch < max);

		System.out.println("Neural Network Results:");
		for(MLDataPair pair: trainingSet )
        {
			final MLData output = network.compute(pair.getInput());

			System.out.println(
                    pair.getInput().getData(0) + "," +
                    pair.getInput().getData(1) + ", actual=" +
                    output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
		}

		Encog.getInstance().shutdown();
	}
}
