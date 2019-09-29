package com.microsys.imb.remote.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import com.microsys.imb.remote.R
import com.microsys.imb.remote.ui.activity.MainActivity
import com.sunday.common.net.ProgressDownloader
import com.sunday.common.net.ProgressResponseBody
import com.sunday.common.utils.FileProvider7
import com.sunday.common.utils.ToastUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_FOO = "com.microsys.imb.remote.service.action.FOO"
private const val ACTION_BAZ = "com.microsys.imb.remote.service.action.BAZ"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "com.microsys.imb.remote.service.extra.PARAM1"
private const val EXTRA_PARAM2 = "com.microsys.imb.remote.service.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class DownloadService : IntentService("DownloadService") {

	override fun onHandleIntent(intent: Intent?) {
		when (intent?.action) {
			ACTION_FOO -> {
				val param1 = intent.getStringExtra(EXTRA_PARAM1)
				val param2 = intent.getStringExtra(EXTRA_PARAM2)
				handleActionFoo(param1, param2)
			}
			ACTION_BAZ -> {
				val param1 = intent.getStringExtra(EXTRA_PARAM1)
				val param2 = intent.getStringExtra(EXTRA_PARAM2)
				handleActionBaz(param1, param2)
			}
		}
	}

	var downloader: ProgressDownloader? = null
	/**
	 * Handle action Foo in the provided background thread with the provided
	 * parameters.
	 */
	private fun handleActionFoo(downloadUrl: String, apkName: String) {
		val file = File(Environment.getExternalStorageDirectory().absolutePath + "/Download/$apkName")
		downloader =
			ProgressDownloader(downloadUrl, file,
				ProgressResponseBody.ProgressResponseListener { bytesRead, contentLength, done ->
					downloadProgress = 1000 * bytesRead / contentLength * 1.0 / 10
					if (done) {
						mManager.cancelAll()
						installApk(file)
						executorService?.shutdownNow()
					}
				})

		if (file.exists()) {
			installApk(file)
			return
		}
		Handler(Looper.getMainLooper()).post {
			ToastUtils.show("软件下载中，请稍后")
		}

		executorService = checkProgress()
		GlobalScope.launch {
			val down = async {
				if (!downloader?.isExcuted!!) {
					downloader?.download(0L)
				}
			}

		}
	}

	/**
	 * Handle action Baz in the provided background thread with the provided
	 * parameters.
	 */
	private fun handleActionBaz(param1: String, param2: String) {
		TODO("Handle action Baz")
	}

	companion object {
		var mContext: Context? = null
		/**
		 * Starts this service to perform action Foo with the given parameters. If
		 * the service is already performing a task this action will be queued.
		 *
		 * @see IntentService
		 */
		// TODO: Customize helper method
		@JvmStatic
		fun startActionDownload(context: Context, downloadUrl: String, apkName: String) {
			mContext = context
			val intent = Intent(context, DownloadService::class.java).apply {
				action = ACTION_FOO
				putExtra(EXTRA_PARAM1, downloadUrl)
				putExtra(EXTRA_PARAM2, apkName)
			}
			context.startService(intent)
		}

		/**
		 * Starts this service to perform action Baz with the given parameters. If
		 * the service is already performing a task this action will be queued.
		 *
		 * @see IntentService
		 */
		// TODO: Customize helper method
		@JvmStatic
		fun startActionBaz(context: Context, param1: String, param2: String) {
			val intent = Intent(context, DownloadService::class.java).apply {
				action = ACTION_BAZ
				putExtra(EXTRA_PARAM1, param1)
				putExtra(EXTRA_PARAM2, param2)
			}
			context.startService(intent)
		}
	}

	/**
	 * @param file
	 * @return
	 * @Description 安装apk
	 */
	private fun installApk(file: File) {
		val intent = Intent(Intent.ACTION_VIEW)
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
		FileProvider7.setIntentDataAndType(
			mContext,
			intent, "application/vnd.android.package-archive", file, true
		)
		mContext?.startActivity(intent)
	}

	var downloadProgress = 0.0

	private fun checkProgress(): ScheduledExecutorService {
		val scheduledExecutorService: ScheduledExecutorService = Executors.newScheduledThreadPool(3)
		scheduledExecutorService.scheduleAtFixedRate({
			if (downloadProgress > 0) {
				setNotification("正在下载中:$downloadProgress%")
			}
		}, 0, 400L, TimeUnit.MILLISECONDS)
		return scheduledExecutorService
	}


	val notificationId = 5996
	var executorService: ScheduledExecutorService? = null
	private var mManager: NotificationManager =
		mContext?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
	private val CHANNEL_ONE_ID = "18888"

	private fun setNotification(text: String) {

		val intent = Intent(mContext, MainActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
		val pi = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
		var notification: Notification? = null

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

			val mUri = Settings.System.DEFAULT_NOTIFICATION_URI

			val mChannel = NotificationChannel(CHANNEL_ONE_ID, "driver", NotificationManager.IMPORTANCE_LOW)

			mChannel.description = "description"

			mChannel.setSound(mUri, Notification.AUDIO_ATTRIBUTES_DEFAULT)

			mManager.createNotificationChannel(mChannel)

			notification = Notification.Builder(mContext, CHANNEL_ONE_ID)
				.setChannelId(CHANNEL_ONE_ID)
				.setSmallIcon(R.mipmap.icon_app_desk)
				.setContentTitle(mContext?.getString(R.string.app_name))
				.setContentText(text)
				.setContentIntent(pi)
				.build()
		} else {
			// 提升应用权限
			notification = Notification.Builder(mContext)
				.setSmallIcon(R.mipmap.icon_app_desk)
				.setContentTitle(mContext?.getString(R.string.app_name))
				.setContentText(text)
				.setContentIntent(pi)
				.build()
		}
		notification!!.flags = Notification.FLAG_ONGOING_EVENT
//		notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
//		notification.flags = notification.flags or Notification.FLAG_FOREGROUND_SERVICE
//		context.startForeground(10000, notification)
		mManager.notify(notificationId, notification)
	}
}
