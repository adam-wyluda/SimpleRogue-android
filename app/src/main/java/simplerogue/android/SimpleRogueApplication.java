package simplerogue.android;

import android.app.Application;
import simplerogue.android.view.HoldButtonThread;
import simplerogue.engine.EngineConfigurator;
import simplerogue.engine.game.GameManager;
import simplerogue.engine.manager.Managers;
import simplerogue.engine.platform.PlatformManager;
import simplerogue.engine.view.ViewManager;
import simplerogue.engine.world.World;
import simplerogue.engine.world.WorldManager;
import simplerogue.world.DefaultWorldManager;

/**
 * @author Adam Wyłuda
 */
public class SimpleRogueApplication extends Application
{
    public static volatile SimpleRogueApplication INSTANCE;

    public SimpleRogueApplication()
    {
        INSTANCE = this;
    }

    @Override
    public void onCreate()
    {
        EngineConfigurator.registerDefaultManagers();
        Managers.register(PlatformManager.class, new AndroidPlatformManager(this));
        Managers.register(WorldManager.class, new DefaultWorldManager());
        Managers.initAll();

        World world = Managers.get(WorldManager.class).getWorlds().get(0);
        Managers.get(GameManager.class).loadWorld(world);
        Managers.get(ViewManager.class).refreshView();

        HoldButtonThread.INSTANCE.start();
    }
}
