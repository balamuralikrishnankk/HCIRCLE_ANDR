package threading;

import android.os.Handler;
import android.os.Message;
/**
 * Created by yashesh on 6/7/2015.
 */
public abstract class BackgroundJob<T extends BackgroundJobClient> implements Runnable{

      protected  T mClient;


        protected static final int TASK_COMPLETE=11,TASK_ERROR=45,TASK_ABORT=74;


      protected Handler uiHandler;

      public BackgroundJob(T client){
          mClient=client;
          if(mClient.needAsyncResponse()){
              getUiHandler();
          }

      }
      protected void notifyCompletion(int requestCode,Object result){
            if(mClient.needResponse()){

                if(mClient.needAsyncResponse()){
                    getUiHandler().sendMessage(getMessage(TASK_COMPLETE, requestCode, result));
                }else{
                    mClient.onBackgroundJobComplete(requestCode,result);
                }

            }
      }
      private Handler getUiHandler(){
          if(uiHandler==null){
              uiHandler=new Handler(){

                  @Override
                  public void handleMessage(Message msg) {
                      super.handleMessage(msg);
                      MessageData data= (MessageData) msg.obj;
                      switch (data.getOpType()){

                          case TASK_COMPLETE:
                              mClient.onBackgroundJobComplete(data.getRequestCode(),data.getData());
                              break;
                          case TASK_ABORT:
                              mClient.onBackgroundJobAbort(data.getRequestCode(),data.getData());
                              break;
                          case TASK_ERROR:
                              mClient.onBackgroundJobError(data.getRequestCode(),data.getData());
                              break;

                      }



                  }
              };
          }
          return  uiHandler;

      }

      protected void notifyAbort(int requestCode,Object reason){


          if(mClient.needResponse()){

              if(mClient.needAsyncResponse()){
                  getUiHandler().sendMessage(getMessage(TASK_ABORT,requestCode,reason));
              }else{
                  mClient.onBackgroundJobAbort(requestCode,reason);
              }

          }



      }

      protected void notifyError(int requestCode,Object error){


          if(mClient.needResponse()){

              if(mClient.needAsyncResponse()){
                  getUiHandler().sendMessage(getMessage(TASK_ERROR,requestCode,error));
              }else{
                  mClient.onBackgroundJobError(requestCode, error);
              }

          }


      }


    private Message getMessage(int opType,int requestCode,Object dataObj){
        Message msg=new Message();
        MessageData data=new MessageData();
        data.setOpType(opType);
        data.setRequestCode(requestCode);
        data.setData(dataObj);
        msg.obj=data;
        return msg;
    }


    public void execute(){

        BackgroundJobManager.getInstance().submitJob(this);
    }

     private class MessageData{

         private int opType,requestCode;
         private Object data;

         public int getOpType() {
             return opType;
         }

         public void setOpType(int opType) {
             this.opType = opType;
         }

         public int getRequestCode() {
             return requestCode;
         }

         public void setRequestCode(int requestCode) {
             this.requestCode = requestCode;
         }

         public Object getData() {
             return data;
         }

         public void setData(Object data) {
             this.data = data;
         }
     }

}
