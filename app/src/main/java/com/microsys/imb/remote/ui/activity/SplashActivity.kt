package com.microsys.imb.remote.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.microsys.imb.remote.BuildConfig
import com.microsys.imb.remote.R
import com.microsys.imb.remote.app.MyApplication
import com.sunday.common.MlxLibraryApp
import com.sunday.common.utils.CrashHandler
import com.sunday.common.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_splash.*
import rebus.permissionutils.PermissionEnum
import rebus.permissionutils.PermissionManager
import rebus.permissionutils.PermissionUtils
import java.util.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity() {
	private val mHideHandler = Handler()

	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_splash)

		val uiFlags = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				or View.SYSTEM_UI_FLAG_FULLSCREEN //hide statusBar
				or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) //hide navigationBar

		window.decorView.systemUiVisibility = uiFlags

		tvVersion.text = if (BuildConfig.BUILD_TYPE == "debug") {
			"v${BuildConfig.VERSION_NAME}_dev"
		} else {
			"v${BuildConfig.VERSION_NAME}"
		}

		initSdkRelatives()



		ivSplashBg.setImageResource(R.mipmap.icon_splash_bg)

		val requestPermissionList = ArrayList<PermissionEnum>()
		requestPermissionList.add(PermissionEnum.WRITE_EXTERNAL_STORAGE)
		requestPermissionList.add(PermissionEnum.READ_EXTERNAL_STORAGE)
		var granted = false
		for (item in requestPermissionList) {
			granted = PermissionUtils.isGranted(this, item) || granted
		}
		if (granted) {
			mHideHandler.postDelayed({
				val intent = Intent(this, MainActivity::class.java)
				startActivity(intent)
				finish()
			}, 2_000L)
		} else {
			PermissionManager.Builder()
				.permissions(requestPermissionList)
				.callback { permissionsGranted, permissionsDenied, permissionsDeniedForever, permissionsAsked ->
					if (permissionsGranted.containsAll(requestPermissionList)) {
						mHideHandler.postDelayed({
							val intent = Intent(this, MainActivity::class.java)
							startActivity(intent)
							finish()
						}, 500L)
					} else {
						PermissionUtils.openApplicationSettings(this, R::class.java.getPackage().name)
						ToastUtils.show("后续功能需要给予存储等权限，请前往设置通过")
					}

				}
				.askAgain(true)
				.ask(this)
		}


	}

	private fun initSdkRelatives() {
		MlxLibraryApp.init(MyApplication.sInstance)
		CrashHandler.getsInstance().init(MyApplication.sInstance, "meiboyunRemote")
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		PermissionManager.handleResult(this, requestCode, permissions, grantResults)
	}


}
