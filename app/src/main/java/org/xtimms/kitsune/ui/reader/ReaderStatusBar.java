package org.xtimms.kitsune.ui.reader;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.AnimationUtils;

import java.util.Calendar;

public final class ReaderStatusBar extends LinearLayout {

	private final TextView mTextViewClock;
	private final TextView mTextViewBattery;
	private final ImageView mImageViewBattery;

	private final Calendar mTime;
	private boolean mIsActive = false;

	private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
		@SuppressLint("SetTextI18n")
		@Override
		public void onReceive(Context ctxt, Intent intent) {
			update(intent);
		}
	};

	public ReaderStatusBar(Context context) {
		this(context, null, 0);
	}

	public ReaderStatusBar(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ReaderStatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		View.inflate(context, R.layout.view_status, this);
		setOrientation(HORIZONTAL);

		mTextViewBattery = findViewById(R.id.textView_battery);
		mTextViewClock = findViewById(R.id.textView_clock);
		mImageViewBattery = findViewById(R.id.imageView_battery);

		mTime = Calendar.getInstance();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		updateBattery();
		mTicker.run();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getHandler().removeCallbacks(mTicker);
		getContext().unregisterReceiver(mBatteryReceiver);
	}

	private void updateBattery() {
		final IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		final Intent batteryStatus = getContext().registerReceiver(mBatteryReceiver, filter);
		if (batteryStatus != null) {
			update(batteryStatus);
		}
	}

	@SuppressLint("SetTextI18n")
	private void update(@NonNull final Intent batteryStatus) {
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
				status == BatteryManager.BATTERY_STATUS_FULL;

		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		int batteryPct = level * 100 / scale;

		mTextViewBattery.setText(batteryPct + "%");
		int res;
		if (isCharging) {
			if (batteryPct >=90) {
				res = R.drawable.ic_battery_charging_90_black;
			} else if (batteryPct >= 50) {
				res = R.drawable.ic_battery_charging_60_black;
			} else if (batteryPct >= 30) {
				res = R.drawable.ic_battery_charging_40_black;
			} else {
				res = R.drawable.ic_battery_charging_20_black;
			}
		} else {
			if (batteryPct >=90) {
				res = R.drawable.ic_battery_80_black;
			} else if (batteryPct >= 70) {
				res = R.drawable.ic_battery_60_black;
			} else if (batteryPct >= 35) {
				res = R.drawable.ic_battery_40_black;
			} else if (batteryPct >= 15) {
				res = R.drawable.ic_battery_20_black;
			} else {
				res = R.drawable.ic_battery_alert_black;
			}
		}
		mImageViewBattery.setImageResource(res);
	}

	private final Runnable mTicker = new Runnable() {
		public void run() {
			onTimeChanged();

			long now = SystemClock.uptimeMillis();
			long next = now + (60000 - now % 60000);

			getHandler().postAtTime(mTicker, next);
		}
	};

	private void onTimeChanged() {
		if (isActive()) {
			mTime.setTimeInMillis(System.currentTimeMillis());
			mTextViewClock.setText(DateFormat.format("k:mm", mTime));
		}
	}

	private boolean isActive() {
		return mIsActive && getVisibility() == VISIBLE;
	}

	public void show() {
		if (mIsActive) {
			onTimeChanged();
			AnimationUtils.setVisibility(this, View.VISIBLE);
		}
	}

	public void hide() {
		AnimationUtils.setVisibility(this, View.GONE);
	}

	public void setIsActive(boolean flag) {
		mIsActive = flag;
		if (!flag) {
			hide();
		}
	}
}
