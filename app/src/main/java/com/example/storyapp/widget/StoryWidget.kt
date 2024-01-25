package com.example.storyapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import com.example.storyapp.R
import com.example.storyapp.data.local.entity.StoryListEntity
import com.example.storyapp.data.local.room.StoryDatabase

/**
 * Implementation of App Widget functionality.
 */
class StoryWidget : AppWidgetProvider() {

    private lateinit var storyItems: LiveData<List<StoryListEntity>>
    private var story = ArrayList<StoryListEntity>()
    private lateinit var db: StoryDatabase
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        db = StoryDatabase.getInstance(context)
        storyItems = db.storyDao().getList()
        storyItems.observeForever {
            updateList(it)
        }
//        val widgetManager =
//            AppWidgetManager.getInstance(context.applicationContext)
//        widgetManager.notifyAppWidgetViewDataChanged(
//            widgetManager.getAppWidgetIds(
//                ComponentName
//                    (context.applicationContext.packageName, StoryWidget::class.java.name)
//            ),
//            R.id.item_widget
//        )
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }


    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
//        val widgetManager =
//            AppWidgetManager.getInstance(context.applicationContext)
//        widgetManager.notifyAppWidgetViewDataChanged(
//            widgetManager.getAppWidgetIds(
//                ComponentName
//                    (context.applicationContext.packageName, StoryWidget::class.java.name)
//            ),
//            R.id.item_widget
//        )
        if (intent.action != null) {
            if (intent.action == TOAST_ACTION) {
                val viewIndex = intent.getStringExtra(EXTRA_ITEM)
                Toast.makeText(context, viewIndex, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateList(newList: List<StoryListEntity>) {
        story.clear()
        story.addAll(newList)
    }

    companion object {
        private const val TOAST_ACTION = "com.example.storyapp.TOAST_ACTION"
        const val EXTRA_ITEM = "com.example.storyapp.EXTRA_ITEM"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StackWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val views = RemoteViews(context.packageName, R.layout.story_banner_widget)
            views.setRemoteAdapter(R.id.stack_view, intent)
            views.setEmptyView(R.id.stack_view, R.id.empty_view)

            val toastIntent = Intent(context, StoryWidget::class.java)
            toastIntent.action = TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val toastPendingIntent = PendingIntent.getBroadcast(
                context, 0, toastIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else 0
            )
            views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

