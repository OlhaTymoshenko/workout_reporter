package ua.com.amicablesoft.android.wr.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ua.com.amicablesoft.android.wr.R;
import ua.com.amicablesoft.android.wr.models.VideoFile;

/**
 * Created by olha on 2/20/17.
 */

class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final List<VideoFile> videoFiles = new ArrayList<>();

    GalleryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(view);
        galleryViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(galleryViewHolder.videoFile.getVideoPath()));
                intent.setDataAndType(Uri.parse(galleryViewHolder.videoFile.getVideoPath()), "video/mp4");
                context.startActivity(intent);
            }
        });
        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GalleryViewHolder galleryViewHolder = (GalleryViewHolder) holder;
        VideoFile file = videoFiles.get(position);
        Picasso.with(context).load(file.getThumbnail()).into(galleryViewHolder.cardImage);
        String title = file.getFileName();
        galleryViewHolder.cardTitle.setText(title);
        galleryViewHolder.videoFile = file;
    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    void setVideoFilesList(List<VideoFile> videoFiles) {
        this.videoFiles.clear();
        this.videoFiles.addAll(videoFiles);
        notifyDataSetChanged();
    }

    private class GalleryViewHolder extends RecyclerView.ViewHolder {

        ImageView cardImage;
        TextView cardTitle;
        VideoFile videoFile;

        GalleryViewHolder(View itemView) {
            super(itemView);
            cardImage = (ImageView) itemView.findViewById(R.id.card_image);
            cardTitle = (TextView) itemView.findViewById(R.id.card_title);
        }
    }
}
