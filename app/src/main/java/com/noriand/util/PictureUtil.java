package com.noriand.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PictureUtil {
	public static Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		String path = inContext.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "temp_"+System.currentTimeMillis()+".jpg";
		return Uri.parse(path);
	}

	public static String getAbsolutePathFromUri(Context context, Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
		return path;
	}
	
	public static Uri getLastCaptureImageUri(Context context) {
		Uri uri = null;
		String[] PROJECTION = {MediaStore.Images.ImageColumns.DATA};
	
		Cursor cursorImages = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION, null, null, null);
		if(cursorImages != null && cursorImages.moveToLast()) {
			uri = Uri.parse(cursorImages.getString(0));
			cursorImages.close();
		}
		return uri;
	}

	public static String profileImageDegreeSizeCkeck(Context context, String path, int size) {
		Bitmap prepareBitmap = null;
		Bitmap rotateBitmap = null;
		Bitmap thumbnailBitmap = null;

		Options options = new Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(path, options);

		float widthScale = options.outWidth / size;
		float heightScale = options.outHeight / size;
		float scale = widthScale < heightScale ? widthScale : heightScale;
		if (scale >= 64) {
			options.inSampleSize = 64;
		} else if (scale >= 32) {
			options.inSampleSize = 32;
		} else if (scale >= 16) {
			options.inSampleSize = 16;
		} else if (scale >= 8) {
			options.inSampleSize = 8;
		} else if (scale >= 4) {
			options.inSampleSize = 4;
		} else if (scale >= 2) {
			options.inSampleSize = 2;
		} else {
			options.inSampleSize = 1;
		}
		options.inDither = true;
		options.inPurgeable = true;
		options.inPreferredConfig = Config.RGB_565;
		options.inJustDecodeBounds = false;
		prepareBitmap = BitmapFactory.decodeFile(path, options);
		if(prepareBitmap == null) {
			return null;
		}

		if(prepareBitmap.getWidth() > size){
			thumbnailBitmap = resizeAndCropCenter(prepareBitmap, size, false);
		} else{
			thumbnailBitmap = prepareBitmap.copy(Config.RGB_565, true);
		}

		int degree = getExifOrientation(path);
		if (degree != 0) {
			rotateBitmap = rotateBitmap(thumbnailBitmap, degree, true);
			if (!thumbnailBitmap.isRecycled()) {
				thumbnailBitmap.recycle();
			}
		} else {
			rotateBitmap = thumbnailBitmap.copy(Config.RGB_565, true);
		}

		// 내부메모리에 crop한 이미지를 저장
		File file = null;
		try {
			file = createImageFile(context);
		} catch (IOException e) {
		}

		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		} catch (FileNotFoundException e) {
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}

			if (thumbnailBitmap.equals(prepareBitmap) && prepareBitmap != null && !prepareBitmap.isRecycled()) {
				prepareBitmap.recycle();
			}

			// 방향정보 0도 저장
			ExifInterface exif = null;
			try {
				exif = new ExifInterface(path);
				exif.setAttribute(ExifInterface.TAG_ORIENTATION, "" + ExifInterface.ORIENTATION_NORMAL);
				exif.saveAttributes();
			
			} catch (IOException e) {
			}

			// 스캐닝 요청
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			Uri uri = Uri.parse("file://" + path);
			intent.setData(uri);
			context.sendBroadcast(intent);
        }

		return file.getAbsolutePath();
	}
	
	public static Bitmap resizeAndCropCenter(Bitmap bitmap, int size, boolean recycle) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		if (w == size && h == size) {
			return bitmap;
		}

		// scale the image so that the shorter side equals to the target;
		// the longer side will be center-cropped.
		float scale = (float) size / Math.min(w, h);

		Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
		int width = Math.round(scale * bitmap.getWidth());
		int height = Math.round(scale * bitmap.getHeight());
		Canvas canvas = new Canvas(target);
		canvas.translate((size - width) / 2f, (size - height) / 2f);
		canvas.scale(scale, scale);
		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		if (recycle) {
			bitmap.recycle();
		}

		return target;
	}
	
	public synchronized static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException e) {
		}

		if(exif != null) {
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

			if(orientation != -1) {
				if(orientation == ExifInterface.ORIENTATION_ROTATE_90) {
					degree = 90;
				} else if(orientation == ExifInterface.ORIENTATION_ROTATE_180) {
					degree = 180;
				} else if(orientation == ExifInterface.ORIENTATION_ROTATE_270) {
					degree = 270;
				}
			}
		}

		return degree;
	}
	public static File createImageFile(Context context) throws IOException {
		File imageStorageDir = new File(context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp");
		if (!imageStorageDir.exists()) {
			imageStorageDir.mkdirs();
		}

		imageStorageDir = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
		return imageStorageDir;
	}

	public static Bitmap rotateBitmap(Bitmap source, int rotation, boolean recycle) {
		if(rotation == 0) {
			return source;
		}

		int w = source.getWidth();
		int h = source.getHeight();
		Matrix m = new Matrix();
		m.postRotate(rotation);
		Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, w, h, m, false);
		if(recycle) {
			source.recycle();
		}

		return bitmap;
	}

	private static Config getConfig(Bitmap bitmap) {
		Config config = bitmap.getConfig();
		if(config == null) {
			config = Config.RGB_565;
		}
		return config;
	}

//	public static Bitmap convertViewToBitmap(View view) {
//		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
//		Canvas canvas = new Canvas(bitmap);
//		if(view instanceof SurfaceView) {
//			SurfaceView surfaceView = (SurfaceView)view;
//			surfaceView.setZOrderOnTop(true);
//			surfaceView.draw(canvas);
//			surfaceView.setZOrderOnTop(true);
//			return bitmap;
//		} else {
//			view.draw(canvas);
//			return bitmap;
//		}
//	}
}
