package mjd.com.usedbookplatform.switchIndex.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mjd.com.usedbookplatform.R;
import mjd.com.usedbookplatform.switchIndex.bookBean.DIscussBean;
import mjd.com.usedbookplatform.switchIndex.bookBean.bookInfo;

/**
 * Created by IT之旅 on 2016-10-3.
 */
public class CreateTableAndQueryFromDatabase extends AsyncTask {
    private String sql;
    private Context context;
    private bookInfo bookData;
    private String urlFromString;
    private String createOrQueryFlag;
    private DIscussBean dIscussBean;
    private List<DIscussBean> listDiscuss;
    private ListView list_discuss_book;
    private TextView list_discuss_errorPage;
    private View view;
    private String insertContent;
    private String insertName;
    private boolean isOk = false;

    public CreateTableAndQueryFromDatabase(String sql, Context context, bookInfo bookData,String createOrQueryFlag,String content,String name) {
        this.sql = sql;
        System.out.println(sql+"测试是否正确");
        this.context = context;
        this.bookData = bookData;
        this.createOrQueryFlag = createOrQueryFlag;
        this.insertContent = content;
        this.insertName = name;
        urlFromString = context.getResources().getString(R.string.url);
        listDiscuss = new ArrayList<>();
        view = LayoutInflater.from(context).inflate(R.layout.activity_discuss_detail,null);
        list_discuss_book = (ListView) view.findViewById(R.id.list_discuss_book);
        list_discuss_errorPage = (TextView) view.findViewById(R.id.list_discuss_errorPage);
    }

    String insertSQL(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String localDateTime = dateFormat.format(date);
        String sql_insert = "INSERT INTO " + bookData.getBook_name() + " VALUES('"+ insertName+"','"+insertContent+"','"+localDateTime+"',0)";
        System.out.println(sql_insert);
        return sql_insert;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        setOrGetFromDatabase(sql);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (!isOk){
            Toast.makeText(context,"评论成功",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context,"评论失败，请检查网络设置",Toast.LENGTH_SHORT).show();
        }
    }

    private void setOrGetFromDatabase(String sql) {
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        String url = "jdbc:mysql://"+urlFromString+":3306/usedbook";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url,"root","131365");
            statement = connection.createStatement();
            if ("query".equals(createOrQueryFlag)){
                result = statement.executeQuery(sql);
                while (result.next()){
                    dIscussBean = new DIscussBean();
                    dIscussBean.setUser_name(result.getString("user_name"));
                    dIscussBean.setDiscuss_content(result.getString("discuss_content"));
                    dIscussBean.setDiscuss_time(result.getString("discuss_time"));
                    dIscussBean.setDiscuss_amount(result.getInt("discuss_amount")+"");
                    listDiscuss.add(dIscussBean);
                }
            }else if ("create".equals(createOrQueryFlag)){
                statement.execute(sql);
                isOk = statement.execute(insertSQL());

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
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
