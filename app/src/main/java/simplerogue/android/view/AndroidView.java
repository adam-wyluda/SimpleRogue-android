package simplerogue.android.view;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import com.google.common.base.Preconditions;
import lombok.Data;
import simplerogue.android.SimpleRogueApplication;
import simplerogue.android.activity.MainActivity;
import simplerogue.engine.platform.View;
import simplerogue.engine.view.Render;

import java.util.concurrent.CountDownLatch;

/**
 * @author Adam Wyłuda
 */
@Data
public class AndroidView implements View
{
    public static final AndroidView INSTANCE = new AndroidView();

    private Render render;
    private int height = 50;
    private int width = 70;

    @Override
    public String promptText(String title, String initialValue)
    {
        Preconditions.checkState(
                !Looper.getMainLooper().equals(Looper.myLooper()),
                "Must be running on a non-UI thread");

        final CountDownLatch latch = new CountDownLatch(1);
        final String[] result = new String[1];
        result[0] = initialValue;

        Context context = MainActivity.INSTANCE;
        final EditText editText = new EditText(context);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(editText);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                result[0] = editText.getText().toString();
                latch.countDown();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                latch.countDown();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface)
            {
                latch.countDown();
            }
        });

        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return result[0];
    }
}
