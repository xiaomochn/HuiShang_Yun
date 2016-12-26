package com.huishangyun.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.huishangyun.Office.Businesstrip.DefineDatePickDialog;

/**
 * 时间选择框
 *
 * @author Pan
 * @version v1.0
 * @since 亿企云APP/添加任务
 */
public class TimeFragment extends DialogFragment {

    private TimeFace timeFace;
    private int type;
    private boolean isChooseTime = false;
    private Activity activity;

    /**
     * @param-timeFace-返回选择结果的接口
     * @param-type-自定义的选择类型 若只有一个默认传0
     */
    public void setIndex(TimeFace timeFace, int type) {
        this.timeFace = timeFace;
        this.type = type;
    }

    /**
     * -返回所选时间和类型的接口
     *
     * @author-Pan
     */
    public interface TimeFace {

        /**
         * 返回所选的时间和类型
         *
         * @param-time-最后选择的时间
         * @param-type-选择的类型
         */
        public void chooseTime(String time, int type, long timeStamp);
    }

    /**
     * 设置是否选择精确日期
     *
     * @param isChooseTime
     * @param activity
     */
    public void setChooseTime(boolean isChooseTime, Activity activity) {
        this.isChooseTime = isChooseTime;
        this.activity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        if (isChooseTime) {
            String sysTimeStr;
            long sysTime = System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            sysTimeStr = format.format(new Date(sysTime));
            final DefineDatePickDialog dateTimePicKDialog = new DefineDatePickDialog(
                    activity, sysTimeStr);
            AlertDialog alertDialog = dateTimePicKDialog.dateTimePicKDialog()
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            try {
                                Date d2 = sf1.parse(dateTimePicKDialog.getDataTime());
                                timeFace.chooseTime(dateTimePicKDialog.getDataTime(), type, d2.getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            dismiss();

                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
					        dismiss();
                        }
                    }).create();
            dateTimePicKDialog.setAlertDialog(alertDialog);
            return alertDialog;
        } else {
            Calendar c = Calendar.getInstance();
            return new DatePickerDialog(
                    getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {

                            Date date = new Date(year - 1900, month, dayOfMonth); //获取时间转换为Date对象
                            SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
                            String time = sf1.format(date);
                            timeFace.chooseTime(time, type, date.getTime());
                        }
                    },
                    c.get(Calendar.YEAR), // 传入年份
                    c.get(Calendar.MONTH), // 传入月份
                    c.get(Calendar.DAY_OF_MONTH) // 传入天数
            );
        }

    }
}
