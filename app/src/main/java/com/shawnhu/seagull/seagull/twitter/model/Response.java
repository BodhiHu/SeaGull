package com.shawnhu.seagull.seagull.twitter.model;

import android.os.Bundle;

public class Response<Data> {
    protected final Exception exception;
    protected final Data data;
    protected final Bundle extras;

    public Response(final Data data, final Exception exception) {
        this(data, exception, null);
    }

    public Response(final Data data, final Exception exception, final Bundle extras) {
        this.data = data;
        this.exception = exception;
        this.extras = extras != null ? extras : new Bundle();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Response)) return false;
        final Response<?> other = (Response<?>) obj;
        if (getData() == null) {
            if (other.getData() != null) return false;
        } else if (!getData().equals(other.getData())) return false;
        if (exception == null) {
            if (other.exception != null) return false;
        } else if (!exception.equals(other.exception)) return false;
        if (getExtras() == null) {
            if (other.getExtras() != null) return false;
        } else if (!getExtras().equals(other.getExtras())) return false;
        return true;
    }

    public Data getData() {
        return data;
    }

    public Exception getException() {
        return exception;
    }

    public Bundle getExtras() {
        return extras;
    }

    public boolean hasData() {
        return getData() != null;
    }

    public boolean hasException() {
        return exception != null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (getData() == null ? 0 : getData().hashCode());
        result = prime * result + (exception == null ? 0 : exception.hashCode());
        result = prime * result + (getExtras() == null ? 0 : getExtras().hashCode());
        return result;
    }
}
