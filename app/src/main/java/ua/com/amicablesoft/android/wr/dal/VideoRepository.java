package ua.com.amicablesoft.android.wr.dal;

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

import ua.com.amicablesoft.android.wr.models.Specification;
import ua.com.amicablesoft.android.wr.models.VideoFile;

/**
 * Created by olha on 2/27/17.
 */

public class VideoRepository implements IVideoRepository {

    private static final String TAG = VideoRepository.class.getSimpleName();
    private Context context;

    public VideoRepository (Context context) {
        this.context = context;
    }

    @Override
    public List<VideoFile> getListVideoFiles(Specification specification) {
        List<VideoFile> videoFiles = new ArrayList<>();
        File parentDir;
        String competition = specification.getCompetition();
        if (competition.equals("- None -")) {
            parentDir =
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString()
                            + "/" + specification.getPowerlifterName() + "/");
        } else {
            parentDir =
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString()
                            + "/" + competition + "/" + specification.getPowerlifterName() + "/");
        }

        List<File> videos = getListFiles(parentDir);
        if (videos != null) {
            List<File> filteredVideos = filterListVideo(videos, specification);
            if (filteredVideos != null) {
                for (File video : filteredVideos) {
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
            } else {
                return null;
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

    private List<File> filterListVideo(List<File> videos, Specification specification) {
        List<File> filteredVideos = new ArrayList<>();
        boolean squats = specification.isSquats();
        boolean benchPress = specification.isBenchPress();
        boolean deadLift = specification.isDeadLift();
        if (squats & benchPress & deadLift) {
            filteredVideos = videos;
        } else if (!squats & benchPress & deadLift) {
            for (File file : videos) {
                String name = file.getName();
                if (name.contains("BenchPress") || name.contains("DeadLift")) {
                    filteredVideos.add(file);
                }
            }
        } else if (squats & !benchPress & deadLift) {
            for (File file : videos) {
                String name = file.getName();
                if (name.contains("Squats") || name.contains("DeadLift")) {
                    filteredVideos.add(file);
                }
            }
        } else if (squats & benchPress & !deadLift) {
            for (File file : videos) {
                String name = file.getName();
                if (name.contains("Squats") || name.contains("BenchPress")) {
                    filteredVideos.add(file);
                }
            }
        } else if (!squats & !benchPress & deadLift) {
            for (File file : videos) {
                String name = file.getName();
                if (name.contains("DeadLift")) {
                    filteredVideos.add(file);
                }
            }
        } else if (squats & !benchPress & !deadLift) {
            for (File file : videos) {
                String name = file.getName();
                if (name.contains("Squats")) {
                    filteredVideos.add(file);
                }
            }
        } else if (!squats & benchPress & !deadLift) {
            for (File file : videos) {
                String name = file.getName();
                if (name.contains("BenchPress")) {
                    filteredVideos.add(file);
                }
            }
        } else if (!squats & !benchPress & !deadLift) {
            filteredVideos = null;
        }
        return filteredVideos;
    }
}
