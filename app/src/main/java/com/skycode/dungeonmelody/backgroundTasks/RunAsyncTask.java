package com.skycode.dungeonmelody.backgroundTasks;

import android.os.AsyncTask;

public class RunAsyncTask extends AsyncTask {

    private final Runnable _runnable;
    private final boolean _loop;

    public RunAsyncTask(Runnable runnable, boolean loop)
    {
        _runnable = runnable;
        _loop = loop;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        while (true)
        {
            _runnable.run();

            if(isCancelled() || !_loop)
            {
                return null;
            }
        }
    }
}
