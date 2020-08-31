package org.xtimms.kitsune.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import java.util.WeakHashMap;

import timber.log.Timber;

public abstract class AnimationUtils {

	public static final Interpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();

	private static final int DURATION_SHORT = 100;

	private static final WeakHashMap<View,Animator> sAnimations = new WeakHashMap<>();

	public static void setVisibility(@NonNull View view, int visibility) {
		switch (visibility) {
			case View.VISIBLE:
				Timber.tag("VVV").d("VISIBLE");
				break;
			case View.INVISIBLE:
				Timber.tag("VVV").d("INVISIBLE");
				break;
			case View.GONE:
				Timber.tag("VVV").d("GONE");
				break;
			default:
				Timber.tag("VVV").d(String.valueOf(visibility));
		}
		final int currentVisibility = view.getVisibility();
		if (visibility == currentVisibility) {
			return;
		}
		Animator oldAnimation = sAnimations.get(view);
		if (oldAnimation != null) {
			oldAnimation.cancel();
		}
		final int duration = view.getResources().getInteger(android.R.integer.config_shortAnimTime);
		ViewPropertyAnimator animator = view.animate()
				.setDuration(duration);
		if (visibility == View.VISIBLE) {
			//show it
			view.setAlpha(0f);
			view.setVisibility(View.VISIBLE);
			animator.setListener(new ViewAnimationListener(view, visibility));
			animator.alpha(1f);
		} else {
			animator.alpha(0f);
			animator.setListener(new ViewAnimationListener(view, visibility));
		}
		animator.start();
	}

	private static class ViewAnimationListener implements Animator.AnimatorListener {

		private final View mView;
		private final int mVisibility;

		ViewAnimationListener(View view, int visibility) {
			mView = view;
			mVisibility = visibility;
		}

		@Override
		public void onAnimationStart(Animator animation) {
			sAnimations.put(mView, animation);
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			if (mVisibility != View.VISIBLE) {
				mView.setVisibility(mVisibility);
				mView.setAlpha(1f);
			}
			sAnimations.remove(mView);
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			sAnimations.remove(mView);
		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}
	}

	public static void expand(final View v) {
		v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 1;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1
						? ViewGroup.LayoutParams.WRAP_CONTENT
						: (int)(targetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public static void collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if(interpolatedTime == 1){
					v.setVisibility(View.GONE);
				}else{
					v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public static void crossfade(@Nullable final View whatHide, @Nullable View whatShow) {
		if (whatShow != null && whatShow.getVisibility() != View.VISIBLE) {
			cancelAnimation(whatShow);
			whatShow.setAlpha(0f);
			whatShow.setVisibility(View.VISIBLE);
			whatShow.animate()
					.alpha(1f)
					.setDuration(DURATION_SHORT)
					.setListener(null);
		}

		if (whatHide != null && whatHide.getVisibility() == View.VISIBLE) {
			cancelAnimation(whatHide);
			whatHide.animate()
					.alpha(0f)
					.setDuration(DURATION_SHORT)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							whatHide.setVisibility(View.GONE);
							whatHide.setAlpha(1);
						}
					});
		}

	}

	public static void zooma(@Nullable final View whatHide, @Nullable View whatShow) {
		if (whatShow != null && whatShow.getVisibility() != View.VISIBLE) {
			cancelAnimation(whatShow);
			whatShow.setScaleX(0f);
			whatShow.setScaleY(0f);
			whatShow.setAlpha(0f);
			whatShow.setVisibility(View.VISIBLE);
			whatShow.animate()
					.scaleX(1f)
					.scaleY(1f)
					.alpha(1f)
					.setDuration(DURATION_SHORT)
					.setListener(null);
		}

		if (whatHide != null && whatHide.getVisibility() == View.VISIBLE) {
			cancelAnimation(whatHide);
			whatHide.animate()
					.scaleX(0f)
					.scaleY(0f)
					.alpha(0f)
					.setDuration(DURATION_SHORT)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							whatHide.setVisibility(View.GONE);
						}
					});
		}

	}

	private static void cancelAnimation(@NonNull View view) {
		view.animate().cancel();
	}

}
