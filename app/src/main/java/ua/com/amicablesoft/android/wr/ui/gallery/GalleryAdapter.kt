/*
 * Created by Olha Tymoshenko on 5/25/18 1:15 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui.gallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import ua.com.amicablesoft.android.wr.R
import ua.com.amicablesoft.android.wr.models.VideoFile
import java.util.*

internal class GalleryAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val videoFiles = ArrayList<VideoFile>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        val galleryViewHolder = GalleryViewHolder(view)
        galleryViewHolder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(galleryViewHolder.videoFile!!.videoPath))
            intent.setDataAndType(Uri.parse(galleryViewHolder.videoFile!!.videoPath), "video/mp4")
            val packageManager = context.packageManager
            val infos = packageManager.queryIntentActivities(intent, 0)
            if (infos.size > 0) {
                context.startActivity(intent)
            } else {
                Snackbar.make(parent, context.getText(R.string.snackbar_text_install_app),
                        Snackbar.LENGTH_LONG).show()
            }
        }
        return galleryViewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val galleryViewHolder = holder as GalleryViewHolder
        val file = videoFiles[position]
        Picasso.with(context).load(file.thumbnail).into(galleryViewHolder.cardImage)
        val title = file.fileName
        galleryViewHolder.cardTitle.text = title
        galleryViewHolder.videoFile = file
    }

    override fun getItemCount(): Int {
        return videoFiles.size
    }

    fun setVideoFilesList(videoFiles: List<VideoFile>) {
        this.videoFiles.clear()
        this.videoFiles.addAll(videoFiles)
        notifyDataSetChanged()
    }

    private inner class GalleryViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var cardImage: ImageView = itemView.findViewById<View>(R.id.card_image) as ImageView
        internal var cardTitle: TextView = itemView.findViewById<View>(R.id.card_title) as TextView
        internal var videoFile: VideoFile? = null
    }
}
