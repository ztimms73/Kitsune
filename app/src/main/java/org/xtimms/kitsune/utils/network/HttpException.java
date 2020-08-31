package org.xtimms.kitsune.utils.network;

import java.io.IOException;

public final class HttpException extends IOException {

	private final int mStatusCode;

	public HttpException(int statusCode) {
		this.mStatusCode = statusCode;
	}

	public int getStatusCode() {
		return mStatusCode;
	}
}
