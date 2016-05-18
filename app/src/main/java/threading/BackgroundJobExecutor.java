package threading;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yashesh on 6/7/2015.
 */
public class BackgroundJobExecutor<T extends BackgroundJob> extends ThreadPoolExecutor{



    public BackgroundJobExecutor(int corePoolSize, int maximumPoolSize) {
        super(corePoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory());
    }


    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }


    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);

    }


    @Override
    protected void terminated() {
        super.terminated();
    }
}
