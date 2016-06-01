package com.wonhigh.base.dialog;

import com.wonhigh.base.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * TODO: 数据请求加载过程  <br>
 * reference: http://blog.csdn.net/lgl1170860350/article/details/39958421
 * 
 * @author li.jf
 * @date 2014-12-12 下午3:54:29
 * @version 1.0.0 
 * @copyright wonhigh.cn
 */
public class LoadingProView extends FrameLayout {
	private View mTarget = null;
	private AnimationDrawable loadingDrawable = null;
	private TextView tv_text = null;
	private ImageView imgView = null;
	
	public LoadingProView(Context context, View target) {
		super(context);
		mTarget = target;
		inflate(context, R.layout.loading_view, this);
		imgView = (ImageView) findViewById(R.id.loading_img);
		loadingDrawable = (AnimationDrawable) imgView.getDrawable();
		tv_text = (TextView) findViewById(R.id.loading_text);
		setVisibility(View.GONE);
		
		ViewGroup.LayoutParams lp = mTarget.getLayoutParams();
		ViewParent parent = mTarget.getParent();
		FrameLayout container = new FrameLayout(context);
		ViewGroup group = (ViewGroup) parent;
		int index = group.indexOfChild(mTarget);
		group.removeView(mTarget);
		group.addView(container, index, lp);
		container.addView(mTarget);
		container.addView(this);
		
		group.invalidate();
	}
	
	public void setText(CharSequence text) {
		tv_text.setText(text);
	}
	
	public void show() {
		setVisibility(View.VISIBLE);
		loadingDrawable.start();
	}

	public void hide() {
		setVisibility(View.GONE);
		loadingDrawable.stop();
	}

	public boolean isShown() {
		return getVisibility() == View.VISIBLE;
	}
	
	private View.OnClickListener interceptListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// do nothing
		}
	};
	/**
	 * 设置loading显示时是否拦截点击事件<br/>
	 * @param flag true:拦截
	 */
	public void interceptClick(boolean flag) {
		setOnClickListener(flag ? interceptListener : null);
	}
}