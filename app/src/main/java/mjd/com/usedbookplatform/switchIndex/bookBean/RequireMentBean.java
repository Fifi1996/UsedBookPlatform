package mjd.com.usedbookplatform.switchIndex.bookBean;

import java.io.Serializable;

/**
 * Created by IT之旅 on 2016-10-7.
 */
public class RequireMentBean implements Serializable {
    private String requireTime;
    private String requireReply;
    private String requireIdTime;//唯一标识
    private int requireAmount;
    private String requireIdTimeConnect;
    private int isResolve;  //是否被解决了，0未解决，1解决了

    public RequireMentBean() {
    }


    @Override
    public String toString() {
        return "RequireMentBean{" +
                "requireTime='" + requireTime + '\'' +
                ", requireReply='" + requireReply + '\'' +
                ", requireIdTime='" + requireIdTime + '\'' +
                ", requireAmount=" + requireAmount +
                ", requireIdTimeConnect='" + requireIdTimeConnect + '\'' +
                ", isResolve=" + isResolve +
                '}';
    }

    public String getRequireIdTimeConnect() {
        return requireIdTimeConnect;
    }

    public void setRequireIdTimeConnect(String requireIdTimeConnect) {
        this.requireIdTimeConnect = requireIdTimeConnect;
    }

    public int getRequireAmount() {
        return requireAmount;
    }

    public void setRequireAmount(int requireAmount) {
        this.requireAmount = requireAmount;
    }

    public String getRequireTime() {
        return requireTime;
    }

    public void setRequireTime(String requireTime) {
        this.requireTime = requireTime;
    }

    public String getRequireReply() {
        return requireReply;
    }

    public void setRequireReply(String requireReply) {
        this.requireReply = requireReply;
    }

    public String getRequireIdTime() {
        return requireIdTime;
    }

    public void setRequireIdTime(String requireIdTime) {
        this.requireIdTime = requireIdTime;
    }

    public int getIsResolve() {
        return isResolve;
    }

    public void setIsResolve(int isResolve) {
        this.isResolve = isResolve;
    }
}
