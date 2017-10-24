package mjd.com.usedbookplatform.switchIndex.bookBean;

import java.io.Serializable;

/**
 * Created by IT之旅 on 2016-10-9.
 */
public class UserBean implements Serializable {
    private String user_name;
    private String user_password;
    private String user_contact_information;
    private String user_require_id_time;
    private String user_register_time;

    public UserBean() {
    }

    public UserBean(String user_name, String user_password, String user_contact_information,
                    String user_require_id_time, String user_register_time) {
        this.user_name = user_name;
        this.user_password = user_password;
        this.user_contact_information = user_contact_information;
        this.user_require_id_time = user_require_id_time;
        this.user_register_time = user_register_time;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "user_name='" + user_name + '\'' +
                ", user_password='" + user_password + '\'' +
                ", user_contact_information='" + user_contact_information + '\'' +
                ", user_require_id_time='" + user_require_id_time + '\'' +
                ", user_register_time='" + user_register_time + '\'' +
                '}';
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_contact_information() {
        return user_contact_information;
    }

    public void setUser_contact_information(String user_contact_information) {
        this.user_contact_information = user_contact_information;
    }

    public String getUser_require_id_time() {
        return user_require_id_time;
    }

    public void setUser_require_id_time(String user_require_id_time) {
        this.user_require_id_time = user_require_id_time;
    }

    public String getUser_register_time() {
        return user_register_time;
    }

    public void setUser_register_time(String user_register_time) {
        this.user_register_time = user_register_time;
    }
}
