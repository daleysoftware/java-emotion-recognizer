package erprj.app;

import java.io.*;

import erprj.db.*;
import erprj.image.*;

public interface ISubApplication
{
    public boolean run()
        throws FileNotFoundException, IOException, DatabaseParserException,
               NeuralNetworkTypeException, ImageConversionException;
}
