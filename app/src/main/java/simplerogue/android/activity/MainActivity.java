package simplerogue.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.caverock.androidsvg.SVG;
import simplerogue.android.R;
import simplerogue.android.view.AndroidButtonMapping;
import simplerogue.android.view.AndroidView;
import simplerogue.android.view.HoldButtonThread;
import simplerogue.engine.manager.Managers;
import simplerogue.engine.view.Key;
import simplerogue.engine.view.ViewManager;

/**
 * @author Adam Wyłuda
 */
public class MainActivity extends Activity
{
    private static final String BUTTON_EMPTY_PATH = "svg/button_empty.svg";
    private static final String BUTTON_NW_PATH = "svg/button_nw.svg";
    private static final String BUTTON_N_PATH = "svg/button_n.svg";
    private static final String BUTTON_NE_PATH = "svg/button_ne.svg";
    private static final String BUTTON_E_PATH = "svg/button_e.svg";
    private static final String BUTTON_SE_PATH = "svg/button_se.svg";
    private static final String BUTTON_S_PATH = "svg/button_s.svg";
    private static final String BUTTON_SW_PATH = "svg/button_sw.svg";
    private static final String BUTTON_W_PATH = "svg/button_w.svg";

    private static final float BUTTON_SPACING = 3.0f;

    public static volatile MainActivity INSTANCE;

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout layout = (FrameLayout) findViewById(R.id.gameLayout);
        gameView = new GameView(this);
        layout.addView(gameView);

        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
        {
            @Override
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8)
            {
                refreshGameView();
            }
        });

        for (Integer buttonId : AndroidButtonMapping.buttonIds())
        {
            Button button = (Button) findViewById(buttonId);
            button.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent)
                {
                    return handleTouchEvent(view, motionEvent);
                }
            });
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        INSTANCE = this;

        HoldButtonThread.INSTANCE.setListener(createHoldButtonListener());
        refreshButtons();
    }

    private void refreshButtons()
    {

        setBackground(R.id.buttonNW, BUTTON_NW_PATH);
        setBackground(R.id.buttonN, BUTTON_N_PATH);
        setBackground(R.id.buttonNE, BUTTON_NE_PATH);
        setBackground(R.id.buttonE, BUTTON_E_PATH);
        setBackground(R.id.buttonSE, BUTTON_SE_PATH);
        setBackground(R.id.buttonS, BUTTON_S_PATH);
        setBackground(R.id.buttonSW, BUTTON_SW_PATH);
        setBackground(R.id.buttonW, BUTTON_W_PATH);
        setBackground(R.id.buttonC, BUTTON_EMPTY_PATH);

        setBackground(R.id.buttonShowMenu, BUTTON_EMPTY_PATH);
        setBackground(R.id.buttonChangeMode, BUTTON_EMPTY_PATH);
        setBackground(R.id.buttonAction1, BUTTON_EMPTY_PATH);
        setBackground(R.id.buttonAction2, BUTTON_EMPTY_PATH);
        setBackground(R.id.buttonAction3, BUTTON_EMPTY_PATH);
        setBackground(R.id.buttonAction4, BUTTON_EMPTY_PATH);
    }

    private void setBackground(int buttonId, String imagePath)
    {
        try
        {
            SVG svg = SVG.getFromAsset(getAssets(), imagePath);
            Bitmap bitmap = Bitmap.createBitmap((int) svg.getDocumentWidth(), (int) svg.getDocumentHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(0, 0, 0, 0);
            svg.setDocumentViewBox(-BUTTON_SPACING, -BUTTON_SPACING,
                    svg.getDocumentWidth() + 2 * BUTTON_SPACING,
                    svg.getDocumentHeight() + 2 * BUTTON_SPACING);
            svg.renderToCanvas(canvas);

            Button button = (Button) findViewById(buttonId);
            button.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        refreshGameView();

        return true;
    }

    private boolean handleTouchEvent(View view, MotionEvent event)
    {
        int buttonId = view.getId();

        if (event.getAction() == MotionEvent.ACTION_DOWN &&
                AndroidButtonMapping.MAPPING.containsKey(buttonId))
        {
            Key key = AndroidButtonMapping.MAPPING.get(buttonId);

            HoldButtonThread.INSTANCE.keyPressed(key);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            HoldButtonThread.INSTANCE.keyReleased();
        }

        return false;
    }

    private HoldButtonThread.Listener createHoldButtonListener()
    {
        return new HoldButtonThread.Listener()
        {
            @Override
            public void notifiedEngine()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        refreshGameView();
                    }
                });
            }
        };
    }

    private void refreshGameView()
    {
        refreshAndroidView();

        ViewManager viewManager = Managers.get(ViewManager.class);
        viewManager.refreshView();

        gameView.invalidate();
    }

    private void refreshAndroidView()
    {
        AndroidView androidView = AndroidView.INSTANCE;
        androidView.setHeight(gameView.getHeight() / (int) gameView.getTextHeight());
        androidView.setWidth(gameView.getWidth() / (int) gameView.getTextWidth());
    }
}
