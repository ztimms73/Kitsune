package org.xtimms.kitsune.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.storage.settings.AppSettings;

@SuppressWarnings("deprecation")
public abstract class ThemeUtils {

	private static final int[] APP_THEMES = new int[]{
			R.style.AppTheme_Default,
			R.style.AppTheme_PurpleCorallite,
			R.style.AppTheme_Ambiance,
			R.style.AppTheme_Nephritis,
			R.style.AppTheme_RoyalBlue,
			R.style.AppTheme_SizzlingRed,
			R.style.AppTheme_JacksonsPurple,
			R.style.AppTheme_Keppel,
			R.style.AppTheme_PixelatedGrass,
			R.style.AppTheme_Teal,
			R.style.AppTheme_Watermelon,
			R.style.AppTheme_ProtossPylon,
			R.style.AppTheme_Bluebell,
			R.style.AppTheme_VeryBerry,
			R.style.AppTheme_JalapenoRed,
			R.style.AppTheme_Dupain,
			R.style.AppTheme_Photon,
			R.style.AppTheme_Tachiyomi,
			R.style.AppTheme_VK,
			R.style.AppThemeDark_Dark,
			R.style.AppThemeDark_Miku,
			R.style.AppThemeBlack_AMOLED,
	};

	public static Drawable[] getThemedIcons(Context context, @DrawableRes int... resIds) {
		boolean dark = isAppThemeDark(context);
		PorterDuffColorFilter cf = dark ?
				new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.white_overlay_85), PorterDuff.Mode.SRC_ATOP)
				: null;
		Drawable[] ds = new Drawable[resIds.length];
		for (int i=0;i<resIds.length;i++) {
			ds[i] = ContextCompat.getDrawable(context, resIds[i]);
			if (ds[i] != null && dark) {
				ds[i].setColorFilter(cf);
			}
		}
		return ds;
	}

	@ColorInt
	public static int getAttrColor(Context context, @AttrRes int resId) {
		TypedValue typedValue = new TypedValue();
		TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { resId });
		int color = a.getColor(0, 0);
		a.recycle();
		return color;
	}

	@ColorInt
	public static int getThemeAttrColor(Context context, @AttrRes int resId) {
		TypedArray a = context.getTheme().obtainStyledAttributes(getAppThemeRes(context), new int[] { resId });
		int color = a.getColor(0, 0);
		a.recycle();
		return color;
	}

	public static Drawable getAttrDrawable(Context context, @AttrRes int resId) {
		TypedValue typedValue = new TypedValue();
		TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { resId });
		Drawable drawable = a.getDrawable(0);
		a.recycle();
		return drawable;
	}

	public static int getAppThemeRes(Context context) {
		return APP_THEMES[getAppTheme(context)];
	}

	@StyleRes
	public static int getAppThemeRes(int index) {
		return APP_THEMES[index];
	}

	public static int getAppTheme(Context context) {
		return AppSettings.get(context).getAppTheme();
	}

	public static boolean isAppThemeDark(Context context) {
		return getAppTheme(context) > 17;
	}

	public static Drawable getSelectableBackgroundBorderless(Context context) {
		return getAttrDrawable(context, R.attr.selectableItemBackgroundBorderless);
	}

	public static void setAllImagesColor(ViewGroup container, @ColorRes int colorId) {
		int color = ContextCompat.getColor(container.getContext(), colorId);
		View o;
		for (int i = container.getChildCount() - 1;i >= 0;i--) {
			o = container.getChildAt(i);
			if (o instanceof ImageView) {
				((ImageView) o).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
			} else if (o instanceof TextView) {
				for (Drawable d : ((TextView)o).getCompoundDrawables()) {
					if (d != null) {
						DrawableCompat.setTint(d, color);
					}
				}
			} else if (o instanceof ViewGroup) {
				setAllImagesColor((ViewGroup) o, colorId);
			}
		}
	}

	public static Drawable getColoredDrawable(@NonNull Context context, @DrawableRes int resId, @AttrRes int colorAttrId) {
		final Drawable drawable = ContextCompat.getDrawable(context, resId);
		if (drawable != null) {
			drawable.setColorFilter(
					getThemeAttrColor(context, colorAttrId),
					PorterDuff.Mode.SRC_IN
			);
		}
		return drawable;
	}

	@StyleRes
	public static int getBottomSheetTheme(Context context) {
		return isAppThemeDark(context) ? R.style.AppDialogDark : R.style.AppDialogLight;
	}
}
