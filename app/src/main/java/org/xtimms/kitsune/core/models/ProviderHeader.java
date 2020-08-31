package org.xtimms.kitsune.core.models;

import androidx.annotation.NonNull;

public class ProviderHeader {

	@NonNull
	public final String cName;
	@NonNull
	public final String dName;
	@NonNull
	public final String desc;

	public ProviderHeader(@NonNull String cName, @NonNull String dName, @NonNull String desc) {
		this.cName = cName;
		this.dName = dName;
		this.desc = desc;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ProviderHeader && ((ProviderHeader) obj).cName.equals(cName);
	}

	@Override
	public int hashCode() {
		return cName.hashCode();
	}
}
