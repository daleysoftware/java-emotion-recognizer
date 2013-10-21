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

public class MouthImage extends FacialImage
{
    public MouthImage(BufferedImage src)
    {
        m_faceImage = src;
    }
}
