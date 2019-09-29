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
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.XPopup
import com.microsys.imb.remote.R
import com.microsys.imb.remote.base.BaseNavigationFragment
import com.microsys.imb.remote.bean.*
import com.microsys.imb.remote.constants.TestConstants
import com.microsys.imb.remote.ui.adapter.recycler.AbstractSimpleAdapter
import com.microsys.imb.remote.ui.adapter.recycler.ViewHolder
import com.microsys.imb.remote.utils.OrgPersonUtils
import com.microsys.imb.remote.widgets.dialog.PrepareCallDialog
import com.sunday.common.utils.StatusBarUtils
import com.sunday.common.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_select_persons_from_org.*

class SelectPersonsFromOrgFragment : BaseNavigationFragment() {

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_select_persons_from_org, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initData()
	}

	/**
	 * 加载一些数据
	 */
	private fun initData() {
		StatusBarUtils.initSystemBarColor(this.activity as Activity?, false, Color.WHITE)

		orgTitleList?.add(TestConstants.testContactBean.topdept)
		orgTitleList?.add(TestConstants.curShowSubdept!!)
		curShowOrgBean = TestConstants.curShowSubdept!!

		initRcvOrgTitle()

		initRcvOrgDetails()

		initBottomBar()

		initClick()
	}

	/**
	 * 动态改变底部栏目的UI
	 */
	private fun initBottomBar() {
		val size = TestConstants.getSelectPersonsSet()!!.size
		if (size > 0) {
			tvCurSelectGroup.text = "已选择：" + size + "个人"
			btnConfirm.tag = "yes"
			btnConfirm.setBackgroundResource(R.drawable.drawable_blue_4dp_rect)
		} else {
			btnConfirm.tag = "no"
			btnConfirm.setBackgroundResource(R.drawable.drawable_deep_gray_4dp_rect)
		}
	}


	//组级别标题rc的数据源和adapter
	var orgTitleList: ArrayList<Any>? = ArrayList()
	private var adapterOrgTitle: AbstractSimpleAdapter<Any>? = null

	//当前被选中的上级部门
	private var curShowOrgBean: Subdept? = null

	/**
	 * 加载标题列表
	 */
	private fun initRcvOrgTitle() {
		adapterOrgTitle = object : AbstractSimpleAdapter<Any>(context, orgTitleList, R.layout.item_title_item) {
			override fun onBindViewHolder(holder: ViewHolder?, data: Any?, curPosition: Int) {
				dealOrgTitleData(holder!!, data!!, curPosition)
			}
		}
		rcvTitleFlipper.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
		rcvTitleFlipper.adapter = adapterOrgTitle

		adapterOrgTitle!!.setOnItemClickListener { o, position, holder ->
			dealOrgTitleTapClickEvent(o, holder, position)
		}
	}

	/**
	 * 处理标题rv的数据
	 * @param data 数据
	 * @param holder holder
	 * @curPosition 当前下标
	 */
	private fun dealOrgTitleData(holder: ViewHolder, data: Any, curPosition: Int) {
		val tvOrgTitle = holder.getView<TextView>(R.id.tvOrgTitle)
		tvOrgTitle!!.text = when (data) {
			is Topdept -> data.name
			is SubdeptX -> "/" + (data).name
			is Subdept -> "/" + (data).name
			else -> ""
		}

		if (curPosition == adapterOrgTitle!!.itemCount - 1) {
			tvOrgTitle.setTextColor(Color.parseColor("#999999"))
		} else {
			tvOrgTitle.setTextColor(Color.parseColor("#4DA6FE"))
		}
	}

	/**
	 * 处理标题rv的点击事件
	 * @param o 数据
	 * @param holder holder
	 * @position 当前下标
	 */
	private fun dealOrgTitleTapClickEvent(o: Any, holder: ViewHolder?, position: Int) {
		val orgName =
			when (o) {
				is Topdept -> {
					Navigation.findNavController(rcvTitleFlipper).navigateUp()
					o.name
				}
				is SubdeptX -> {
					if (o.name == curShowOrgBean?.name) {
						//doNothing
					} else {
						curShowOrgBean = toSubdept(o)
						contactList?.clear()
						contactList?.addAll(o.subcontact)
						adapterRcvOrgDetails?.resetData(contactList)
						val tempList: ArrayList<Any>? = ArrayList()
						for (index in 0 until orgTitleList?.size!!) {
							if (index <= position) {
								tempList?.add(orgTitleList?.get(index)!!)
							}
						}
						orgTitleList?.clear()
						orgTitleList?.addAll(tempList!!)
						adapterOrgTitle?.notifyDataSetChanged()
					}
					o.name
				}
				is Subdept -> {
					if (o.name == curShowOrgBean?.name) {
						//doNothing
					} else {
						curShowOrgBean = o
						contactList?.clear()
						if (null != curShowOrgBean!!.subdept) {
							contactList?.addAll(curShowOrgBean!!.subdept!!)
						}

						if (null != curShowOrgBean!!.subcontact && curShowOrgBean!!.subcontact.isNotEmpty()) {
							contactList?.addAll(curShowOrgBean!!.subcontact)
						}

						adapterRcvOrgDetails?.notifyDataSetChanged()


						val tempList: ArrayList<Any>? = ArrayList()
						for (index in 0 until orgTitleList?.size!!) {
							if (index <= position) {
								tempList?.add(orgTitleList?.get(index)!!)
							}
						}
						orgTitleList?.clear()
						orgTitleList?.addAll(tempList!!)
						adapterOrgTitle?.notifyDataSetChanged()

					}
					o.name
				}
				else -> ""
			}
	}

	/**
	 * 将subdeptX转化为subdept
	 */
	private fun toSubdept(o: SubdeptX): Subdept? {
		val subcontactList: ArrayList<Subcontact>? = ArrayList()
		for (item in o.subcontact) {
			subcontactList?.add(Subcontact(item.itemStatus, item.deptid, item.name, item.number, item.online))
		}
		return Subdept(o.itemStatus, o.id, o.name, subcontactList!!, null, o.supid)
	}


	/**
	 * 联系人rcv的数据源和adapter
	 */
	var contactList: ArrayList<Any>? = ArrayList()
	var adapterRcvOrgDetails: AbstractSimpleAdapter<Any>? = null

	companion object {
		//item是否选中标记
		const val ITEM_SELECT = 1
		const val ITEM_NOT_SELECT = 0


		/**
		 * 检查同级组的对象是否有选中状态
		 * @param data 被检查的组
		 * @param myId 发起请求的组或成员对象的ID
		 */
		fun checkSameLevelOrgItemCheckStatus(data: Any, myId: Int, isDep: Boolean): Boolean {
			when (data) {
				is SubdeptX -> {
					for (item in data.subcontact) {
						if (item.itemStatus == ITEM_SELECT) {
							return true
						}
					}
				}
				is Subdept -> {
					for (item in data.subcontact) {
						if (item.itemStatus == ITEM_SELECT) {
							return true
						}
					}
					if (null != data.subdept && data.subdept.isNotEmpty()) {
						for (item in data.subdept) {
							if (item.itemStatus == ITEM_SELECT && item.id != myId) {
								return true
							}
						}
					}
				}
			}
			return false
		}


		/**
		 * 将上级组设置为未选中，并检查上级组的组内选中状态
		 * 如果有，不做操作
		 * 如果没有，递归
		 * @param upOrgId 调起的当前组ID
		 */
		fun setUpOrgNotSelectAndCheckUpperOrgStatus(upOrgId: Int) {
			if (-1 == upOrgId || 0 == upOrgId) {
				return
			}
			when (val upData = TestConstants.getAllOrgMaps()!!.get(upOrgId) ?: return) {
				is SubdeptX -> {
					upData.itemStatus = ITEM_NOT_SELECT
					if (upData.supid == 0) {
						return
					}
					val upperData = TestConstants.getAllOrgMaps()!!.get(upData.supid) ?: return
					if (checkSameLevelOrgItemCheckStatus(upperData, upData.id, true)) {
						//doNothing
					} else {
						setUpOrgNotSelectAndCheckUpperOrgStatus(upData.supid)
						return
					}
				}
				is Subdept -> {
					upData.itemStatus = ITEM_NOT_SELECT

					if (upData.supid == 0) {
						return
					}

					val upperData = TestConstants.getAllOrgMaps()!!.get(upData.supid) ?: return


					if (checkSameLevelOrgItemCheckStatus(upperData, upData.id, true)) {
						//doNothing
					} else {
						setUpOrgNotSelectAndCheckUpperOrgStatus(upData.id)
						return
					}
				}
			}
		}

		/**
		 * 当前组是选中状态，当前组的上级组的状态设置为选中,并且递归
		 * @param targetDepId   上级组的ID
		 */
		fun setUpOrgSelectAndSetUpperSelect(targetDepId: Int) {
			if (-1 == targetDepId || 0 == targetDepId) {
				return
			}
			var findUpDep = false
			for (itemDep in TestConstants.testContactBean.topdept.subdept) {
				if (findUpDep) {
					break
				}
				if (targetDepId == itemDep.id) {
					findUpDep = true
					itemDep.itemStatus = ITEM_SELECT
					//doNothing,已经是最顶级的组了
				} else {
					if (null != itemDep.subdept && itemDep.subdept.isNotEmpty()) {
						for (innerItemDep in itemDep.subdept) {
							if (findUpDep) {
								break
							}
							if (targetDepId == innerItemDep.id) {
								findUpDep = true
								if (innerItemDep.itemStatus == ITEM_NOT_SELECT) {
									innerItemDep.itemStatus = ITEM_SELECT
									setUpOrgSelectAndSetUpperSelect(innerItemDep.supid)
									break
								} else {
									//doNothing
								}
							}
						}
					}
				}
			}
		}

		/**
		 * 将当前组下面的所有对象的状态切换为选中或非选中状态
		 * @param data 当前组
		 * @param status 需要切换的状态
		 */
		fun setDepChildSelect(data: Any, status: Int) {
			when (data) {
				is SubdeptX -> {
					for (personItem in data.subcontact) {
						personItem.itemStatus = status
						if (status == ITEM_SELECT) {
							TestConstants.addPersonToSelectPersonSet(personItem)
						} else if (status == ITEM_NOT_SELECT) {
							TestConstants.removePersonFromSelectPersonSet(personItem)
						}
					}
				}
				is Subdept -> {
					for (personItem in data.subcontact) {
						personItem.itemStatus = status
						if (status == ITEM_SELECT) {
							TestConstants.addPersonToSelectPersonSet(personItem)
						} else if (status == ITEM_NOT_SELECT) {
							TestConstants.removePersonFromSelectPersonSet(personItem)
						}
					}
					if (null != data.subdept && data.subdept.isNotEmpty()) {
						for (depItem in data.subdept) {
							depItem.itemStatus = status
							setDepChildSelect(depItem, status)
						}
					}
				}
			}
		}
	}

	/**
	 * 处理联系人rv的数据
	 * @param data 数据
	 * @param holder holder
	 * @curPosition 当前下标
	 */
	private fun dealOrgDetailsData(data: Any, holder: ViewHolder, curPosition: Int) {
		when (data) {
			is SubdeptX -> {
				holder.getView<TextView>(R.id.tvItemGroupName).text = data.name

				holder.getView<ImageView>(R.id.ivItemSelectStatus)
					?.apply {
						//暂时先用名字进行判断，后期改成unid
						if (data.itemStatus == ITEM_SELECT) {
							setBackgroundResource(R.mipmap.icon_rect_select)
						} else {
							setBackgroundResource(R.mipmap.icon_rect_not_select)
						}
						setOnClickListener {
							val upData = TestConstants.getAllOrgMaps()!!.get(data.supid)
							if (data.itemStatus == ITEM_NOT_SELECT) {
								data.itemStatus = ITEM_SELECT
								setDepChildSelect(status = ITEM_SELECT, data = data)
								if (checkSameLevelOrgItemCheckStatus(upData, data.id, true)) {
									//doNothing
								} else {
									setUpOrgSelectAndSetUpperSelect(data.supid)
								}
							} else if (data.itemStatus == ITEM_SELECT) {
								data.itemStatus = ITEM_NOT_SELECT
								setDepChildSelect(status = ITEM_NOT_SELECT, data = data)

								if (checkSameLevelOrgItemCheckStatus(upData, data.id, true)) {
									//doNothing
								} else {
									setUpOrgNotSelectAndCheckUpperOrgStatus(data.supid)
								}
							}
							adapterRcvOrgDetails!!.notifyDataSetChanged()
						}
					}

				holder.getView<ImageView>(R.id.ivItemAvatar).visibility = View.GONE
				holder.getView<ImageView>(R.id.ivArrow).visibility = View.VISIBLE
			}
			is Subdept -> {
				holder.getView<TextView>(R.id.tvItemGroupName).text = data.name

				holder.getView<ImageView>(R.id.ivItemSelectStatus)
					?.apply {
						//暂时先用名字进行判断，后期改成unid
						if (data.itemStatus == ITEM_SELECT) {
							setBackgroundResource(R.mipmap.icon_rect_select)
						} else {
							setBackgroundResource(R.mipmap.icon_rect_not_select)
						}

						setOnClickListener {
							val upData = TestConstants.getAllOrgMaps()!!.get(data.supid)
							if (data.itemStatus == ITEM_NOT_SELECT) {
								data.itemStatus = ITEM_SELECT
								setDepChildSelect(status = ITEM_SELECT, data = data)
								if (checkSameLevelOrgItemCheckStatus(upData, data.id, true)) {
									//doNothing
								} else {
									setUpOrgSelectAndSetUpperSelect(data.supid)
								}
							} else if (data.itemStatus == ITEM_SELECT) {
								data.itemStatus = ITEM_NOT_SELECT
								setDepChildSelect(status = ITEM_NOT_SELECT, data = data)

								if (checkSameLevelOrgItemCheckStatus(upData, data.id, true)) {
									//doNothing
								} else {
									setUpOrgNotSelectAndCheckUpperOrgStatus(data.supid)
								}
							}

							adapterRcvOrgDetails!!.notifyDataSetChanged()
						}
					}

				holder.getView<ImageView>(R.id.ivItemAvatar).visibility = View.GONE
				holder.getView<ImageView>(R.id.ivArrow).visibility = View.VISIBLE
			}
			is Subcontact -> {
				holder.getView<TextView>(R.id.tvItemGroupName).text = data.name

				holder.getView<ImageView>(R.id.ivItemSelectStatus)
					?.apply {
						//暂时先用名字进行判断，后期改成unid
						if (data.itemStatus == ITEM_SELECT) {
							setBackgroundResource(R.mipmap.icon_rect_select)
						} else {
							setBackgroundResource(R.mipmap.icon_rect_not_select)
						}
					}

				holder.getView<ImageView>(R.id.ivItemAvatar).visibility = View.VISIBLE
				holder.getView<ImageView>(R.id.ivArrow).visibility = View.GONE
			}
			is SubcontactX -> {
				holder.getView<TextView>(R.id.tvItemGroupName).text = data.name

				holder.getView<ImageView>(R.id.ivItemSelectStatus)
					?.apply {
						//暂时先用名字进行判断，后期改成unid
						if (data.itemStatus == ITEM_SELECT) {
							setBackgroundResource(R.mipmap.icon_rect_select)
						} else {
							setBackgroundResource(R.mipmap.icon_rect_not_select)
						}
					}

				holder.getView<ImageView>(R.id.ivItemAvatar).visibility = View.VISIBLE
				holder.getView<ImageView>(R.id.ivArrow).visibility = View.GONE
			}
		}
		initBottomBar()
	}

	/**
	 * 处理联系人rv的点击事件
	 * @param data 数据
	 * @param holder holder
	 * @position 当前下标
	 */
	private fun dealOrgDetailsTapClickEvent(data: Any, holder: ViewHolder, position: Int) {
		when (data) {
			is SubdeptX -> {
				orgTitleList?.add(data)
				adapterOrgTitle?.notifyDataSetChanged()
				curShowOrgBean = toSubdept(data)
				contactList?.clear()
				contactList?.addAll(data.subcontact)
				adapterRcvOrgDetails?.notifyDataSetChanged()
			}
			is Subdept -> {
				orgTitleList?.add(data)
				adapterOrgTitle?.notifyDataSetChanged()
				curShowOrgBean = data
				contactList?.clear()
				contactList?.addAll(data.subcontact)
				adapterRcvOrgDetails?.notifyDataSetChanged()

			}
			is Subcontact -> {
				if (data.itemStatus == 0) {
					switchPersonStatus(data)
					setUpOrgSelectAndSetUpperSelect(data.deptid)
				} else if (data.itemStatus == 1) {
					switchPersonStatus(data)
					val sameLevelOrg = TestConstants.getAllOrgMaps()!!.get(data.deptid)
					if (checkSameLevelOrgItemCheckStatus(sameLevelOrg!!, data.number.toInt(), false)) {
						//doNothing
					} else {
						setUpOrgNotSelectAndCheckUpperOrgStatus(data.deptid)
					}
				}

				adapterRcvOrgDetails!!.setSelectList(data.name)
				adapterRcvOrgDetails!!.notifyDataSetChanged()
			}
			is SubcontactX -> {
				if (data.itemStatus == 0) {
					switchPersonStatus(data)
					setUpOrgSelectAndSetUpperSelect(data.deptid)
				} else if (data.itemStatus == 1) {
					switchPersonStatus(data)
					val sameLevelOrg = TestConstants.getAllOrgMaps()!!.get(data.deptid)
					if (checkSameLevelOrgItemCheckStatus(sameLevelOrg!!, data.number.toInt(), false)) {
						//doNothing
					} else {
						setUpOrgNotSelectAndCheckUpperOrgStatus(data.deptid)
					}
				}

				adapterRcvOrgDetails!!.notifyDataSetChanged()
			}
		}
	}

	/**
	 * 加载联系人Rv,绑定数据和点击事件
	 */
	private fun initRcvOrgDetails() {
		if (null != curShowOrgBean!!.subdept) {
			contactList?.addAll(curShowOrgBean!!.subdept!!)
		}
		if (null != curShowOrgBean!!.subcontact && curShowOrgBean!!.subcontact.isNotEmpty()) {
			contactList?.addAll(curShowOrgBean!!.subcontact)
		}

		adapterRcvOrgDetails =
			object : AbstractSimpleAdapter<Any>(context, contactList, R.layout.item_work_group_and_members) {
				override fun onBindViewHolder(holder: ViewHolder?, data: Any?, curPosition: Int) {
					dealOrgDetailsData(data!!, holder!!, curPosition)
				}
			}
		rcvOrgDetails.layoutManager = LinearLayoutManager(context)
		rcvOrgDetails.adapter = adapterRcvOrgDetails

		adapterRcvOrgDetails!!.setOnItemClickListener { o, position, holder ->
			dealOrgDetailsTapClickEvent(o, holder, position)
		}
	}

	/**
	 * 控件的点击事件
	 */
	private fun initClick() {
		tvBack.setOnClickListener {
			if (adapterOrgTitle!!.itemCount > 2) {
				dealOrgTitleTapClickEvent(
					adapterOrgTitle!!.getmDatas()[adapterOrgTitle!!.itemCount - 2]!!,
					null,
					adapterOrgTitle!!.itemCount - 2
				)
				ToastUtils.show("返回上一层")
			} else {
				Navigation.findNavController(it).navigateUp()
			}
		}

		tvClose.setOnClickListener {
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
//		val tempSelectList = ArrayList<String>()
//		for (item in personList) {
//			if (item.contains(groupName)) {
//				tempSelectList.add(item)
//			}
//		}
	}


	/**
	 * 切换人员的选中状态
	 */
	private fun switchPersonStatus(data: Any) {
		OrgPersonUtils.switchPersonStatus(data)
		ToastUtils.show("当前选中人数有${TestConstants.getSelectPersonsSet()?.size}个")
	}


}
