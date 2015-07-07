package simplerogue.android.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simplerogue.android.SettingsHelper;
import simplerogue.android.view.AndroidView;
import simplerogue.android.view.RenderCache;
import simplerogue.engine.game.Game;
import simplerogue.engine.level.Direction;
import simplerogue.engine.view.Char;
import simplerogue.engine.view.CharArea;
import simplerogue.engine.view.Render;

/**
 * @author Adam Wyłuda
 */
public class GameView extends View
{
    private static final Logger LOG = LoggerFactory.getLogger(GameView.class);

    private RenderCache renderCache;

    public GameView(Context context)
    {
        super(context);
    }

    public float getTextHeight()
    {
        return getTextBounds(createPaint()).height();
    }

    public float getTextWidth()
    {
        return getTextBounds(createPaint()).width();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Render render = AndroidView.INSTANCE.getRender();
        CharArea area = render.getCharArea();

        if (area != null)
        {
            int height = area.getHeight();
            int width = area.getWidth();

            // Invalidate cache if char area has changed its dimensions
            if (renderCache != null &&
                    (height != renderCache.getRender().getCharArea().getHeight() ||
                    width != renderCache.getRender().getCharArea().getWidth()))
            {
                renderCache = null;
            }

            TextPaint paint = createPaint();
            Rect bounds = getTextBounds(paint);
            float textHeight = bounds.height();
            float textWidth = bounds.width();

            Char[][] chars = area.getChars();

            Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas imageCanvas = new Canvas(bitmap);

            Bitmap characterBitmap = Bitmap.createBitmap((int) textWidth, (int) textHeight, Bitmap.Config.ARGB_8888);
            Canvas characterCanvas = new Canvas(characterBitmap);

            int shiftStartY = 0, shiftStartX = 0, shiftEndY = 0, shiftEndX = 0;

            if (renderCache != null)
            {
                Direction shiftDirection = render.getShiftDirection();

                shiftStartY = -shiftDirection.transformY(0);
                shiftStartX = -shiftDirection.transformX(0);
                shiftEndY = height + shiftStartY;
                shiftEndX = width + shiftStartX;

                imageCanvas.drawBitmap(renderCache.getBitmap(),
                        textWidth * shiftStartX, textHeight * shiftStartY, null);
            }

            int drawCount = 0;

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    Char ch = chars[y][x];

                    if (renderCache != null &&
                            y >= shiftStartY && y < shiftEndY &&
                            x >= shiftStartX && x < shiftEndX)
                    {
                        int ty = y - shiftStartY;
                        int tx = x - shiftStartX;

                        CharArea cachedArea = renderCache.getRender().getCharArea();

                        if (ty >= 0 && ty < height &&
                                tx >= 0 && tx < width &&
                                !ch.equals(cachedArea.getChars()[ty][tx]))
                        {
                            drawCount++;
                            drawCharacter(imageCanvas, characterBitmap, characterCanvas, paint, textHeight, textWidth, y, x, ch);
                        }
                    }
                    else
                    {
                        drawCount++;
                        drawCharacter(imageCanvas, characterBitmap, characterCanvas, paint, textHeight, textWidth, y, x, ch);
                    }
                }
            }

            LOG.debug("Draw count: {}", drawCount);

            renderCache = new RenderCache();
            renderCache.setBitmap(bitmap);
            renderCache.setRender(render);

            canvas.drawBitmap(bitmap, 0f, 0f, null);

            int wholeHeight = height * (int) textHeight;
            int wholeWidth = width * (int) textWidth;

            // Clear borders
            paint.setColor(Color.BLACK);
            canvas.drawRect(0, wholeHeight, canvas.getWidth(), canvas.getHeight(), paint);
            canvas.drawRect(wholeWidth, 0, canvas.getWidth(), canvas.getHeight(), paint);
        }
    }

    private void drawCharacter(Canvas canvas, Bitmap characterBitmap, Canvas characterCanvas, TextPaint paint,
                               float textHeight, float textWidth,
                               int y, int x,
                               Char ch)
    {
        simplerogue.engine.view.Color background = ch.getBackgroundColor();

        paint.setColor(toARGB(background));
        characterCanvas.drawRect(0, 0, textWidth, textHeight, paint);

        char character = ch.getCharacter();
        simplerogue.engine.view.Color color = ch.getColor();

        paint.setColor(toARGB(color));
        characterCanvas.drawText(Character.toString(character), 0, textHeight, paint);

        canvas.drawBitmap(characterBitmap, x * textWidth, y * textHeight, paint);
    }

    private TextPaint createPaint()
    {
        TextPaint paint = new TextPaint();
        Typeface typeface = Typeface.MONOSPACE;
        paint.setTypeface(typeface);
        paint.setTextSize(getFontSize());
        paint.setAntiAlias(SettingsHelper.isAntialiasing(getContext()));

        return paint;
    }

    private int getFontSize()
    {
        return SettingsHelper.getFontSize(getContext());
    }

    private Rect getTextBounds(TextPaint paint)
    {
        Rect rect = new Rect();
        paint.getTextBounds("@", 0, 1, rect);

        return rect;
    }

    private int toARGB(simplerogue.engine.view.Color color)
    {
        return Color.argb(255, color.getRed(), color.getGreen(), color.getBlue());
    }
}
