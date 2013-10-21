package erprj.app;

import erprj.db.*;
import erprj.dro.*;
import erprj.image.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import javax.media.jai.*;
import javax.imageio.*;

public class FeatureDetectionMouthApplication implements ISubApplication
{
    protected String m_imageFile;
    protected int m_emotionType;

    // The size of the reduced object.
    private static final int m_width = 25;
    private static final int m_height = 16;

    FeatureDetectionMouthApplication(String imageFile, int emotionType)
    {
        m_imageFile = imageFile;
        m_emotionType = emotionType;
    }

    public boolean run()
        throws ImageConversionException
    {
        // Load the image.
        BufferedImage src =
            JAI.create("fileload", m_imageFile).getAsBufferedImage();

        // Scale the image.
        BufferedImage scaled = new BufferedImage(
                m_width, m_height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = scaled.createGraphics();
        graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(src, 0, 0, m_width, m_height, null);
        graphics.dispose();

        // Normalize.
        int[] rgb = new int[m_width*m_height];
        float[] normal = new float[m_width*m_height];
        scaled.getRGB(0, 0, m_width, m_height, rgb, 0, m_width);

        for (int y = 0; y < m_height; y++)
        {
            for (int x = 0; x < m_width; x++)
            {
                int index = y * m_width + x;

                int R = (rgb[index] >> 16) & 0xff;
                int G = (rgb[index] >> 8) & 0xff;
                int B = rgb[index] & 0xff;

                float intensity = ((R+G+B)/(255.0f*3))*2.0f-1.0f;
                normal[index] = intensity;
            }
        }

        // And write the result.
        DimensionReducedObject dimReduced =
            new DimensionReducedObject(normal, 0);

        DatabaseFormatter formatter =
            new DatabaseFormatter(m_emotionType, dimReduced);

        System.out.println(formatter.databaseEncoding());
        return true;
    }
}
