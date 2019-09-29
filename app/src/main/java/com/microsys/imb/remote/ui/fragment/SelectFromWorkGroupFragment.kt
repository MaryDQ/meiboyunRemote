package com.microsys.imb.remote.ui.fragment

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.lxj.xpopup.XPopup
import com.microsys.imb.remote.R
import com.microsys.imb.remote.base.BaseNavigationFragment
import com.microsys.imb.remote.ui.adapter.recycler.AbstractSimpleAdapter
import com.microsys.imb.remote.ui.adapter.recycler.ViewHolder
import com.microsys.imb.remote.widgets.dialog.PrepareCallDialog
import com.sunday.common.utils.StatusBarUtils
import com.sunday.common.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_select_from_work_group.*

class SelectFromWorkGroupFragment : BaseNavigationFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_from_work_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        StatusBarUtils.initSystemBarColor(this.activity as Activity?, false, Color.WHITE)
        initRcvWorkGroup()
        initClick()
    }

    var groupList = ArrayList(listOf("迈克行通信", "美播云科技", "万物研究院"))
    var originalGroupList = ArrayList(listOf("迈克行通信", "美播云科技", "万物研究院"))
    var workGroupAdapter: AbstractSimpleAdapter<String>? = null

    private fun initRcvWorkGroup() {

        workGroupAdapter = object : AbstractSimpleAdapter<String>(context, groupList, R.layout.item_work_group) {
            override fun onBindViewHolder(holder: ViewHolder?, data: String?, curPosition: Int) {
                holder?.getView<TextView>(R.id.tvItemGroupName)?.text = data
                holder?.getView<ImageView>(R.id.ivItemSelectStatus)
                    ?.apply {
                        if (workGroupAdapter?.selectPosition == curPosition) {
                            setBackgroundResource(R.mipmap.icon_item_select)
                        } else {
                            setBackgroundResource(R.mipmap.icon_item_not_select)
                        }
                    }

            }
        }
        workGroupAdapter!!.setOnItemClickListener { _, position, _ ->
            tvCurSelectGroup.text = "已选择：1个工作组"
            workGroupAdapter!!.selectPosition = position
            workGroupAdapter!!.notifyDataSetChanged()
            btnConfirm.setBackgroundResource(R.drawable.drawable_blue_4dp_rect)
            btnConfirm.tag = "yes"
        }
        rcvWorkGroups.layoutManager = LinearLayoutManager(context)
        rcvWorkGroups.adapter = workGroupAdapter
    }

    private fun initClick() {
        tvCancel.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearchGroup(etSearch.text.toString())
            }
            false
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    doSearchGroup(s.toString())
                } else {
                    workGroupAdapter!!.resetData(originalGroupList)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        btnConfirm.setOnClickListener {
            if ("no" == it.tag as String) {
                ToastUtils.show("请选择人员")
                return@setOnClickListener
            }
            XPopup.Builder(context)
                .asCustom(PrepareCallDialog(context!!))
                .show()
        }

    }

    /**
     * 根据输入，搜索目标,更新列表
     */
    private fun doSearchGroup(groupName: String) {
        val tempSelectList = ArrayList<String>()
        for (item in groupList) {
            if (item.contains(groupName)) {
                tempSelectList.add(item)
            }
        }
        workGroupAdapter!!.resetData(tempSelectList)
    }

}
