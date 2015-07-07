package simplerogue.android;

import android.content.Context;
import simplerogue.android.view.AndroidView;
import simplerogue.engine.manager.AbstractManager;
import simplerogue.engine.platform.FileSystemStorage;
import simplerogue.engine.platform.PlatformManager;
import simplerogue.engine.platform.Storage;
import simplerogue.engine.platform.View;

import java.io.File;

/**
 * @author Adam Wyłuda
 */
public class AndroidPlatformManager extends AbstractManager implements PlatformManager
{
    private final File gameHome;

    public AndroidPlatformManager(Context context)
    {
        this.gameHome = context.getFilesDir();
    }

    @Override
    public Storage createStorage()
    {
        return new FileSystemStorage(gameHome);
    }

    @Override
    public View createView()
    {
        return AndroidView.INSTANCE;
    }
}
