package amit_yoav.deep_diving.compat;

import android.media.SoundPool;
import android.os.Build;

/**
 * Created by amir on 12/17/16.
 */

public class Compat {
    public static SoundPool createSoundPool() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            return Api21Plus.createSoundPool();
        }
        return OldApis.createSoundPool();
    }
}
