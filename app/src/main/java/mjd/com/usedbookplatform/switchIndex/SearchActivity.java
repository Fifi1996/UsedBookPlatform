package mjd.com.usedbookplatform.switchIndex;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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
import mjd.com.usedbookplatform.switchIndex.bookBean.bookInfo;

public class SearchActivity extends AppCompatActivity {

    private EditText searceText;
    private String search_sql;
    private ProgressDialog dialog;
    private Button searchButton;
    private List<bookInfo> bookInfoList;
    private String urlFromString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        urlFromString = getResources().getString(R.string.url);
        searceText = (EditText) findViewById(R.id.search_edit_forBook);
        searchButton = (Button) findViewById(R.id.search_send_forBook);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_sql = searceText.getText().toString();
                //Toast.makeText(SearchActivity.this,search_sql,Toast.LENGTH_LONG).show();
                searchForBook(search_sql);
            }
        });
    }

    void searchForBook(final String search_field){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ListView listView = (ListView) findViewById(R.id.search_listView);
                TextView listBook_errorPage = (TextView) findViewById(R.id.listBook_errorPage);
                TextView search_declare = (TextView) findViewById(R.id.search_declare);
                if (dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
                switch (msg.what){
                    case 0:
                        //Toast.makeText(SearchActivity.this,"网络错误，请检查后重新再试",Toast.LENGTH_LONG).show();
                        listView.setVisibility(View.GONE);
                        search_declare.setVisibility(View.GONE);
                        listBook_errorPage.setVisibility(View.VISIBLE);
                        listBook_errorPage.setText("网络错误，请检查后重新再试");
                        return;
                    case 1:
                        bookInfoList = (List<bookInfo>) msg.obj;
                        search_declare.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        System.out.println(bookInfoList);
                        break;
                    case 2:
                        listView.setVisibility(View.GONE);
                        listBook_errorPage.setVisibility(View.VISIBLE);
                        return;
                }


                listView.setAdapter(new ListHotBookAdapter(SearchActivity.this,bookInfoList));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bookInfo",bookInfoList.get(position));
                        Intent intent = new Intent(SearchActivity.this,DetailListviewBookInfoActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                });
            }
        };

        dialog = new ProgressDialog(SearchActivity.this);
        dialog.setIcon(R.drawable.hot_dialog_title);
        dialog.setTitle("检索书籍信息: ");
        dialog.setMessage("正在努力检索中...");
        dialog.show();
        dialog.setCancelable(false);

        new Thread(new Runnable() {
            @Override
            public void run()  {
                Connection connection = null;
                String url = "jdbc:mysql://"+urlFromString+":3306/usedbook?useUnicode=true&characterEncoding=UTF-8";
                Statement statement = null;
                ResultSet result = null;
                String sql = "select * from book_detailinfo where book_name like '%"+search_field +"%'";
                System.out.println(sql);
                bookInfo bookData;
                List<bookInfo> bookInfoList_bookData = new ArrayList<bookInfo>();
                Message msg = Message.obtain();
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection(url,"root","131365");
                    statement = connection.createStatement();
                    result = statement.executeQuery(sql);
                    while (result.next()){
                        bookData = new bookInfo();
                        bookData.setSeller_sellbook_isbn(result.getString("seller_sellbook_isbn"));
                        bookData.setBook_name(result.getString("book_name"));
                        bookData.setBook_mark(result.getString("book_mark"));
                        bookData.setBook_version(result.getString("book_version"));
                        bookData.setBook_author(result.getString("book_author"));
                        bookData.setBook_price(result.getInt("book_price")+"");
                        bookData.setBook_sell_declare(result.getString("book_sell_declare"));
                        bookData.setBook_time(result.getString("book_time"));
                        bookData.setSeller_register_name(result.getString("seller_register_name"));
                        bookInfoList_bookData.add(bookData);
                    }
                    if (bookInfoList_bookData.size() > 0 ){
                        msg.what = 1;
                        msg.obj = bookInfoList_bookData;
                        handler.sendMessage(msg);
                    }else {
                        msg.what=2;
                        handler.sendMessage(msg);
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                    msg.what = 0;
                    handler.sendMessage(msg);
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
    }
}
