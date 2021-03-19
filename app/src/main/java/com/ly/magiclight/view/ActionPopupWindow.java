package com.ly.magiclight.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.ly.magiclight.R;
import com.ly.magiclight.utils.PixelUtil;
import com.ly.magiclight.utils.ScreenUtil;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

/**
 * @author ly
 * date 2021/3/18 11:17
 */
public class ActionPopupWindow extends BasePopupWindow {

    private ColorfulLayout colorfulLayout;
    private AlertDialog colorDialog;
    private String curMode;
    private LinearLayout ll;

    public ActionPopupWindow(Context context) {
        super(context);
        setWidth(ScreenUtil.getScreenWidth(mContext) * 3 / 4);
        setAnimationStyle(R.style.action_photo_anim);
    }

    @Override
    public int getLayoutId() {
        return R.layout.popup_action;
    }

    @Override
    public void initViews(View contentView) {
        ll = contentView.findViewById(R.id.ll_container);
        ll.setBackgroundColor(0x99ffffff);
        int top = PixelUtil.dp2px(mContext, 10);

        for (String s : ColorfulLayout.MODE) {
            TextView tv = new TextView(mContext);
            tv.setText(s);
            tv.setPadding(0, top, 0, top);
            tv.setTag(s);
            tv.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            lp.weight = 1;
            if (ll.getChildCount() > 0)
                lp.leftMargin = PixelUtil.dp2px(mContext, 10);

            ll.addView(tv, lp);

            tv.setOnClickListener(v -> {
                String tmp = (String) v.getTag();
                if (ColorfulLayout.MODE[3].equals(tmp)) {
                    showColorPicker();
                } else {
                    curMode = tmp;
                    setBg4SelMode();
                    colorfulLayout.changeMode(curMode);
                }

                if (onItemClick != null)
                    onItemClick.onClick();

                ll.postDelayed(this::dismiss, 500);
            });
        }

    }

    private void setBg4SelMode() {
        int index = colorfulLayout.getIndexByMode(curMode);

        for (int i = 0; i < ll.getChildCount(); i++) {
            TextView item = (TextView) ll.getChildAt(i);
            if (index == i) {
                item.setBackgroundColor(0xbbaaaaaa);
                item.setTextColor(0xffffffff);
                item.setTextSize(18);
            } else {
                item.setBackgroundColor(0x00ffffff);
                item.setTextColor(0xee333333);
                item.setTextSize(16);
            }
        }
    }

    private void showColorPicker() {
        if (colorDialog == null) {
            colorDialog = new ColorPickerDialog.Builder(mContext)
                    .setPositiveButton("确定",
                            (ColorEnvelopeListener) (envelope, fromUser) -> {
                                curMode = ColorfulLayout.MODE[3];
                                setBg4SelMode();
                                colorfulLayout.setSingleColor(envelope.getColor());
                            }
                    )
                    .attachAlphaSlideBar(true) // the default value is true.
                    .attachBrightnessSlideBar(true)  // the default value is true.
                    .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                    .setCancelable(true)
                    .show();
        } else {
            colorDialog.show();
        }
    }

    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    interface OnItemClick {
        void onClick();
    }

    public void setColorfulLayout(ColorfulLayout colorfulLayout) {
        this.colorfulLayout = colorfulLayout;
        curMode = colorfulLayout.getCurMode();
        setBg4SelMode();
    }
}
