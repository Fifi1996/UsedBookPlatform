package mjd.com.usedbookplatform.switchIndex.bookBean;

import java.io.Serializable;

/**
 * Created by IT之旅 on 2016-10-2.
 */
public class SellerInfo implements Serializable {
    private String seller_register_name;
    private String seller_register_password;
    private String seller_name;
    private String seller_tel;
    private String user_require_id_time;

    public SellerInfo() {
    }

    public SellerInfo(String seller_register_name, String seller_register_password, String seller_name, String seller_tel, String user_require_id_time) {
        this.seller_register_name = seller_register_name;
        this.seller_register_password = seller_register_password;
        this.seller_name = seller_name;
        this.seller_tel = seller_tel;
        this.user_require_id_time = user_require_id_time;
    }

    @Override
    public String toString() {
        return "SellerInfo{" +
                "seller_register_name='" + seller_register_name + '\'' +
                ", seller_register_password='" + seller_register_password + '\'' +
                ", seller_name='" + seller_name + '\'' +
                ", seller_tel='" + seller_tel + '\'' +
                ", user_require_id_time='" + user_require_id_time + '\'' +
                '}';
    }

    public String getUser_require_id_time() {
        return user_require_id_time;
    }

    public void setUser_require_id_time(String user_require_id_time) {
        this.user_require_id_time = user_require_id_time;
    }

    public String getSeller_register_name() {
        return seller_register_name;
    }

    public void setSeller_register_name(String seller_register_name) {
        this.seller_register_name = seller_register_name;
    }

    public String getSeller_register_password() {
        return seller_register_password;
    }

    public void setSeller_register_password(String seller_register_password) {
        this.seller_register_password = seller_register_password;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getSeller_tel() {
        return seller_tel;
    }

    public void setSeller_tel(String seller_tel) {
        this.seller_tel = seller_tel;
    }
}
