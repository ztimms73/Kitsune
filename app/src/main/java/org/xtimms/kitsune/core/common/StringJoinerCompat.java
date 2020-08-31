package org.xtimms.kitsune.core.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public final class StringJoinerCompat {

	private final String mPrefix;
	private final String mDelimiter;
	private final String mSuffix;
	private String mEmptyValue;
	@Nullable
	private StringBuilder mValue;

	public StringJoinerCompat(@NonNull CharSequence delimiter) {
		this(delimiter, "", "");
	}

	public StringJoinerCompat(@NonNull CharSequence delimiter, @NonNull CharSequence prefix, @NonNull CharSequence suffix) {
		mPrefix = prefix.toString();
		mDelimiter = delimiter.toString();
		mSuffix = suffix.toString();
		mEmptyValue = "";
	}

	public StringJoinerCompat setEmptyValue(@NonNull CharSequence emptyValue) {
		mEmptyValue = emptyValue.toString();
		return this;
	}

	@NotNull
	@Override
	public String toString() {
		if (mValue == null) {
			return mEmptyValue;
		} else {
			if (mSuffix.equals("")) {
				return mValue.toString();
			} else {
				int initialLength = mValue.length();
				String result = mValue.append(mSuffix).toString();
				mValue.setLength(initialLength);
				return result;
			}
		}
	}

	public void add(CharSequence newElement) {
		prepareBuilder().append(newElement);
    }

	public StringJoinerCompat merge(@NonNull StringJoinerCompat other) {
		if (other.mValue != null) {
			final int length = other.mValue.length();
			StringBuilder builder = prepareBuilder();
			builder.append(other.mValue, other.mPrefix.length(), length);
		}
		return this;
	}

	private StringBuilder prepareBuilder() {
		if (mValue != null) {
			mValue.append(mDelimiter);
		} else {
			mValue = new StringBuilder().append(mPrefix);
		}
		return mValue;
	}

	public int length() {
		return (mValue != null ? mValue.length() + mSuffix.length() : mEmptyValue.length());
	}
}
