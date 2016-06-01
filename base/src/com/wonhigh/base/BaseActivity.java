package com.wonhigh.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wonhigh.base.dialog.CommonProDialog;
import com.wonhigh.base.util.DensityUtil;
import com.wonhigh.base.util.ImageLoadUtil;
import com.wonhigh.base.util.Logger;
import com.wonhigh.base.util.NetUtil;
import com.wonhigh.base.util.ToastUtil;

/** 
 * @ClassName: BaseActivity 
 * @Description: 基础activity
 * @author li.xy
 * @date 2014-6-18 下午5:23:25  
 */
public abstract class BaseActivity extends Activity implements OnClickListener {
	protected String TAG = "belle";
	/**==================清空Activity发送广播=================**/
	public static String FINISH_ACTIVITY_BROADCAST = "com.wonhigh.finish.activity";
	private final static int DIALOG_PROGRESS = 1;
	private ProgressDialog progressDialog;
	private CommonProDialog waitingDialog;
	protected RelativeLayout mNetWorkWarningLayout;
	private NetWorkBroadCast netWorkBroadCast;
	private FinishBroadCast finishBroadCast;
	protected Resources res;
	private float downX;
	private float downY;
	private VelocityTracker mVelocityTracker;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Logger.d(this.getClass().getSimpleName() + "====>onCreate");
		ImageLoadUtil.init(getApplicationContext());

		TAG = this.getClass().getSimpleName();

		res = getResources();
		// 取消标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 竖屏锁定
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		netWorkBroadCast = new NetWorkBroadCast();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(netWorkBroadCast, intentFilter);

		finishBroadCast = new FinishBroadCast();
		IntentFilter finishIntentFilter = new IntentFilter();
		finishIntentFilter.addAction(FINISH_ACTIVITY_BROADCAST);
		registerReceiver(finishBroadCast, finishIntentFilter);

	}

	/** 
	 * @Description: 初始化UI(setContentView放在initView)    
	 * @return：void    
	 */
	protected abstract void initView();

	/** 
	 * @Description: 初始化顶部TitleView    
	 * @return：void    
	 */
	protected abstract void initTitleView();

	/** 
	 * @Description: 点击事件
	 * @param view    
	 * @return：void    
	 */
	protected abstract void click(View view);

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PROGRESS:
			waitingDialog = new CommonProDialog(BaseActivity.this, "正在加载...");
			waitingDialog.setCanceledOnTouchOutside(false);
			return waitingDialog;
			/*progressDialog = new ProgressDialog(BaseActivity.this);
			progressDialog.setMessage("加载中...");
			return progressDialog;*/
		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	protected void startProgressDialog() {
		showDialog(DIALOG_PROGRESS);
	}

	protected void dismissProgressDialog() {
		removeDialog(DIALOG_PROGRESS);
	}

	protected void showToast(String msg) {
		ToastUtil.toasts(getApplicationContext(), msg);
		//        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	protected void showToast(int msgId) {
		ToastUtil.toasts(getApplicationContext(), msgId);
		//        Toast.makeText(getApplicationContext(), msgId, Toast.LENGTH_SHORT).show();
	}

	protected void showLToast(String msg) {
		ToastUtil.toastL(getApplicationContext(), msg);
		//        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	protected void showLToast(int msgId) {
		ToastUtil.toastL(getApplicationContext(), msgId);
		//        Toast.makeText(getApplicationContext(), msgId, Toast.LENGTH_SHORT).show();
	}

	protected void getTextStr(TextView view) {
		view.getText().toString().trim();
	}

	protected String getResString(int id) {
		return getResources().getString(id);
	}

	/**
	 * 检查字符串是否是空对象或空字符串
	 * @author wang.fb 
	 * @param str
	 * @return 为空返回true,不为空返回false
	 */
	public boolean isNull(String str) {
		if (TextUtils.isEmpty(str)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 从当前activity跳转到目标activity,<br>
	 * 如果目标activity曾经打开过,就重新展现,<br>
	 * 如果从来没打开过,就新建一个打开
	 * @author wang.fb
	 * @time 2014_4_10
	 * @param cls
	 */
	@SuppressLint("InlinedApi")
	public void gotoExistActivity(Class<?> cls) {
		Intent intent;
		intent = new Intent(this.getApplicationContext(), cls);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	/**
	 * 新建一个activity打开
	 * @author wang.fb
	 * @time 2014_4_10
	 * @param cls
	 */
	public void gotoActivity(Class<?> cls) {
		Intent intent;
		intent = new Intent(this.getApplicationContext(), cls);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		click(v);
	}

	@Override
	protected void onStart() {
		Logger.d(this.getClass().getSimpleName() + "====>onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Logger.d(this.getClass().getSimpleName() + "====>onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Logger.d(this.getClass().getSimpleName() + "====>onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Logger.d(this.getClass().getSimpleName() + "====>onPause");
		super.onPause();
		ToastUtil.cancelToast();
	}

	@Override
	protected void onStop() {
		Logger.d(this.getClass().getSimpleName() + "====>onStop");
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		Logger.d(this.getClass().getSimpleName() + "====>onDestroy");
		unregisterReceiver(netWorkBroadCast);
		unregisterReceiver(finishBroadCast);
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Logger.d(this.getClass().getSimpleName() + "====>onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	/**广播监听网络是否正常**/
	public class NetWorkBroadCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (mNetWorkWarningLayout == null) {
				return;
			}
			if (NetUtil.isNetworkConnected(context)) {
				mNetWorkWarningLayout.setVisibility(View.GONE);
			} else {
				mNetWorkWarningLayout.setVisibility(View.VISIBLE);
			}
		}

	}

	public class FinishBroadCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}

	}

	public void setNetWorkWarningLayout(RelativeLayout layout) {
		this.mNetWorkWarningLayout = layout;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		float actionX = ev.getX();
		float actionY = ev.getY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = actionX;
			downY = actionY;
			if (null == mVelocityTracker) {
				mVelocityTracker = VelocityTracker.obtain();
			} else {
				/**重置*/
				mVelocityTracker.clear();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			mVelocityTracker.addMovement(ev);
			/**一秒划过的像素点*/
			mVelocityTracker.computeCurrentVelocity(1000);
			if (isExceedDistance(actionX, actionY, downX, downY)) {
				finish();
				/**执行淡出动画*/
				overridePendingTransition(0, android.R.anim.fade_out);
			}
			break;
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_UP:
			try {
				float XVelocity = mVelocityTracker.getXVelocity();
				float YVelocity = mVelocityTracker.getYVelocity();
				if ((XVelocity > YVelocity && XVelocity > 0 && XVelocity > 4000)
						|| isExceedDistance(actionX, actionY, downX, downY)) {
					finish();
					/**执行淡出动画*/
					overridePendingTransition(0, android.R.anim.fade_out);
					mVelocityTracker.recycle();
					return true;
				}
				if (android.os.Build.VERSION.SDK_INT < 18) {
					/**4.3版本不需要手动回收，因为在dispatchTouchEvent方法会回收*/
					mVelocityTracker.recycle();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		}
		return super.dispatchTouchEvent(ev);
	}

	public boolean isExceedDistance(float actionX, float actionY, float downX, float downY) {
		/**向右滑动一定距离，则fininsh当前页面*/
		if (actionX > downX && Math.abs(actionX - downX) > Math.abs(actionY - downY)) {
			if (Math.abs(actionX - downX) >= (DensityUtil.getInstance(this).getWindowWidth() / 3)) {
				return true;
			}
		}
		return false;
	}
}
