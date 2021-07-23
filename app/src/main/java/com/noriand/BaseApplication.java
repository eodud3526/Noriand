package com.noriand;

import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class BaseApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		setImageLoader();
	}

	private void setImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
//				.displayer(new FadeInBitmapDisplayer(700, true, true, true))
				.showImageForEmptyUri(R.drawable.bg_empty_white)
				.showImageOnFail(R.drawable.bg_empty_white)
				.showImageOnLoading(R.drawable.bg_empty_white)
				.bitmapConfig(Bitmap.Config.RGB_565)
//				.imageScaleType(ImageScaleType.EXACTLY)
				.cacheOnDisk(true).cacheInMemory(true).build();

		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
		config.threadPriority(Thread.NORM_PRIORITY + 5);
		config.denyCacheImageMultipleSizesInMemory();
		config.defaultDisplayImageOptions(options);
		config.threadPoolSize(5);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(300 * 1024 * 1024); // 300
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.memoryCache(new LruMemoryCache(10 * 1024 * 1024)); // 10
		ImageLoader.getInstance().init(config.build());
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
