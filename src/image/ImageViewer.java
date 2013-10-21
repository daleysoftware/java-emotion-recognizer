package erprj.image;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import javax.media.jai.*;
import javax.media.jai.widget.*;

public class ImageViewer extends Frame
{
    public ImageViewer(BufferedImage img)
        throws ImageConversionException
    {
        add(new ScrollingImagePanel(img, img.getWidth(), img.getHeight()));
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

        pack();
        show();
    }
}
