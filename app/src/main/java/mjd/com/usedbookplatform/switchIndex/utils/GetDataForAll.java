package mjd.com.usedbookplatform.switchIndex.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mjd.com.usedbookplatform.R;
import mjd.com.usedbookplatform.switchIndex.bookBean.bookInfo;

/**
 * Created by IT之旅 on 2016-10-1.
 */
public class GetDataForAll {
    private List<bookInfo> bookInfoList;
    private Context context;
    private ProgressDialog dialog;
    private String urlFromString;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog!=null && dialog.isShowing()){
                dialog.dismiss();
            }
            bookInfoList = (List<bookInfo>) msg.obj;
            System.out.println(bookInfoList);
        }
    };

    public GetDataForAll(Context context) {
        this.context = context;
        urlFromString = context.getResources().getString(R.string.url);
    }

    public List<bookInfo> getDataFromDatabase(){
        bookInfoList = new ArrayList<>();
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("正在努力加载中....");
        dialog.show();
        dialog.setCancelable(false);

        new Thread(new Runnable() {
            @Override
            public void run()  {
                Connection connection = null;
                String url = "jdbc:mysql://"+urlFromString+":3306/usedbook";
                Statement statement = null;
                ResultSet result = null;
                String sql = "select * from book_detailinfo";
                bookInfo bookData;
                List<bookInfo> bookInfoList_bookData = new ArrayList<bookInfo>();
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection(url,"root","131365");
                    statement = connection.createStatement();
                    result = statement.executeQuery(sql);
                    while (result.next()){
                        bookData = new bookInfo();
                        bookData.setSeller_sellbook_isbn(result.getString("seller_sellbook_isbn"));
                        bookData.setBook_name(result.getString("book_name"));
                        bookData.setBook_version(result.getString("book_version"));
                        bookData.setBook_author(result.getString("book_author"));
                        bookData.setBook_price(result.getInt("book_price")+"");
                        bookData.setBook_sell_declare(result.getString("book_sell_declare"));
                        bookData.setSeller_register_name(result.getString("seller_register_name"));
                        bookInfoList_bookData.add(bookData);
                    }
                    Message msg = Message.obtain();
                    msg.obj = bookInfoList_bookData;
                    handler.sendMessage(msg);
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
        }).start();
        System.out.println(bookInfoList+"---->>>>>>>>>>");
        return bookInfoList;
    }


}
