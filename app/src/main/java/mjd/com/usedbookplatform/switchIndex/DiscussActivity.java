package mjd.com.usedbookplatform.switchIndex;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mjd.com.usedbookplatform.R;
import mjd.com.usedbookplatform.switchIndex.bookBean.DIscussBean;
import mjd.com.usedbookplatform.switchIndex.bookBean.bookInfo;

public class DiscussActivity extends AppCompatActivity {

    private TextView discuss_title,list_discuss_errorPage;
    private ListView list_discuss_book;
    private bookInfo bookData;
    private DIscussBean dIscussBean;
    private List<DIscussBean> listDiscuss;
    private Button list_discuss_sendDiscuss;
    private EditText list_discuss_editDiscuss;
    private String urlFromString;
    private ProgressDialog showStepGetData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);
        bookData = (bookInfo) getIntent().getExtras().getSerializable("data");
        listDiscuss = new ArrayList<>();
        urlFromString = getResources().getString(R.string.url);
        initWidget();
        getDataFromDatabase();
    }

    private void getDataFromDatabase() {
        new DiscussData(DiscussActivity.this).execute();
    }

    private void initWidget() {
        discuss_title = (TextView) findViewById(R.id.discuss_title);
        discuss_title.setText("书籍 '"+bookData.getBook_name()+" ' 的热门评论");
        list_discuss_book = (ListView) findViewById(R.id.list_discuss_book);
        list_discuss_sendDiscuss = (Button) findViewById(R.id.list_discuss_sendDiscuss);
        list_discuss_editDiscuss = (EditText) findViewById(R.id.list_discuss_editDiscuss);
        list_discuss_errorPage = (TextView) findViewById(R.id.list_discuss_errorPage);
    }


    class DiscussData extends AsyncTask {
        private Context context;
        public DiscussData(Context context) {
            this.context = context;
            showStepGetData = new ProgressDialog(context);
            showStepGetData.setMessage("正在火速获取评论中....");
            showStepGetData.setCancelable(false);
            showStepGetData.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            getDataFromDataBase();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (showStepGetData!=null && showStepGetData.isShowing()){
                showStepGetData.dismiss();
            }
            if (listDiscuss !=null){
                list_discuss_book.setVisibility(View.VISIBLE);
                list_discuss_book.setAdapter(new DiscussAdapter(DiscussActivity.this,listDiscuss,bookData.getBook_name()));
                //agreeWithIt();
            }else {
                list_discuss_book.setVisibility(View.GONE);
                list_discuss_errorPage.setVisibility(View.VISIBLE);
            }

        }

        void getDataFromDataBase(){
            Connection connection = null;
            String url = "jdbc:mysql://"+urlFromString+":3306/usedbook?useUnicode=true&characterEncoding=UTF-8";
            Statement statement = null;
            ResultSet result = null;
            String sql = "select * from "+bookData.getBook_name() +" order by discuss_amount DESC";
            System.out.println(sql);
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(url,"root","131365");
                statement = connection.createStatement();
                result = statement.executeQuery(sql);
                while (result.next()){
                    dIscussBean = new DIscussBean();
                    dIscussBean.setUser_name(result.getString("user_name"));
                    dIscussBean.setDiscuss_content(result.getString("discuss_content"));
                    dIscussBean.setDiscuss_time(result.getString("discuss_time"));
                    dIscussBean.setDiscuss_amount(result.getInt("discuss_amount")+"");
                    listDiscuss.add(dIscussBean);
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("sql 语句错误");
            }finally {
                if (connection!=null){
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
