package ua.com.amicablesoft.android.wr.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.com.amicablesoft.android.wr.R;
import ua.com.amicablesoft.android.wr.dal.IRepository;
import ua.com.amicablesoft.android.wr.dal.Repository;
import ua.com.amicablesoft.android.wr.models.Competition;
import ua.com.amicablesoft.android.wr.models.Powerlifter;
import ua.com.amicablesoft.android.wr.models.VideoFile;

/**
 * Created by olha on 2/16/17.
 */

class GalleryPresenter {

    private static final String TAG = GalleryPresenter.class.getSimpleName();
    private final GalleryView view;
    private Repository repository;
    private Powerlifter currentPowerlifter;
    private Context context;

    GalleryPresenter (GalleryView view, Context context) {
        this.view = view;
        this.context = context;
    }

    void setPowerlifters() {
        repository.getPowerlifters(new IRepository.LoadPowerliftersCallback() {
            @Override
            public void onPowerliftersLoaded(ArrayList<Powerlifter> powerlifters) {
                view.setListPowerlifters(powerlifters);
            }

            @Override
            public void onDataNotAvailable() {
            }
        });
        view.setPowerlifter(0);
    }

    void getCompetitions(final Context context) {
        repository = new Repository();
        repository.getCompetitions(new IRepository.LoadCompetitionsCallback() {
            @Override
            public void onCompetitionsLoaded(ArrayList<Competition> competitionArrayList) {
                ArrayList<String> listItems = new ArrayList<>();
                listItems.add(context.getString(R.string.title_exercise));
                listItems.add(context.getString(R.string.title_squats));
                listItems.add(context.getString(R.string.title_bench_press));
                listItems.add(context.getString(R.string.title_dead_lift));
                listItems.add(context.getString(R.string.title_competition));
                for (Competition c : competitionArrayList) {
                    listItems.add(c.getCompetition());
                }
                listItems.add(context.getString(R.string.button_show));
                view.setListItems(listItems);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    void setCurrentPowerlifter (Powerlifter powerlifter) {
        currentPowerlifter = powerlifter;
    }

    private String getCurrentPowerlifterName() {
        return currentPowerlifter.getLastName() + "-" + currentPowerlifter.getName();
    }

    List<VideoFile> getListVideoFiles() {
        List<VideoFile> videoFiles = new ArrayList<>();
        File parentDir =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString()
                        + "/" + getCurrentPowerlifterName() + "/");
        List<File> videos = getListFiles(parentDir);
        if (videos != null) {
            for (File video : videos) {
                String fileName = video.getName();
                String thumbName = fileName.substring(0, fileName.length() - 4);
                String path = context.getExternalCacheDir().toString();
                File thumb = new File(path, thumbName + ".png");
                if (thumb.exists()) {
                    VideoFile videoFile = new VideoFile(video.getPath(), thumb, thumbName);
                    videoFiles.add(videoFile);
                } else {
                    try {
                        thumb.createNewFile();
                    } catch (IOException e) {
                        Log.e(TAG, "Error", e);
                    }
                    thumb = createThumbnailFromFile(video, thumb);
                    VideoFile videoFile = new VideoFile(video.getPath(), thumb, thumbName);
                    videoFiles.add(videoFile);
                }
            }
            return videoFiles;
        } else {
            return null;
        }
    }

    private List<File> getListFiles(File parentDir) {
        ArrayList<File> listFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    listFiles.addAll(getListFiles(file));
                } else listFiles.add(file);
            }
            return listFiles;
        } else {
            return null;
        }
    }

    private File createThumbnailFromFile(File file, File thumb) {
            String filePath = file.getPath();
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
        return saveBitmapToFile(bitmap, thumb);
    }

    private File saveBitmapToFile (Bitmap bitmap, File thumb) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(thumb);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return thumb;
    }
}
