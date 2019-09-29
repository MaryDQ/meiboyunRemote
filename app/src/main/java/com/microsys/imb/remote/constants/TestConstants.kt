package com.microsys.imb.remote.constants

import android.util.SparseArray
import com.google.gson.Gson
import com.microsys.imb.remote.bean.GongXunGroupInfoBean
import com.microsys.imb.remote.bean.Subdept

class TestConstants {
	companion object {
		const val json =
			"{\"topdept\":{\"id\":0,\"name\":\"组织结构\",\"subdept\":[{\"id\":74,\"name\":\"通宽广\",\"subcontact\":[{\"deptid\":74,\"name\":\"毛积法\",\"number\":\"83064\",\"online\":false}],\"subdept\":[{\"id\":90,\"name\":\"工程测试\",\"subcontact\":[{\"deptid\":90,\"name\":\"徐凤清\",\"number\":\"83075\",\"online\":false}],\"supid\":74},{\"id\":92,\"name\":\"广播视频会议\",\"subcontact\":[{\"deptid\":92,\"name\":\"李季\",\"number\":\"83076\",\"online\":false}],\"supid\":74},{\"id\":76,\"name\":\"财务部\",\"subcontact\":[{\"deptid\":76,\"name\":\"程勇华\",\"number\":\"83065\",\"online\":false}],\"supid\":74},{\"id\":78,\"name\":\"办公室\",\"subcontact\":[{\"deptid\":78,\"name\":\"余万里\",\"number\":\"83066\",\"online\":false}],\"supid\":74},{\"id\":80,\"name\":\"研发部\",\"subcontact\":[{\"deptid\":80,\"name\":\"方志刚\",\"number\":\"83067\",\"online\":false}],\"supid\":74},{\"id\":82,\"name\":\"生产管理\",\"subcontact\":[{\"deptid\":82,\"name\":\"叶素华\",\"number\":\"83071\",\"online\":false},{\"deptid\":82,\"name\":\"章小明\",\"number\":\"83072\",\"online\":false},{\"deptid\":82,\"name\":\"郑美英\",\"number\":\"83073\",\"online\":false},{\"deptid\":82,\"name\":\"郑小军\",\"number\":\"83074\",\"online\":false},{\"deptid\":82,\"name\":\"毛小妹\",\"number\":\"83068\",\"online\":false},{\"deptid\":82,\"name\":\"汪银霞\",\"number\":\"83069\",\"online\":false},{\"deptid\":82,\"name\":\"王怡闻\",\"number\":\"83070\",\"online\":false}],\"supid\":74}],\"supid\":0},{\"id\":38,\"name\":\"研究院\",\"subcontact\":[{\"deptid\":38,\"name\":\"刘秋华\",\"number\":\"83032\",\"online\":false}],\"subdept\":[{\"id\":66,\"name\":\"测试部\",\"subcontact\":[{\"deptid\":66,\"name\":\"徐博\",\"number\":\"83061\",\"online\":false},{\"deptid\":66,\"name\":\"张金龙\",\"number\":\"83062\",\"online\":false},{\"deptid\":66,\"name\":\"钟佳琪\",\"number\":\"83057\",\"online\":false},{\"deptid\":66,\"name\":\"邓继武\",\"number\":\"83058\",\"online\":false},{\"deptid\":66,\"name\":\"詹俊波\",\"number\":\"83063\",\"online\":true},{\"deptid\":66,\"name\":\"毛增慧\",\"number\":\"83059\",\"online\":false},{\"deptid\":66,\"name\":\"吴琰栋\",\"number\":\"83060\",\"online\":false}],\"supid\":38},{\"id\":40,\"name\":\"主任\",\"subcontact\":[{\"deptid\":40,\"name\":\"钱海良\",\"number\":\"83033\",\"online\":false}],\"supid\":38},{\"id\":42,\"name\":\"研发部\",\"subcontact\":[{\"deptid\":42,\"name\":\"宋怡婷\",\"number\":\"83047\",\"online\":false},{\"deptid\":42,\"name\":\"苏铮俊\",\"number\":\"83048\",\"online\":false},{\"deptid\":42,\"name\":\"许文强\",\"number\":\"83049\",\"online\":false},{\"deptid\":42,\"name\":\"严斌\",\"number\":\"83050\",\"online\":false},{\"deptid\":42,\"name\":\"余纯洋\",\"number\":\"83051\",\"online\":false},{\"deptid\":42,\"name\":\"虞景皓\",\"number\":\"83052\",\"online\":false},{\"deptid\":42,\"name\":\"张学乾\",\"number\":\"83053\",\"online\":false},{\"deptid\":42,\"name\":\"占立晨\",\"number\":\"83054\",\"online\":false},{\"deptid\":42,\"name\":\"赵超阳\",\"number\":\"83055\",\"online\":false},{\"deptid\":42,\"name\":\"赵灵刚\",\"number\":\"83056\",\"online\":false},{\"deptid\":42,\"name\":\"陈子楷\",\"number\":\"83034\",\"online\":false},{\"deptid\":42,\"name\":\"董然\",\"number\":\"83035\",\"online\":false},{\"deptid\":42,\"name\":\"龚循\",\"number\":\"83036\",\"online\":false},{\"deptid\":42,\"name\":\"胡智慧\",\"number\":\"83037\",\"online\":false},{\"deptid\":42,\"name\":\"孔令辉\",\"number\":\"83038\",\"online\":false},{\"deptid\":42,\"name\":\"刘猛\",\"number\":\"83039\",\"online\":false},{\"deptid\":42,\"name\":\"刘希哲\",\"number\":\"83040\",\"online\":false},{\"deptid\":42,\"name\":\"卢晨\",\"number\":\"83041\",\"online\":false},{\"deptid\":42,\"name\":\"马李鑫\",\"number\":\"83042\",\"online\":false},{\"deptid\":42,\"name\":\"缪彬威\",\"number\":\"83043\",\"online\":false},{\"deptid\":42,\"name\":\"欧阳家桥\",\"number\":\"83044\",\"online\":false},{\"deptid\":42,\"name\":\"齐甜甜\",\"number\":\"83045\",\"online\":false},{\"deptid\":42,\"name\":\"商克亮\",\"number\":\"83046\",\"online\":false}],\"supid\":38}],\"supid\":0},{\"id\":94,\"name\":\"美播云\",\"subcontact\":[{\"deptid\":94,\"name\":\"叶福君\",\"number\":\"83078\",\"online\":false}],\"subdept\":[{\"id\":98,\"name\":\"销售部\",\"subcontact\":[{\"deptid\":98,\"name\":\"李传鹏\",\"number\":\"83079\",\"online\":false}],\"supid\":94},{\"id\":96,\"name\":\"客服部\",\"subcontact\":[{\"deptid\":96,\"name\":\"黄晓慧\",\"number\":\"83077\",\"online\":false}],\"supid\":94},{\"id\":100,\"name\":\"调试部\",\"subcontact\":[{\"deptid\":100,\"name\":\"2011\",\"number\":\"2011\",\"online\":false},{\"deptid\":100,\"name\":\"厂家测试终端账号_1\",\"number\":\"2009\",\"online\":false},{\"deptid\":100,\"name\":\"2007\",\"number\":\"2007\",\"online\":false},{\"deptid\":100,\"name\":\"H8A_TV\",\"number\":\"2000\",\"online\":false},{\"deptid\":100,\"name\":\"H8U_TV\",\"number\":\"2001\",\"online\":true},{\"deptid\":100,\"name\":\"2006\",\"number\":\"2006\",\"online\":false},{\"deptid\":100,\"name\":\"华为平板\",\"number\":\"2002\",\"online\":false},{\"deptid\":100,\"name\":\"会议摄像头\",\"number\":\"2003\",\"online\":false},{\"deptid\":100,\"name\":\"2005\",\"number\":\"2005\",\"online\":false},{\"deptid\":100,\"name\":\"2010\",\"number\":\"2010\",\"online\":false},{\"deptid\":100,\"name\":\"2004\",\"number\":\"2004\",\"online\":false},{\"deptid\":100,\"name\":\"厂家测试会议平板\",\"number\":\"2008\",\"online\":false}],\"supid\":94}],\"supid\":0},{\"id\":1,\"name\":\"总经办\",\"subcontact\":[{\"deptid\":1,\"name\":\"吴婷\",\"number\":\"83002\",\"online\":false},{\"deptid\":1,\"name\":\"余文谊\",\"number\":\"83001\",\"online\":false},{\"deptid\":1,\"name\":\"吴嬿\",\"number\":\"83003\",\"online\":false}],\"supid\":0},{\"id\":5,\"name\":\"迈可行\",\"subdept\":[{\"id\":6,\"name\":\"销售部\",\"subcontact\":[{\"deptid\":6,\"name\":\"王涛\",\"number\":\"83019\",\"online\":false},{\"deptid\":6,\"name\":\"黄进\",\"number\":\"83008\",\"online\":false},{\"deptid\":6,\"name\":\"焦俊杰\",\"number\":\"83009\",\"online\":false},{\"deptid\":6,\"name\":\"许文波\",\"number\":\"83012\",\"online\":false},{\"deptid\":6,\"name\":\"卢聪杰\",\"number\":\"83018\",\"online\":false},{\"deptid\":6,\"name\":\"林涛\",\"number\":\"83010\",\"online\":false},{\"deptid\":6,\"name\":\"刘月平\",\"number\":\"83011\",\"online\":false},{\"deptid\":6,\"name\":\"岳晗\",\"number\":\"83015\",\"online\":false},{\"deptid\":6,\"name\":\"李志慧\",\"number\":\"83017\",\"online\":false},{\"deptid\":6,\"name\":\"陈新辉\",\"number\":\"83004\",\"online\":false},{\"deptid\":6,\"name\":\"王挺\",\"number\":\"83020\",\"online\":false},{\"deptid\":6,\"name\":\"丁静涛\",\"number\":\"83005\",\"online\":false},{\"deptid\":6,\"name\":\"袁帅\",\"number\":\"83014\",\"online\":false},{\"deptid\":6,\"name\":\"周斌\",\"number\":\"83016\",\"online\":false},{\"deptid\":6,\"name\":\"何旭初\",\"number\":\"83006\",\"online\":false},{\"deptid\":6,\"name\":\"何长飞\",\"number\":\"83007\",\"online\":false},{\"deptid\":6,\"name\":\"许叶佳\",\"number\":\"83013\",\"online\":false}],\"supid\":5},{\"id\":24,\"name\":\"财务部\",\"subcontact\":[{\"deptid\":24,\"name\":\"施卓敏\",\"number\":\"83022\",\"online\":false},{\"deptid\":24,\"name\":\"余天标\",\"number\":\"83024\",\"online\":false},{\"deptid\":24,\"name\":\"吴琦\",\"number\":\"83025\",\"online\":false},{\"deptid\":24,\"name\":\"冯依琳\",\"number\":\"83023\",\"online\":false}],\"supid\":5},{\"id\":30,\"name\":\"综合管理\",\"subcontact\":[{\"deptid\":30,\"name\":\"陈莹雪\",\"number\":\"83026\",\"online\":false},{\"deptid\":30,\"name\":\"金国庆\",\"number\":\"83027\",\"online\":false},{\"deptid\":30,\"name\":\"徐洁\",\"number\":\"83028\",\"online\":false}],\"supid\":5},{\"id\":34,\"name\":\"系统调试\",\"subcontact\":[{\"deptid\":34,\"name\":\"秦康雯\",\"number\":\"83030\",\"online\":false},{\"deptid\":34,\"name\":\"宁宇光\",\"number\":\"83029\",\"online\":false},{\"deptid\":34,\"name\":\"朱刚\",\"number\":\"83031\",\"online\":false}],\"supid\":5}],\"supid\":0}],\"supid\":-1},\"local\":[{\"deptid\":74,\"name\":\"毛积法\",\"number\":\"83064\",\"online\":false}]}"
		var testContactBean: GongXunGroupInfoBean = Gson().fromJson(json, GongXunGroupInfoBean::class.java)

		var curShowSubdept: Subdept? = null

		var allOrgMap: SparseArray<Any>? = null

		private var selectPersons: MutableSet<Any> = mutableSetOf<Any>()

		private var curContactPersonsMap: MutableMap<String, Any> = mutableMapOf()

		/**
		 * 获取所有的联系人Map
		 */
		fun getAllContactPersonsMaps(): MutableMap<String, Any> {
			if (curContactPersonsMap.isEmpty()) {
				for (item in testContactBean.topdept.subdept) {
					if (null == item || null == item.subcontact) {
						//doNothing
					} else {
						for (contactItem in item.subcontact) {
							curContactPersonsMap[contactItem.number] = contactItem
						}
					}

					if (!item.subdept.isNullOrEmpty()) {
						for (innerSub in item.subdept) {
							for (innerContactItem in innerSub.subcontact) {
								curContactPersonsMap[innerContactItem.number] = innerContactItem
							}
						}
					}

				}
			}
			return curContactPersonsMap
		}

		/**
		 * 获得组ID和组对象的map键值对
		 */
		fun getAllOrgMaps(): SparseArray<Any>? {
			if (null == allOrgMap) {
				allOrgMap = SparseArray()
				allOrgMap?.put(testContactBean.topdept.id, testContactBean.topdept)
				for (item in testContactBean.topdept.subdept) {
					allOrgMap?.put(item.id, item)
					if (null != item.subdept) {
						for (innerItem in item.subdept) {
							allOrgMap?.put(innerItem.id, innerItem)
						}
					}
				}

			}

			return allOrgMap!!
		}

		/**
		 * 获取当前的选中情况
		 */
		fun getSelectPersonsSet(): MutableSet<Any>? {
			return selectPersons
		}

		/**
		 * 添加新的选中人员
		 */
		fun addPersonToSelectPersonSet(data: Any) {
			val setList = getSelectPersonsSet()
			setList?.add(data)
			data.hashCode()
		}

		/**
		 * 移除之前选中的人员
		 */
		fun removePersonFromSelectPersonSet(data: Any) {
			val setList = getSelectPersonsSet()
			setList?.remove(data)
		}

	}
}