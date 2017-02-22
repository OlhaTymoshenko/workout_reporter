package ua.com.amicablesoft.android.wr.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ua.com.amicablesoft.android.wr.R;

/**
 * Created by olha on 2/20/17.
 */

class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final List<File> thumbnailses = new ArrayList<>();
    private GalleryPresenter galleryPresenter;

    GalleryAdapter(Context context, GalleryPresenter galleryPresenter) {
        this.context = context;
        this.galleryPresenter = galleryPresenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MediaPlayer mediaPlayer = new MediaPlayer();
//                int index = parent.indexOfChild(v);
//                File file = thumbnailses.get(index);
//                String name = file.getName();
//                String path = galleryPresenter.getVideoPath(name);
//                try {
//                    mediaPlayer.setDataSource(path);
//                    mediaPlayer.prepare();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                mediaPlayer.start();
            }
        });
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GalleryViewHolder galleryViewHolder = (GalleryViewHolder) holder;
        File file = thumbnailses.get(position);
        Picasso.with(context).load(file).into(galleryViewHolder.cardImage);
        String name = file.getName();
        String title = name.substring(0, name.length() - 4);
        galleryViewHolder.cardTitle.setText(title);
    }

    @Override
    public int getItemCount() {
        return thumbnailses.size();
    }

    void setThumbnailsList(List<File> thumbnails) {
        this.thumbnailses.clear();
        this.thumbnailses.addAll(thumbnails);
        notifyDataSetChanged();
    }

    private class GalleryViewHolder extends RecyclerView.ViewHolder {

        ImageView cardImage;
        TextView cardTitle;

        GalleryViewHolder(View itemView) {
            super(itemView);
            cardImage = (ImageView) itemView.findViewById(R.id.card_image);
            cardTitle = (TextView) itemView.findViewById(R.id.card_title);
        }
    }
}
