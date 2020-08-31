package org.xtimms.kitsune.core;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ListWrapper<E> extends ObjectWrapper<ArrayList<E>> {

	public ListWrapper(ArrayList<E> object) {
		super(object);
	}

	public ListWrapper(Throwable error) {
		super(error);
	}

	public boolean isEmpty() {
		return mObject == null || mObject.isEmpty();
	}

	@NonNull
	public static <T> ListWrapper<T> badList() {
		return new ListWrapper<>(new BadResultException());
	}

	public int size() {
		return mObject == null ? 0 : mObject.size();
	}
}
