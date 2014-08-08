package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;
import android.content.Intent;

import com.shawnhu.seagull.seagull.twitter.utils.AsyncTaskManager;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.BROADCAST_TASK_STATE_CHANGED;

public abstract class ManagedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private final AsyncTaskManager manager;
	private final Context context;
	private final String tag;

	public ManagedAsyncTask(final Context context, final AsyncTaskManager manager) {
		this(context, manager, null);
	}

	public ManagedAsyncTask(final Context context, final AsyncTaskManager manager, final String tag) {
		super(manager.getHandler());
		this.manager = manager;
		this.context = context;
		this.tag = tag;
	}

	public Context getContext() {
		return context;
	}

	public String getTag() {
		return tag;
	}

	@Override
	protected void finalize() throws Throwable {
		manager.remove(hashCode());
		super.finalize();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		context.sendBroadcast(new Intent(BROADCAST_TASK_STATE_CHANGED));
	}

	@Override
	protected void onPostExecute(final Result result) {
		super.onPostExecute(result);
		context.sendBroadcast(new Intent(BROADCAST_TASK_STATE_CHANGED));
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		context.sendBroadcast(new Intent(BROADCAST_TASK_STATE_CHANGED));
	}

}
