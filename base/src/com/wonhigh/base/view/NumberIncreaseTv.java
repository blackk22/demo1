package com.wonhigh.base.view;

import java.lang.ref.WeakReference;

import com.wonhigh.base.util.Logger;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TODO: 数值更新控件 （1秒左右）
 * 
 * @author yang.dl
 * @date 2015-6-11 上午9:40:47
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class NumberIncreaseTv extends TextView {

	private static final int NUMBER_MINIMUM = 500;//最小值，直接设值
	private static final int NUMBER_MAXIMUM = 5000;//最大值

	private static final int THREAD_SLEEP_TIME = 20;//线程休眠时间，即数字更新时间间隔
	private static final int UPDATE_DURATION_1200 = 1200;//数值大于10000的总增长时间

	private int updateCount;//更新次数
	private int numberIncreaseInterval = 100;//数字更新幅度

	private int number;//要设置的数值

	private Context mContext;
	private Activity mActivity;

	private IncreaseThread mUpdateThread;

	private UpdateHandler mUpdateHandler;

	public NumberIncreaseTv(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public NumberIncreaseTv(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public NumberIncreaseTv(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}

	private void init(Context context) {
		mContext = context;
		mActivity = (Activity) mContext;
		mUpdateHandler = new UpdateHandler(this);
	}

	/***
	 * 设置数字内容
	 * @param number
	 */
	public void setNumberText(String number) {
		excuteUpdateThread(number);
	}

	/***
	 * 设置数字内容
	 * @param number
	 */
	public void setNumberText(int number) {
		excuteUpdateThread(String.valueOf(number));
	}

	private void excuteUpdateThread(String number) {
		if (mUpdateThread != null) {//update中。。
			return;
		}
		try {
			this.number = Integer.parseInt(number);
			if (this.number <= NUMBER_MINIMUM) {//小于等于最小值，直接setText
				setText(formatString(number));
				return;
			}
			if (this.number <= NUMBER_MAXIMUM) {
				updateCount = this.number / numberIncreaseInterval;
			} else {//大于最大值，直接重新计算numberIncreaseInterval，保证周期是在1200毫秒内
				updateCount = UPDATE_DURATION_1200 / THREAD_SLEEP_TIME + 1;//THREAD_SPLEEP_TIME触发的次数+1
				numberIncreaseInterval = this.number / updateCount;//计算update幅度
			}
			mUpdateThread = new IncreaseThread(this);
			mUpdateThread.start();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setText(formatString(number));//Integer.parseInt(number)报错，直接setText
		}
	}

	/***
	 * 格式化总共的有效积分     使用逗号分割积分总数，如：1,234,567,890
	 * @param s
	 * @return
	 */
	public static String formatString(String s) {
		if (s.length() > 0 && s.length() <= 3) {
			return s;
		}
		if (s.length() > 3 && s.length() <= 6) {
			return s.substring(0, s.length() - 3) + "," + s.substring(s.length() - 3, s.length());
		} else if (s.length() > 6 && s.length() <= 9) {

			return s.substring(0, s.length() - 6) + "," + s.substring(s.length() - 6, s.length() - 3) + ","
					+ s.substring(s.length() - 3, s.length());
		} else if (s.length() > 9 && s.length() <= 12) {
			return s.substring(0, s.length() - 9) + "," + s.substring(s.length() - 9, s.length() - 6) + ","
					+ s.substring(s.length() - 6, s.length() - 3) + "," + s.substring(s.length() - 3, s.length());
		}
		return "";
	}

	/***
	 * 
	 * TODO: 更新控件handler,static防止内存泄露
	 * 
	 * @author yang.dl
	 * @date 2015-6-11 下午4:10:13
	 * @version 1.0.0 
	 * @copyright wonhigh.cn
	 */
	static class UpdateHandler extends Handler {
		private WeakReference<NumberIncreaseTv> mWeakTv;

		public UpdateHandler(NumberIncreaseTv mNumberIncreaseTv) {
			mWeakTv = new WeakReference<NumberIncreaseTv>(mNumberIncreaseTv);
		}

		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			NumberIncreaseTv mNumberIncreaseTv = mWeakTv.get();
			if (mNumberIncreaseTv != null) {
				mNumberIncreaseTv.setText(formatString(String.valueOf(msg.what)));
				if (msg.what == mNumberIncreaseTv.number) {//结束update
					mNumberIncreaseTv.mUpdateThread = null;
				}
			}
		}
	}

	/**
	 * 
	 * TODO: 数字增长线程，static防止内存泄露
	 * 
	 * @author yang.dl
	 * @date 2015-6-11 下午4:08:04
	 * @version 1.0.0 
	 * @copyright wonhigh.cn
	 */
	static class IncreaseThread extends Thread {
		private WeakReference<NumberIncreaseTv> mWeakTv;

		public IncreaseThread(NumberIncreaseTv mNumberIncreaseTv) {
			mWeakTv = new WeakReference<NumberIncreaseTv>(mNumberIncreaseTv);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			NumberIncreaseTv mNumberIncreaseTv = mWeakTv.get();
			for (int i = 1; mNumberIncreaseTv != null && !mNumberIncreaseTv.mActivity.isFinishing()
					&& i <= mNumberIncreaseTv.updateCount; i++) {//i = updateCount也执行循环，是因为如果整除I*updateCount才得到完成的值，否则第updateCount次循环，是为了显示最后加上余数部分
				int threadSleepTime = 0;
				if (i == 1) {
					threadSleepTime = 15 * THREAD_SLEEP_TIME;//第一次更新，增加等待时间，提高体验
				} else {
					threadSleepTime = THREAD_SLEEP_TIME;
				}
				try {
					Thread.sleep(threadSleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block 
					e.printStackTrace();
				}
				int currentNumber = 0;
				if (i != mNumberIncreaseTv.updateCount) {
					currentNumber = i * mNumberIncreaseTv.numberIncreaseInterval;
				} else {
					currentNumber = mNumberIncreaseTv.number;
				}
				mNumberIncreaseTv.mUpdateHandler.sendEmptyMessage(currentNumber);
				Logger.i("aaa", "currentNumber=" + currentNumber);
				Logger.i("aaa", "updateCount=" + mNumberIncreaseTv.updateCount);
				Logger.i("aaa", "i=" + i);

			}
		}
	}

}
