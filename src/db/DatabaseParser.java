package erprj.db;

import erprj.dro.*;

public class DatabaseParser
{
    String m_fileID;
    int m_emotionType;
    DimensionReducedObject m_dimReduced;

    public DatabaseParser(String meta, int numFeatures)
        throws DatabaseParserException
    {
        String[] split = meta.split(",");

        if (split.length < 3)
        {
            throw new DatabaseParserException("Split on comma less than 3. "+ split.length+ "\nInput String: "+meta);
        }

        m_fileID = split[0];
        m_emotionType = Integer.parseInt(split[1]);

        float[] array = new float[split.length-2];

        for (int i = 0; i < array.length; i++)
        {
            array[i] = Float.valueOf(split[i+2]).floatValue();
        }

        m_dimReduced = new DimensionReducedObject(array, numFeatures);
    }

    public String fileID()
    {
        return m_fileID;
    }

    public int emotionType()
    {
        return m_emotionType;
    }

    public DimensionReducedObject dimReduced()
    {
        return m_dimReduced;
    }

    public String toString()
    {
        return fileID() + "," + emotionType() + "," + dimReduced().toString();
    }
}
