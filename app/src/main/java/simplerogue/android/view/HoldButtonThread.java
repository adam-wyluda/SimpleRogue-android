package simplerogue.android.view;

import lombok.Setter;
import simplerogue.engine.manager.Managers;
import simplerogue.engine.view.Key;
import simplerogue.engine.view.ViewManager;

import java.util.concurrent.Semaphore;

/**
 * Sends key pressed events to engine while user holds button.
 *
 * @author Adam Wyłuda
 */
public class HoldButtonThread extends Thread
{
    public static interface Listener
    {
        void notifiedEngine();
    }

    public static HoldButtonThread INSTANCE = new HoldButtonThread();

    private static final int FIRST_PAUSE = 500;
    private static final int PAUSE = 100;

    private volatile Semaphore semaphore = new Semaphore(0);
    private volatile boolean keyHold = false;
    private volatile Key key;

    @Setter
    private volatile Listener listener;

    public void keyPressed(Key key)
    {
        this.key = key;
        this.keyHold = true;
        this.semaphore.release();
    }

    public void keyReleased()
    {
        this.keyHold = false;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                semaphore.acquire();
                semaphore.drainPermits();

                boolean firstRun = true;
                while (keyHold)
                {
                    Managers.get(ViewManager.class).handleKey(key);
                    listener.notifiedEngine();

                    if (!keyHold)
                    {
                        break;
                    }

                    if (firstRun)
                    {
                        Thread.sleep(FIRST_PAUSE);
                        firstRun = false;
                    }
                    else
                    {
                        Thread.sleep(PAUSE);
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
