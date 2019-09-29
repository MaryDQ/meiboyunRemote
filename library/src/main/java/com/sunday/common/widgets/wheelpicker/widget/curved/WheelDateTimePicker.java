package com.sunday.common.widgets.wheelpicker.widget.curved;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sunday.common.R;
import com.sunday.common.model.ArrangeResult;
import com.sunday.common.widgets.wheelpicker.core.AbstractWheelDecor;
import com.sunday.common.widgets.wheelpicker.core.AbstractWheelPicker;
import com.sunday.common.widgets.wheelpicker.core.IWheelPicker;
import com.sunday.common.widgets.wheelpicker.view.WheelStraightPicker;

import java.util.ArrayList;
import java.util.List;

public class WheelDateTimePicker extends LinearLayout implements IWheelPicker {
    protected WheelStraightPicker pickerDay;
    protected WheelStraightPicker pickerHour;
    protected WheelStraightPicker pickerMinute;
    TextView tv_not_available;

    protected AbstractWheelPicker.OnWheelChangeListener listener;
    protected OnDateTimeDialogSelected dateTimeListener;

    protected String day, hour, minute;
    protected String labelBGColor = "#EEEEEE";
    protected int stateYear, stateMonth, stateDay, stateTime;

    protected ArrangeResult timeSelectResult;
    private List<String> days = new ArrayList<>();
    private List<String> hours = new ArrayList<>();
    private List<String> minutes = new ArrayList<>();

    private int type;//1 电话 3 视频

    public WheelDateTimePicker(Context context) {
        super(context);
    }

