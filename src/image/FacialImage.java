package erprj.image;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import javax.media.jai.*;
import javax.media.jai.widget.*;
import java.util.*;
import javax.imageio.*;
import java.io.*;

public class FacialImage
{
    private static int m_counter = 0;
    private static int m_random = 0;

    private static final int m_width = 320;
    private static final int m_height = 240;

    // -------------------------------------------------------------------------
    // Images at various stages of filtering
    // -------------------------------------------------------------------------

    // A cropped 3-channel image (only the face).
    protected BufferedImage m_faceImage;

    // -------------------------------------------------------------------------
    // Apply averaging filter to an image.
    // -------------------------------------------------------------------------
    
    private static BufferedImage average(BufferedImage image)
    {
        float[] averaging = 
        {
            1.0f/100, 2.0f/100, 4.0f/100,  2.0f/100, 1.0f/100,
            2.0f/100, 4.0f/100, 8.0f/100,  4.0f/100, 2.0f/100,
            4.0f/100, 8.0f/100, 16.0f/100, 8.0f/100, 4.0f/100,
            2.0f/100, 4.0f/100, 8.0f/100,  4.0f/100, 2.0f/100,
            1.0f/100, 2.0f/100, 4.0f/100,  2.0f/100, 1.0f/100
        };

        BufferedImage imageAverage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_RGB);

        Kernel averageKernel = new Kernel(4, 4, averaging);
        ConvolveOp cop = new ConvolveOp(averageKernel, ConvolveOp.EDGE_NO_OP,
                null);

        cop.filter(image, imageAverage);
        return imageAverage;
    }

    // -------------------------------------------------------------------------
    // Apply threshold filter to an image.
    // -------------------------------------------------------------------------

    private static void threshold(BufferedImage image)
    {
        int[] rgb = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0,
                image.getWidth());

        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                int index = y * image.getWidth() + x;

                int R = (rgb[index] >> 16) & 0xff;
                int G = (rgb[index] >> 8) & 0xff;
                int B = rgb[index] & 0xff;
                double I = (R+G+B)/3.0;

                rgb[index] = (I > 130) ? 0x00ffffff : 0;
            }
        }

        image.setRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0,
                image.getWidth());
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    protected FacialImage()
    {
        // Nothing to do.
    }

    public FacialImage(BufferedImage src)
        throws ImageConversionException
    {
        // ---------------------------------------------------------------------
        // Scale the image.
        // ---------------------------------------------------------------------

        BufferedImage scaled = new BufferedImage(
                m_width, m_height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = scaled.createGraphics();
        graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(src, 0, 0, m_width, m_height, null);
        graphics.dispose();

        // ---------------------------------------------------------------------
        // Apply feat detection algorithm.
        // ---------------------------------------------------------------------

        int[] scaledRgb;
        scaledRgb = scaled.getRGB(0, 0, m_width, m_height, null, 0, m_width);
        scaled.getRGB(0, 0, m_width, m_height, scaledRgb, 0, m_width);

        int[] featRgb = new int[m_width*m_height];
        BufferedImage feat = new BufferedImage(m_width, m_height,
                BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < m_height; y++)
        {
            for (int x = 0; x < m_width; x++)
            {
                int index = y * m_width + x;

                // Standard values.
                int R = (scaledRgb[index] >> 16) & 0xff;
                int G = (scaledRgb[index] >> 8) & 0xff;
                int B = scaledRgb[index] & 0xff;
                double r = ((double) R) / (R+G+B);
                double g = ((double) G) / (R+G+B);

                // Skin algorithm.
                double F1 = -1.376*r*r + 1.0743*r + 0.2;
                double F2 = -0.776*r*r + 0.5601*r + 0.18;
                double w = (r-0.33)*(r-0.33) + (g-0.33)*(g-0.33);
                double theta =
                    Math.acos((0.5*((R-G)+(R-B)))/
                            (Math.sqrt((R-G)*(R-G)+(R-B)*(G-B))));
                double H = B<=G ? theta : Math.PI*2-theta;
                H = H *360/(2*Math.PI);
                boolean skin = g<F1 && g>F2 && w>0.001 && (H>240 || H<=20);

                featRgb[index] = skin ? 0x00ffffff : 0;
            }
        }

        feat.setRGB(0, 0, m_width, m_height, featRgb, 0, m_width);

        // ---------------------------------------------------------------------
        // Apply averaging filter.
        // ---------------------------------------------------------------------

        BufferedImage featAverage = average(average(feat));

        // ---------------------------------------------------------------------
        // Apply threshold filter.
        // ---------------------------------------------------------------------

        threshold(featAverage);

        // ---------------------------------------------------------------------
        // Find the face.
        // ---------------------------------------------------------------------

        int minX = featAverage.getWidth()-1;
        int minY = featAverage.getHeight()-1;
        int maxX = 0;
        int maxY = 0;

        featRgb = featAverage.getRGB(0, 0, m_width, m_height, null, 0,
                m_width);

        for (int y = (int) (0.1*m_height); y < (int) (0.9*m_height); y++)
        {
            for (int x = (int) (0.1*m_width); x < (int) (0.9*m_width); x++)
            {
                int index = y * m_width + x;
                int R = (featRgb[index] >> 16) & 0xff;

                if (R > 200)
                {
                    minX = x < minX ? x : minX;
                    minY = y < minY ? y : minY;
                    maxX = x > maxX ? x : maxX;
                    maxY = y > maxY ? y : maxY;
                }
            }
        }

        // ---------------------------------------------------------------------
        // Crop the face image.
        // ---------------------------------------------------------------------

        if (minX >= maxX || minY >= maxY)
        {
            throw new ImageConversionException("Can't find the face!");
        }

        m_faceImage = scaled.getSubimage(minX, minY, maxX-minX+1,
                maxY-minY+1);
    }

    public BufferedImage getImage()
    {
        return m_faceImage;
    }

    public BufferedImage getGreyImage()
    {
        BufferedImage reduced = new BufferedImage(
                64, 64, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = reduced.createGraphics();
        graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(m_faceImage, 0, 0, 64, 64, null);
        graphics.dispose();

        return reduced;
    }

    public void writeNext()
    {
        if (m_random == 0)
        {
            Random r = new Random();
            m_random = r.nextInt();
            m_random = m_random < 0 ? -m_random : m_random;
        }

        try
        {
            File outputfile = new File(m_random + "-" + m_counter + ".jpg");
            m_counter++;

            ImageIO.write(getGreyImage(), "jpg", outputfile);
        }
        catch (IOException e)
        {
            // Nothing.
        }
    }
}
