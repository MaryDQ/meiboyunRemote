package com.sunday.common.widgets.pulltorefresh.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class PullableListView extends ListView implements Pullable
{
	
	private boolean canpull=true;
	private boolean canload=true;

	public PullableListView(Context context)
	{
		super(context);
	}

	public PullableListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	/**
	 * �Ƿ���������ˢ��
	 * */
	public void setCanPullDown( boolean bol){
		this.canpull=bol;
		
	}
	/**
	 * �Ƿ������������ء�
	 * */
	public void setCanPullUp(boolean bol){
		this.canload=bol;
	}
	

	@Override
	public boolean canPullDown()
	{

		if(canpull){
			if (getCount() == 0)
			{
				// û��item��ʱ��Ҳ��������ˢ��
				return true;
			} else if (getFirstVisiblePosition() == 0
					&& getChildAt(0).getTop() >= 0)
			{
				// ����ListView�Ķ�����
				return true;
			} else {
                return false;
            }
		}else {
			return false;
		}
	}

	@Override
	public boolean canPullUp()
	{
		if(canload){
		if (getCount() == 0)
		{
			// û��item��ʱ��Ҳ������������
			return true;
		} else if (getLastVisiblePosition() == (getCount() - 1))
		{
			// �����ײ���
			if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
					&& getChildAt(
							getLastVisiblePosition()
									- getFirstVisiblePosition()).getBottom() <= getMeasuredHeight()) {
                return true;
            }
		}
		return false;
		}else {
			return false;
	}
		}
}
