package lu.ing.gameofcode.utils;

import android.app.Application;

import com.octo.android.robospice.okhttp.OkHttpSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;

/**
 * Created by florian on 09/04/16.
 */
public class MySpiceService extends OkHttpSpiceService {

    @Override public CacheManager createCacheManager(Application application) throws CacheCreationException {
        return new CacheManager();
    }

}
