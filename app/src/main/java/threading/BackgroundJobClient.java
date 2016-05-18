package threading;

/**
 * Created by yashesh on 6/7/2015.
 */
public interface BackgroundJobClient {


    public void onBackgroundJobComplete(int requestCode, Object result);

    public void onBackgroundJobAbort(int requestCode, Object reason);

    public void onBackgroundJobError(int requestCode, Object error);

    public boolean needAsyncResponse();

    public boolean needResponse();


}
