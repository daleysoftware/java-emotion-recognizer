package erprj.app;

import erprj.db.*;
import erprj.dro.*;
import erprj.image.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.media.jai.*;
import javax.imageio.*;

public class FeatureDetectionFullApplication implements ISubApplication
{
    protected String m_imageFile;
    protected int m_emotionType;

    FeatureDetectionFullApplication(String imageFile, int emotionType)
    {
        m_imageFile = imageFile;
        m_emotionType = emotionType;
    }

    public boolean run()
        throws ImageConversionException
    {
        BufferedImage src =
            JAI.create("fileload", m_imageFile).getAsBufferedImage();

        FacialImage facialImage = new FacialImage(src);

        // Debug
        //facialImage.writeNext();

        DimensionReducedObject dimReduced =
            new DimensionReducedObject(facialImage, 0);

        DatabaseFormatter formatter =
            new DatabaseFormatter(m_emotionType, dimReduced);

        System.out.println(formatter.databaseEncoding());
        return true;
    }
}
