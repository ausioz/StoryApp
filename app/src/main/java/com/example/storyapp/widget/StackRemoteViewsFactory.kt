package com.example.storyapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.storyapp.R
import com.example.storyapp.data.local.entity.StoryListEntity
import com.example.storyapp.data.local.room.StoryDatabase


internal class StackRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private lateinit var storyItems: LiveData<List<StoryListEntity>>
    private var story = ArrayList<StoryListEntity>()
    private var db = StoryDatabase.getInstance(context.applicationContext)

    override fun onCreate() {
        storyItems = db.storyDao().getList()
        storyItems.observeForever {
            updateList(it)
        }
    }
    override fun onDataSetChanged() {
        storyItems = db.storyDao().getList()
        getViewAt(0)
    }
    override fun onDestroy() {}

    override fun getCount(): Int = story.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
        try {
            val bitmap: Bitmap = Glide.with(context).asBitmap().load(story[position].photoUrl)
                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
            rv.setImageViewBitmap(R.id.imageView_widget, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        rv.setTextViewText(R.id.userTV_widget, story[position].name)
        rv.setTextViewText(R.id.descriptionTV_widget, story[position].description)

        val extras = bundleOf(
            StoryWidget.EXTRA_ITEM to story[position].description.toString()
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.item_widget, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    private fun updateList(newList: List<StoryListEntity>) {
        story.clear()
        story.addAll(newList)
        onDataSetChanged()
    }

}