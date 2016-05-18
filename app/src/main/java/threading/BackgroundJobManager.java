package threading;


/**
 * Created by yashesh on 6/8/2015.
 */
public class BackgroundJobManager<T extends BackgroundJob> {

    int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();


    public static BackgroundJobManager instance;
    private BackgroundJobExecutor executor;

    public static synchronized BackgroundJobManager getInstance(){
        if(instance==null){
            instance=new BackgroundJobManager();
        }
        return instance;
    }

    private BackgroundJobManager(){
        executor=new BackgroundJobExecutor(NUMBER_OF_CORES*2,NUMBER_OF_CORES*2);
    }

    public void submitJob(T job){

        executor.execute(job);
    }
}
