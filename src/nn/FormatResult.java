package erprj.nn;

import java.util.*;

public class FormatResult
{
    float[] m_result;

    public FormatResult(int numEmotions)
    {
        // The result array has one entry per emotion.
        m_result = new float[numEmotions];
    }

    // Sets the result array. Float should be a value between 0.0 and 1.0.
    void set(int index, float value)
    {
        m_result[index] = value;
    }

    // For communication back to the web scripts.
    public String toString()
    {
        String result = new String();

        for (int i = 0; i < m_result.length; i++)
        {
            result += m_result[i];

            if (i+1 != m_result.length)
            {
                result += ",";
            }
        }

        return result;
    }

    public void add(FormatResult fr)
    {
        for (int i = 0; i < m_result.length; i++)
        {
            m_result[i] += fr.m_result[i];
        }
    }

    public void divide(float num)
    {
        for (int i = 0; i < m_result.length; i++)
        {
            m_result[i] /= num;
        }
    }
}