    public WheelDateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.wheel_date_time_picker, null);
        pickerDay = (WheelStraightPicker) view.findViewById(R.id.day_picker);
        pickerHour = (WheelStraightPicker) view.findViewById(R.id.hour_picker);
        pickerMinute = (WheelStraightPicker) view.findViewById(R.id.minute_picker);
        tv_not_available = (TextView) view.findViewById(R.id.tv_not_available);

        Button btnSure = (Button) view.findViewById(R.id.btn_sure);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateTimeListener != null) {
                    dateTimeListener.DateTimeCancel();
                }
            }
        });
        btnSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateTimeListener != null) {
                    dateTimeListener.DateTimeSure(day, hour, minute);
                }
            }
        });

        addView(view);


        pickerDay.setCurrentBackground(true, labelBGColor);
        pickerHour.setCurrentBackground(true, labelBGColor);
        pickerMinute.setCurrentBackground(true, labelBGColor);

        pickerDay.setPadding(20, 0, 20, 0);
    }

    public void setTimeSelectResult(ArrangeResult timeSelectResult, int type) {
        if(timeSelectResult == null){
            return;
        }
        this.type = type;
        this.timeSelectResult = timeSelectResult;
        init();
        initDateTime();
        setDateTime();

        initListener();
    }

    private void initDateTime() {

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String strDate = sdf.format(new Date());

        int firstDayIndex = -1;
        days.clear();
        for (int i = 0; i <timeSelectResult.getResults().size(); i++) {
            if(true/*!timeSelectResult.getResults().get(i).getDate().equals(strDate)*/) {
                if(firstDayIndex == -1){
                    firstDayIndex = i;
                }
                days.add(timeSelectResult.getResults().get(i).getDate());
            }
        }

        hours.clear();
        int orderType = 0;
        if(type == 3){
            orderType = 1;
        }
        int firstHourIndex = -1;
        for (int i = 0; i <timeSelectResult.getResults().get(firstDayIndex).getTimes().size(); i++) {
            if(timeSelectResult.getResults().get(firstDayIndex).getTimes().get(i).getOrderType() == 2 ||
               timeSelectResult.getResults().get(firstDayIndex).getTimes().get(i).getOrderType() == orderType) {
                if(firstHourIndex == -1){
                    firstHourIndex = i;
                }
                hours.add(timeSelectResult.getResults().get(firstDayIndex).getTimes().get(i).getTime());
            }
        }

        minutes.clear();
        if(timeSelectResult.getResults().get(firstDayIndex).getTimes().get(firstHourIndex).getSunExpertTimes()!=null &&
           timeSelectResult.getResults().get(firstDayIndex).getTimes().get(firstHourIndex).getSunExpertTimes().size() > 0){
            for (int i = 0; i <timeSelectResult.getResults().get(firstDayIndex).getTimes().get(firstHourIndex).getSunExpertTimes().size(); i++) {
                if(timeSelectResult.getResults().get(firstDayIndex).getTimes().get(firstHourIndex).getSunExpertTimes().get(i).getIsSelect() == 0) {
                    if(type == 1) {
                        minutes.add(timeSelectResult.getResults().get(firstDayIndex).getTimes().get(firstHourIndex).getSunExpertTimes().get(i).getTime() + "(电话)");
                    }else{
                        minutes.add(timeSelectResult.getResults().get(firstDayIndex).getTimes().get(firstHourIndex).getSunExpertTimes().get(i).getTime() + "(视频)");
                    }
                }else{
                    minutes.add("已被预约");
                }
            }
            pickerMinute.setVisibility(View.VISIBLE);
            tv_not_available.setVisibility(View.INVISIBLE);
        }else{
            pickerMinute.setVisibility(View.INVISIBLE);
            tv_not_available.setVisibility(View.VISIBLE);
        }
    }

    private void setDateTime() {
        pickerDay.setData(days);
        pickerHour.setData(hours);
        pickerMinute.setData(minutes);

        pickerDay.setItemIndex(0);
        day = days.get(0);
        pickerHour.setItemIndex(0);
        hour = hours.get(0);
        if(minutes.size()>0) {
            pickerMinute.setItemIndex(0);
            minute = minutes.get(0);
        }else{
            minute = "";
        }
        pickerDay.invalidate();
        pickerHour.invalidate();
        pickerMinute.invalidate();
    }


    private void initListener() {
        pickerDay.setOnWheelChangeListener(new AbstractWheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrolling(float deltaX, float deltaY) {
                if (null != listener) {
                    listener.onWheelScrolling(deltaX, deltaY);
                }
            }

            @Override
            public void onWheelSelected(int curIndex, String data) {
                    day = data;
                    hours.clear();
                    int orderType = 0;
                    if(type == 3){
                        orderType = 1;
                    }
                int index = -1;
                for (int i = 0; i <timeSelectResult.getResults().size(); i++) {
                    if(timeSelectResult.getResults().get(i).getDate().equals(data)) {
                        index = i;
                        break;
                    }
                }
                    int firstIndex = -1;
                    for (int i = 0; i <timeSelectResult.getResults().get(index).getTimes().size(); i++) {
                        if(timeSelectResult.getResults().get(index).getTimes().get(i).getOrderType() == 2 ||
                           timeSelectResult.getResults().get(index).getTimes().get(i).getOrderType() == orderType) {
                            if(firstIndex == -1){
                                firstIndex = i;
                            }
                            hours.add(timeSelectResult.getResults().get(index).getTimes().get(i).getTime());
                        }
                    }

                    hour = hours.get(0);

                    minutes.clear();
                    if(timeSelectResult.getResults().get(index).getTimes().get(firstIndex).getSunExpertTimes()!=null &&
                            timeSelectResult.getResults().get(index).getTimes().get(firstIndex).getSunExpertTimes().size() > 0){
                        for (int i = 0; i <timeSelectResult.getResults().get(index).getTimes().get(firstIndex).getSunExpertTimes().size(); i++) {
                            if(timeSelectResult.getResults().get(index).getTimes().get(firstIndex).getSunExpertTimes().get(i).getIsSelect() == 0) {
                                if(type == 1) {
                                    minutes.add(timeSelectResult.getResults().get(index).getTimes().get(firstIndex).getSunExpertTimes().get(i).getTime() + "(电话)");
                                }else{
                                    minutes.add(timeSelectResult.getResults().get(index).getTimes().get(firstIndex).getSunExpertTimes().get(i).getTime() + "(视频)");
                                }
                            }else{
                                minutes.add("已被预约");
                            }
                        }
                    }

                    if(minutes.size() > 0){
                        pickerMinute.setVisibility(View.VISIBLE);
                        tv_not_available.setVisibility(View.INVISIBLE);
                        minute = minutes.get(0);
                        pickerMinute.setItemIndex(0);
                    }else{
                        minute = "";
                        pickerMinute.setVisibility(View.INVISIBLE);
                        tv_not_available.setVisibility(View.VISIBLE);
                    }

                    pickerHour.setData(hours);
                    pickerMinute.setData(minutes);
                    pickerHour.setItemIndex(0);
                    pickerHour.invalidate();
                    pickerMinute.invalidate();
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
                stateDay = state;
                if (null != listener) {
                    checkState(listener);
                }
            }
        });

        pickerHour.setOnWheelChangeListener(new AbstractWheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrolling(float deltaX, float deltaY) {
                if (null != listener) {
                    listener.onWheelScrolling(deltaX, deltaY);
                }
            }

            @Override
            public void onWheelSelected(int index, String data) {
                    hour = data;
                    minutes.clear();
                    int curDayIndex = 0;
                    int curHourIndex = 0;
                    for (int i = 0; i <timeSelectResult.getResults().size(); i++) {
                        if(timeSelectResult.getResults().get(i).getDate().equals(day)){
                            curDayIndex = i;
                            break;
                        }
                    }
                    for (int i = 0; i <timeSelectResult.getResults().get(curDayIndex).getTimes().size(); i++) {
                        if(timeSelectResult.getResults().get(curDayIndex).getTimes().get(i).getTime().equals(hour)){
                            curHourIndex = i;
                            break;
                        }
                    }
                    if(timeSelectResult.getResults().get(curDayIndex).getTimes().get(curHourIndex).getSunExpertTimes()!=null &&
                            timeSelectResult.getResults().get(curDayIndex).getTimes().get(curHourIndex).getSunExpertTimes().size() > 0){
                        for (int i = 0; i <timeSelectResult.getResults().get(curDayIndex).getTimes().get(curHourIndex).getSunExpertTimes().size(); i++) {
                            if(timeSelectResult.getResults().get(curDayIndex).getTimes().get(curHourIndex).getSunExpertTimes().get(i).getIsSelect() == 0) {
                                if(type == 1) {
                                    minutes.add(timeSelectResult.getResults().get(curDayIndex).getTimes().get(curHourIndex).getSunExpertTimes().get(i).getTime() + "(电话)");
                                }else{
                                    minutes.add(timeSelectResult.getResults().get(curDayIndex).getTimes().get(curHourIndex).getSunExpertTimes().get(i).getTime() + "(视频)");
                                }
                            }else{
                                minutes.add("已被预约");
                            }
                        }
                    }

                    if(minutes.size() > 0){
                        pickerMinute.setVisibility(View.VISIBLE);
                        tv_not_available.setVisibility(View.INVISIBLE);
                        minute = minutes.get(0);
                    }else{
                        minute = "";
                        pickerMinute.setVisibility(View.INVISIBLE);
                        tv_not_available.setVisibility(View.VISIBLE);
                    }

                    pickerMinute.setData(minutes);
                    pickerMinute.invalidate();

            }

            @Override
            public void onWheelScrollStateChanged(int state) {
                stateTime = state;
                if (null != listener) {
                    checkState(listener);
                }
            }
        });
        pickerMinute.setOnWheelChangeListener(new AbstractWheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrolling(float deltaX, float deltaY) {
                if (null != listener) {
                    listener.onWheelScrolling(deltaX, deltaY);
                }
            }

            @Override
            public void onWheelSelected(int index, String data) {
                    minute = data;
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
                if (null != listener) {
                    checkState(listener);
                }
            }
        });
    }

    @Override
    public void setData(List<String> data) {
        throw new RuntimeException("Set data will not allow here!");
    }

    @Override
    public void setOnWheelChangeListener(AbstractWheelPicker.OnWheelChangeListener listener) {
        this.listener = listener;
    }

    private void checkState(AbstractWheelPicker.OnWheelChangeListener listener) {
        if (stateYear == AbstractWheelPicker.SCROLL_STATE_IDLE &&
                stateMonth == AbstractWheelPicker.SCROLL_STATE_IDLE &&
                stateDay == AbstractWheelPicker.SCROLL_STATE_IDLE) {
            listener.onWheelScrollStateChanged(AbstractWheelPicker.SCROLL_STATE_IDLE);
        }
        if (stateYear == AbstractWheelPicker.SCROLL_STATE_SCROLLING ||
                stateMonth == AbstractWheelPicker.SCROLL_STATE_SCROLLING ||
                stateDay == AbstractWheelPicker.SCROLL_STATE_SCROLLING) {
            listener.onWheelScrollStateChanged(AbstractWheelPicker.SCROLL_STATE_SCROLLING);
        }
        if (stateYear + stateMonth + stateDay == 1) {
            listener.onWheelScrollStateChanged(AbstractWheelPicker.SCROLL_STATE_DRAGGING);
        }
    }

    @Override
    public void setItemIndex(int index) {
        pickerDay.setItemIndex(index);
    }

    @Override
    public void setItemSpace(int space) {
        pickerDay.setItemSpace(space);
    }

    @Override
    public void setItemCount(int count) {
        pickerDay.setItemCount(count);
    }

    @Override
    public void setTextColor(int color) {
        pickerDay.setTextColor(color);
    }

    @Override
    public void setTextSize(int size) {
        pickerDay.setTextSize(size);
    }

    @Override
    public void clearCache() {
        pickerDay.clearCache();
    }

    @Override
    public void setCurrentTextColor(int color) {
        pickerDay.setCurrentTextColor(color);
    }

    @Override
    public void setWheelDecor(boolean ignorePadding, AbstractWheelDecor decor) {
        pickerDay.setWheelDecor(ignorePadding, decor);
    }

    //String 补位
    private String string2string(int num) {
        return String.format("%02d", num);
    }

    /**
     * 日期选定成功
     */
    public interface OnDateTimeDialogSelected {

        void DateTimeCancel();
        void DateTimeSure(String day, String hour, String minute);
    }

    public void setOnDateTimeDialogSelected(OnDateTimeDialogSelected linstener) {
        this.dateTimeListener = linstener;
    }

}