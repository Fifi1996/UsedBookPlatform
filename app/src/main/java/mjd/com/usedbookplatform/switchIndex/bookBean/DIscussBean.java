package mjd.com.usedbookplatform.switchIndex.bookBean;

/**
 * Created by IT之旅 on 2016-10-3.
 */
public class DIscussBean {
    private String user_name;
    private String discuss_content;
    private String discuss_time;
    private String discuss_amount;

    public DIscussBean() {
    }

    @Override
    public String toString() {
        return "DIscussBean{" +
                "user_name='" + user_name + '\'' +
                ", discuss_content='" + discuss_content + '\'' +
                ", discuss_time='" + discuss_time + '\'' +
                ", discuss_amount='" + discuss_amount + '\'' +
                '}';
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDiscuss_content() {
        return discuss_content;
    }

    public void setDiscuss_content(String discuss_content) {
        this.discuss_content = discuss_content;
    }

    public String getDiscuss_time() {
        return discuss_time;
    }

    public void setDiscuss_time(String discuss_time) {
        this.discuss_time = discuss_time;
    }

    public String getDiscuss_amount() {
        return discuss_amount;
    }

    public void setDiscuss_amount(String discuss_amount) {
        this.discuss_amount = discuss_amount;
    }
}
