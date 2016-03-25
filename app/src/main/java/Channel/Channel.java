package Channel;

/**
 * Created by SALAI on 3/25/2016.
 */
public class Channel {
    String channel_id;
    String channel_name;
    int member_count;
    String team_name;
    public void setChannel(String id,String name,int count,String team){
        this.channel_id=id;
        this.channel_name=name;
        this.member_count=count;
        this.team_name=team;
    }
    public int getMember_count(){
        return this.member_count;
    }
    public String getChannel_id(){
        return this.channel_id;
    }
}
