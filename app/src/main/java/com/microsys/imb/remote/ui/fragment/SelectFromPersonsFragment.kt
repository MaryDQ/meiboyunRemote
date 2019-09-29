package com.microsys.imb.remote.ui.fragment

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.microsys.imb.remote.R
import com.microsys.imb.remote.base.BaseNavigationFragment
import com.microsys.imb.remote.bean.*
import com.microsys.imb.remote.constants.SelectFromPersonConstants
import com.microsys.imb.remote.constants.TestConstants
import com.microsys.imb.remote.event.BackPressedEvent
import com.microsys.imb.remote.ui.adapter.recycler.AbstractSimpleAdapter
import com.microsys.imb.remote.ui.adapter.recycler.ViewHolder
import com.microsys.imb.remote.ui.fragment.SelectPersonsFromOrgFragment.Companion.ITEM_NOT_SELECT
import com.microsys.imb.remote.ui.fragment.SelectPersonsFromOrgFragment.Companion.ITEM_SELECT
import com.microsys.imb.remote.utils.OrgPersonUtils
import com.microsys.imb.remote.widgets.dialog.PrepareCallDialog
import com.sunday.common.utils.StatusBarUtils
import com.sunday.common.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_select_from_persons.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SelectFromPersonsFragment : BaseNavigationFragment() {


	companion object {
		var isAlive = false
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_select_from_persons, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initData()
	}

	private fun initData() {
		StatusBarUtils.initSystemBarColor(this.activity as Activity?, false, Color.WHITE)
		isAlive = true
		tvOrganizationName.text = TestConstants.testContactBean.topdept.name
		tvCompanyName.text = TestConstants.testContactBean.topdept.name
		initRcvContactPerson()
		initRcvCompanyOrg()
		initClick()
	}

	override fun onStart() {
		super.onStart()
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this)
		}
	}

	override fun onStop() {
		super.onStop()
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this)
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun receiveBackCodeEvent(event: BackPressedEvent) {
		if (event.isPressed) {
			tvCancel.callOnClick()
		}
	}

	var companyOrgAdapter: AbstractSimpleAdapter<Subdept>? = null
	private fun initRcvCompanyOrg() {
		val companyOrgList: ArrayList<Subdept> = ArrayList(TestConstants.testContactBean.topdept.subdept)

		companyOrgAdapter =
			object : AbstractSimpleAdapter<Subdept>(context, companyOrgList, R.layout.item_company_org_item) {
				override fun onBindViewHolder(holder: ViewHolder?, data: Subdept?, curPosition: Int) {
					holder!!.getView<TextView>(R.id.tvOrgName).text = data?.name + "(不知道多少人)"
					holder.getView<ImageView>(R.id.ivSelectStatus).apply {
						if (data!!.itemStatus == SelectPersonsFromOrgFragment.ITEM_SELECT) {
							setBackgroundResource(R.mipmap.icon_rect_select)
						} else {
							setBackgroundResource(R.mipmap.icon_rect_not_select)
						}
						setOnClickListener {
							if (data.itemStatus == 0) {
								data.itemStatus = 1
							} else if (data.itemStatus == 1) {
								data.itemStatus = 0
							}
							SelectPersonsFromOrgFragment.setDepChildSelect(data, data.itemStatus)
							companyOrgAdapter?.notifyDataSetChanged()
							initBottomBar()
						}
					}
				}
			}
		rcvCompanyOrgDetails.layoutManager = LinearLayoutManager(context)
		rcvCompanyOrgDetails.adapter = companyOrgAdapter
		companyOrgAdapter!!.setOnItemClickListener { o, _, holder ->
			TestConstants.curShowSubdept = o as Subdept
			Navigation.findNavController(holder.itemView)
				.navigate(R.id.action_selectFromPersonsFragment_to_selectPersonsFromOrgFragment)
		}
	}

	var localPersonList: ArrayList<Local>? = ArrayList()
	var originalLocalPersonList: ArrayList<Local>? = ArrayList()
	var localContactPersonAdapter: AbstractSimpleAdapter<Local>? = null

	/**
	 * 加载本地联系人列表
	 */
	private fun initRcvContactPerson() {
		localPersonList = ArrayList(TestConstants.testContactBean.local)
		originalLocalPersonList = ArrayList(TestConstants.testContactBean.local)
		localContactPersonAdapter =
			object : AbstractSimpleAdapter<Local>(context, localPersonList, R.layout.item_work_group) {
				override fun onBindViewHolder(holder: ViewHolder?, data: Local?, curPosition: Int) {
					holder!!.getView<TextView>(R.id.tvItemGroupName).text = data?.name
					//暂时先用名字进行判断，后期改成unid
					holder.getView<ImageView>(R.id.ivItemSelectStatus)
						?.apply {
							//暂时先用名字进行判断，后期改成unid
							if (data!!.itemStatus == SelectPersonsFromOrgFragment.ITEM_SELECT) {
								setBackgroundResource(R.mipmap.icon_rect_select)
							} else {
								setBackgroundResource(R.mipmap.icon_rect_not_select)
							}
						}
				}
			}
		rcvContactPerson.layoutManager = LinearLayoutManager(context)
		rcvContactPerson.adapter = localContactPersonAdapter
		localContactPersonAdapter!!.setOnItemClickListener { o, _, _ ->
			switchPersonStatus(o)
			localContactPersonAdapter!!.notifyDataSetChanged()
			initBottomBar()
		}
	}

	/**
	 * 改变底部栏的UI状态
	 */
	private fun initBottomBar() {
		val selectSize = TestConstants.getSelectPersonsSet()!!.size
		if (selectSize > 0) {
			tvCurSelectGroup.text = "已选择：${selectSize}个人"
			btnConfirm.tag = "yes"
			btnConfirm.setBackgroundResource(R.drawable.drawable_blue_4dp_rect)
		} else {
			tvCurSelectGroup.text = getString(R.string.cur_select_persons)
			btnConfirm.tag = "no"
			btnConfirm.setBackgroundResource(R.drawable.drawable_deep_gray_4dp_rect)
		}
	}

	/**
	 * 切换人员的选中状态
	 */
	private fun switchPersonStatus(data: Any) {
		OrgPersonUtils.switchPersonStatus(data)
		if (data is Local) {
			if (data.itemStatus == ITEM_SELECT) {
				synchronized(SelectFromPersonsFragment::class.java) {
					val person = TestConstants.getAllContactPersonsMaps()[data.number]
					when (person) {
						is SubcontactX -> {
							person.itemStatus = ITEM_SELECT
							SelectPersonsFromOrgFragment.setUpOrgSelectAndSetUpperSelect(data.deptid)
						}
						is Subcontact -> {
							person.itemStatus = ITEM_SELECT
							SelectPersonsFromOrgFragment.setUpOrgSelectAndSetUpperSelect(data.deptid)
						}
					}
				}

			} else if (data.itemStatus == ITEM_NOT_SELECT) {
				synchronized(SelectFromPersonsFragment::class.java) {
					val person = TestConstants.getAllContactPersonsMaps()[data.number]
					val personDep = TestConstants.getAllOrgMaps()?.get(data.deptid)
					when (person) {
						is SubcontactX -> {
							person.itemStatus = ITEM_NOT_SELECT
							when (personDep) {
								is Subdept -> {
									var selectCount = 0
									personDep.subcontact.forEach { itemPerson ->
										if (itemPerson.itemStatus == ITEM_SELECT) {
											selectCount++
										}
									}
									personDep.subdept?.forEach { itemSub ->
										if (itemSub.itemStatus == ITEM_SELECT) {
											selectCount++
										}
									}
									if (selectCount > 0) {
										//doNothing
									} else {
//										personDep.itemStatus= ITEM_NOT_SELECT
										SelectPersonsFromOrgFragment.setUpOrgNotSelectAndCheckUpperOrgStatus(person.deptid)
									}
								}
								is SubdeptX -> {
									var selectCount = 0
									personDep.subcontact.forEach { itemPerson ->
										if (itemPerson.itemStatus == ITEM_SELECT) {
											selectCount++
										}
									}
									if (selectCount > 0) {
										//doNothing
									} else {
//										personDep.itemStatus= ITEM_NOT_SELECT
										SelectPersonsFromOrgFragment.setUpOrgNotSelectAndCheckUpperOrgStatus(person.deptid)
									}
								}
							}
						}
						is Subcontact -> {
							person.itemStatus = ITEM_NOT_SELECT
							when (personDep) {
								is Subdept -> {
									var selectCount = 0
									personDep.subcontact.forEach { itemPerson ->
										if (itemPerson.itemStatus == ITEM_SELECT) {
											selectCount++
										}
									}
									personDep.subdept?.forEach { itemSub ->
										if (itemSub.itemStatus == ITEM_SELECT) {
											selectCount++
										}
									}
									if (selectCount > 0) {
										//doNothing
									} else {
//										personDep.itemStatus= ITEM_NOT_SELECT
										SelectPersonsFromOrgFragment.setUpOrgNotSelectAndCheckUpperOrgStatus(person.deptid)
									}
								}
								is SubdeptX -> {
									var selectCount = 0
									personDep.subcontact.forEach { itemPerson ->
										if (itemPerson.itemStatus == ITEM_SELECT) {
											selectCount++
										}
									}
									if (selectCount > 0) {
										//doNothing
									} else {
//										personDep.itemStatus= ITEM_NOT_SELECT
										SelectPersonsFromOrgFragment.setUpOrgNotSelectAndCheckUpperOrgStatus(person.deptid)
									}
								}
							}
						}
					}
				}
			}
		}

		ToastUtils.show("当前选中人数有${TestConstants.getSelectPersonsSet()?.size}个")
	}

	/**
	 * 控件的点击事件
	 */
	private fun initClick() {
		tvCancel.setOnClickListener {
			if ("返回" == tvCancel.text) {
				SelectFromPersonConstants.curStep = 0
				ctlOrganizationDetailLayout.visibility = View.GONE
				ctlOrganizationDetailLayout.startAnimation(
					AnimationUtils.loadAnimation(
						context,
						R.anim.slide_out_bottom
					)
				)
				tvCancel.text = "取消"
			} else {
				Navigation.findNavController(it).navigateUp()
			}

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
					localContactPersonAdapter!!.resetData(originalLocalPersonList)
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

		ctlCompanyOrgDesc.setOnClickListener {
			jumpToOrgDetails()
		}

		if (1 == SelectFromPersonConstants.curStep) {
			jumpToOrgDetails()
			localContactPersonAdapter?.notifyDataSetChanged()
			companyOrgAdapter?.notifyDataSetChanged()
			initBottomBar()
		} else {
			//doNothing
		}

	}

	override fun onDestroyView() {
		isAlive = false
		super.onDestroyView()
	}

	/**
	 * 根据当前的步骤，恢复界面
	 */
	private fun jumpToOrgDetails() {
		SelectFromPersonConstants.curStep = 1
		tvCancel.text = "返回"
		ctlOrganizationDetailLayout.visibility = View.VISIBLE
		ctlOrganizationDetailLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom))
	}

	override fun onDestroy() {
		super.onDestroy()
		doWhenDestroy()
	}

	/**
	 * fragment销毁时做点事情
	 */
	private fun doWhenDestroy() {
		SelectFromPersonConstants.curStep = 0
		TestConstants.testContactBean = Gson().fromJson(TestConstants.json, GongXunGroupInfoBean::class.java)
		TestConstants.allOrgMap = null
	}

	/**
	 * 根据输入，搜索目标,更新列表
	 */
	private fun doSearchGroup(groupName: String) {
		val tempSelectList = ArrayList<Local>()
		for (item in localPersonList!!) {
			if (item.name.contains(groupName)) {
				tempSelectList.add(item)
			}
		}
		localContactPersonAdapter!!.resetData(tempSelectList)
	}


}
