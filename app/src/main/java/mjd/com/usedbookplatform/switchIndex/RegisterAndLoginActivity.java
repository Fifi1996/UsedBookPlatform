package mjd.com.usedbookplatform.switchIndex;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mjd.com.usedbookplatform.R;
import mjd.com.usedbookplatform.switchIndex.bookBean.UserBean;

public class RegisterAndLoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String APPNAME = "android.intent.action.MAIN";
    private EditText register_name,register_password,register_tel;
    private CheckBox register_remberMe;
    private Button register_register,register_login;
    private String str_register_name,str_register_password,str_register_tel;
    private String jdbcString,url;
    private ProgressDialog dialog;
    private String user_pwd;
    private UserBean userBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_and_login);
        jdbcString = getResources().getString(R.string.jdbc);
        url = getResources().getString(R.string.url);
        initViews();

    }

    private void initViews() {
        register_name = (EditText) findViewById(R.id.register_name);
        register_password = (EditText) findViewById(R.id.register_password);
        register_tel = (EditText) findViewById(R.id.register_tel);
        register_remberMe = (CheckBox) findViewById(R.id.register_remberMe);
        register_register = (Button) findViewById(R.id.register_register);
        register_login = (Button) findViewById(R.id.register_login);
        register_register.setOnClickListener(this);
        register_login.setOnClickListener(this);

    }

    //注册、登录成功后会进行广播，传递从数据库获取到的用户信息
    public void test(View view) {
        Intent intent = new Intent(APPNAME);
        Bundle bundle = new Bundle();
        bundle.putString("user","test测试");
        intent.putExtra("user",bundle);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        str_register_name = register_name.getText().toString().trim();
        System.out.println(str_register_name);
        str_register_password = register_password.getText().toString().trim();
        str_register_tel = register_tel.getText().toString().trim();
        //字符串合法性检测
        if ( !loginTest() ){
            return;
        }
        //注册登录选择
        switch (v.getId()){
            case R.id.register_register:
                new loginTest(str_register_name,str_register_password,str_register_tel,"register").execute();
                break;
            case R.id.register_login:
                new loginTest(str_register_name,str_register_password,str_register_tel,"login").execute();
                break;
        }
    }
    private boolean loginTest() {
        if (TextUtils.isEmpty(str_register_name) || TextUtils.isEmpty(str_register_tel) || TextUtils.isEmpty(str_register_tel)){
            Toast.makeText(RegisterAndLoginActivity.this,"用户名、密码、联系方式三者不可为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        Pattern pattern = Pattern.compile("\\d{11}");
        Matcher matcher = pattern.matcher(str_register_tel);
        if ( !matcher.matches() ){
            Toast.makeText(RegisterAndLoginActivity.this,"联系方式不合法,请重新填写",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private class loginTest extends AsyncTask{

        private String name,pwd,tel;
        private String timeStamp = System.currentTimeMillis()+ "";
        private String url_str;
        private boolean flag;
        private String loginRegister;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        private String registerTime = simpleDateFormat.format(date);

        private String sql_register;
        private String sql_login;

        public loginTest(String name, String pwd, String tel,String loginRegister) {
            this.name = name;
            this.pwd = pwd;
            this.tel = tel;
            this.loginRegister = loginRegister;
            sql_register = "insert into user values ('" + this.name + "','" + this.pwd +"','" + this.tel + "','" + this.timeStamp + "','" + this.registerTime + "')";
            sql_login = "select * from user where user_name='" + this.name + "'";

            url_str = "jdbc:mysql://"+url+":3306/usedbook";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(RegisterAndLoginActivity.this);
            dialog.setMessage("正在获取信息，请稍后....");
            dialog.create();
            dialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                Class.forName(jdbcString);
                connection = DriverManager.getConnection(url_str,"root","131365");
                statement = connection.createStatement();
                if (loginRegister.equals("register")){
                    flag = statement.execute(sql_register);
                    System.out.println(sql_register+"---->>>>");
                }else {
                    resultSet = statement.executeQuery(sql_login);
                    System.out.println(sql_login+"--->>>>>>>>>");
                    while (resultSet.next()){
                        user_pwd = resultSet.getString("user_password");
                        userBean = new UserBean();
                        userBean.setUser_name(resultSet.getString("user_name"));
                        userBean.setUser_password(resultSet.getString("user_password"));
                        userBean.setUser_contact_information(resultSet.getString("user_contact_information"));
                        userBean.setUser_register_time(resultSet.getString("user_register_time"));
                        userBean.setUser_require_id_time(resultSet.getString("user_require_id_time"));
                    }
                    return userBean;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (dialog!=null && dialog.isShowing()){
                dialog.dismiss();
            }
            if (loginRegister.equals("register")){
                if (!flag){
                    Toast.makeText(RegisterAndLoginActivity.this,"注册成功,欢迎您"+name,Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(RegisterAndLoginActivity.this,"注册失败,请稍后再试",Toast.LENGTH_SHORT).show();
                }
            }else {
                //登录成功后
                if (pwd.equals(user_pwd)){
                    Toast.makeText(RegisterAndLoginActivity.this,"登录成功,欢迎您"+name,Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(APPNAME);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("user",userBean);
//                    intent.putExtra("user",bundle);
//                    sendBroadcast(intent);
                    Intent intent1 = new Intent(RegisterAndLoginActivity.this,MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user",userBean);
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                    finish();
                }else {
                    //登录失败后；
                    Toast.makeText(RegisterAndLoginActivity.this,"登录失败，请检查用户名和密码是否正确",Toast.LENGTH_SHORT).show();
                    register_password.setText("");
                    register_name.setText("");
                    register_tel.setText("");
                    return;
                }
            }
        }
    }
}
