package simplerogue.android.view;

import com.google.common.collect.Maps;
import simplerogue.android.R;
import simplerogue.engine.view.Key;

import java.util.Map;
import java.util.Set;

/**
 * @author Adam Wyłuda
 */
public class AndroidButtonMapping
{
    public static final Map<Integer, Key> MAPPING = Maps.newHashMap();

    static
    {
        MAPPING.put(R.id.buttonN, Key.DIRECTION_N);
        MAPPING.put(R.id.buttonNE, Key.DIRECTION_NE);
        MAPPING.put(R.id.buttonE, Key.DIRECTION_E);
        MAPPING.put(R.id.buttonSE, Key.DIRECTION_SE);
        MAPPING.put(R.id.buttonS, Key.DIRECTION_S);
        MAPPING.put(R.id.buttonSW, Key.DIRECTION_SW);
        MAPPING.put(R.id.buttonW, Key.DIRECTION_W);
        MAPPING.put(R.id.buttonNW, Key.DIRECTION_NW);
        MAPPING.put(R.id.buttonC, Key.DIRECTION_C);
        MAPPING.put(R.id.buttonChangeMode, Key.CHANGE_PLAYER_MODE);
        MAPPING.put(R.id.buttonShowMenu, Key.SHOW_MENU);
        MAPPING.put(R.id.buttonAction1, Key.ACTION_1);
        MAPPING.put(R.id.buttonAction2, Key.ACTION_2);
        MAPPING.put(R.id.buttonAction3, Key.ACTION_3);
        MAPPING.put(R.id.buttonAction4, Key.ACTION_4);
    }

    public static Set<Integer> buttonIds()
    {
        return MAPPING.keySet();
    }
}
