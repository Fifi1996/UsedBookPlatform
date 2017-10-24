package mjd.com.usedbookplatform.switchIndex.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by IT之旅 on 2016-10-13.
 */
public class ImageLoaderPictuer {
    private DisplayImageOptions options;
    public ImageLoaderPictuer(Context context){
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new WeakMemoryCache()).build();
        ImageLoader.getInstance().init(configuration);
        options = new DisplayImageOptions.Builder().showStubImage(0).showImageForEmptyUri(0).showImageOnFail(0)
                .cacheInMemory().cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }
    public DisplayImageOptions getOptions(){
        return options;
    }
    public void setOptions(DisplayImageOptions options){
        this.options = options;
    }
}
