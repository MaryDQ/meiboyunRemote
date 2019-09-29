package com.microsys.imb.remote.ui.fragment

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.microsys.imb.remote.R
import com.microsys.imb.remote.base.BaseNavigationFragment
import com.microsys.imb.remote.ui.activity.MainActivity
import com.sunday.common.utils.StatusBarUtils
import kotlinx.android.synthetic.main.fragment_gis_map.*

/**
 *
 * 描述：联情指挥页面
 * 作者: mlx
 * 创建时间： 2019.07.08
 *
 */
class GisMapFragment : BaseNavigationFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gis_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        StatusBarUtils.initSystemBarColor(this.activity as Activity?, false, Color.WHITE)
        tvProductName.text="联情指挥"
        ctlBottomBar.visibility=View.GONE
        ivProductPic.setImageResource(R.mipmap.icon_gis_map_large)
        initClick()
    }

    private fun initClick() {
        ivBack.setOnClickListener {
            if (activity is MainActivity) {
                Navigation.findNavController(it).navigateUp()
            } else {
                activity?.finish()
            }
        }
    }
}
