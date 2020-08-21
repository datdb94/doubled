package com.bd.base.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BaseUtils {

    public static ArrayList<String> getFilesFromAssets(Context context, String folder) {
        ArrayList<String> fonts = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            String[] f = assetManager.list(folder);
            if (f != null) {
                fonts = new ArrayList<>(Arrays.asList(f));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fonts;
    }

    public static String readFileAssets(Context context, String path) {
        String string;
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            string = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
        return string;
    }

    /**
     * Add provider to manifest
     *
     * @param context
     * @param path
     * @return
     */
    public static Uri getUriForFile(Context context, String path) {
        File requestFile = new File(path);
        try {
            return FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    requestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void shareVideo(Context context, String path) {
        Uri fileUri = getUriForFile(context, path);
        if (fileUri != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            sendIntent.setType("video/*");
            context.startActivity(Intent.createChooser(sendIntent, "Send video via:"));
        }
    }

    public static void shareImage(Context context, String path) {
        Uri fileUri = getUriForFile(context, path);
        if (fileUri != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            sendIntent.setType("image/*");
            context.startActivity(Intent.createChooser(sendIntent, "Send image via:"));
        }
    }

    public static void shareText(Context context, String text, String subject) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        context.startActivity(Intent.createChooser(shareIntent, "Share..."));
    }

    public static String saveBitmapPng(Bitmap bitmap, String pathFolder, String name) {
        return saveBitmap(bitmap, pathFolder, name, Bitmap.CompressFormat.PNG);
    }

    public static String saveBitmapJpeg(Bitmap bitmap, String pathFolder, String name) {
        return saveBitmap(bitmap, pathFolder, name, Bitmap.CompressFormat.JPEG);
    }

    private static String saveBitmap(Bitmap bitmap, String pathFolder, String name, Bitmap.CompressFormat format) {
        File folder = new File(pathFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileExtension;
        switch (format) {
            case PNG:
                fileExtension = ".png";
                break;
            case JPEG:
                fileExtension = ".jpeg";
                break;
            default:
                fileExtension = ".webp";
                break;

        }

        File file = new File(folder, name + fileExtension);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(format, 100, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void refreshGallery(Context context, String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(new File(path)));
        context.sendBroadcast(mediaScanIntent);
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getNavigationBarHeight(Context context) {
        boolean isNavigationBar = context.getResources().getBoolean(context.getResources().getIdentifier("config_showNavigationBar", "bool", "android"));
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && isNavigationBar) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static void feedback(Context context, String app_name, String supportEmail, String version) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{supportEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback App: " +
                app_name + "(" + context.getPackageName() + ", version: " + version + ")");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        context.startActivity(Intent.createChooser(emailIntent, "Send mail Report App !"));
    }

    /**
     * MyService.class.toString().replace("class ", "")
     */

    public static boolean checkServiceRunning(Context context, String packageService) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : services) {
            if (info.service.getClassName().toUpperCase().equals(packageService.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            return packageManager.getApplicationLabel(applicationInfo).toString();
        } catch (final PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getVersionName(Context context) {
        try {
            final String packageName = context.getPackageName();
            final PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionName;
        } catch (final PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getVersionCode(Context context) {
        try {
            final String packageName = context.getPackageName();
            final PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionCode;
        } catch (final PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void privacyPolicy(Context context, String link) {
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        boolean isInstalled = false;
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            isInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return isInstalled;
    }

    public static void openMarket(Context context) {
        final String appPackageName = context.getPackageName();
        openMarket(context, appPackageName);
    }

    public static void openMarket(Context context, String packageName) {
        String playStorePackageName = "com.android.vending";
        if (isPackageInstalled(context, playStorePackageName)) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            } catch (ActivityNotFoundException ignored) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (ActivityNotFoundException ignored) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static float dpToPx(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float pxToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static Drawable getAppIconDrawable(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getApplicationIcon(context.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getAppIconBitmap(Context context) {
        return drawableToBitmap(getAppIconDrawable(context));
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmapDrawable = ((BitmapDrawable) drawable).getBitmap();
            if (bitmapDrawable != null) {
                return bitmapDrawable;
            }
        }
        Bitmap bitmap = null;
        if (drawable.getIntrinsicWidth() > 0 || drawable.getIntrinsicHeight() > 0) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }

    public static void intentSelectImageFromGallery(Activity context, int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        context.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }

    private static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    public static Uri intentTakePictureIntent(Activity context, int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                context.startActivityForResult(takePictureIntent, requestCode);
                return photoURI;
            }
        }
        return null;
    }

    public static void showKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void closeKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
