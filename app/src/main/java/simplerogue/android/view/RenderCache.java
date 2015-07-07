package simplerogue.android.view;

import android.graphics.Bitmap;
import lombok.Data;
import simplerogue.engine.view.Render;

/**
 * @author Adam Wyłuda
 */
@Data
public class RenderCache
{
    private Render render;
    private Bitmap bitmap;
}
