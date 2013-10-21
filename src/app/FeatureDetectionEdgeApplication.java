package erprj.app;

import erprj.db.*;
import erprj.dro.*;
import erprj.image.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.media.jai.*;
import javax.imageio.*;

public class FeatureDetectionEdgeApplication implements ISubApplication
{
    protected String m_imageFile;
    protected int m_emotionType;

    FeatureDetectionEdgeApplication(String imageFile, int emotionType)
    {
        m_imageFile = imageFile;
        m_emotionType = emotionType;
    }

    public boolean run()
        throws ImageConversionException
    {
        BufferedImage src =
            JAI.create("fileload", m_imageFile).getAsBufferedImage();

        MouthImage mouthImage = new MouthImage(src);

        DimensionReducedObject dimReduced =
            new DimensionReducedObject(mouthImage, 0);

        DatabaseFormatter formatter =
            new DatabaseFormatter(m_emotionType, dimReduced);

        System.out.println(formatter.databaseEncoding());
        return true;
    }
}
