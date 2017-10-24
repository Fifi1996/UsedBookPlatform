package mjd.com.usedbookplatform.switchIndex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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

public class BookEntriesActivity extends AppCompatActivity {

    private String bookMark;
    private TextView activity_book_entries_title;
    private ListView activity_book_entries_listView;
    private ImageView activity_book_entries_notFoundPic;
    private List<bookInfo> list;
    private String jdbc;
    private String urlFromString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_entries);
        bookMark = getIntent().getStringExtra("bookMark");
        activity_book_entries_listView = (ListView) findViewById(R.id.activity_book_entries_listView);
        activity_book_entries_notFoundPic = (ImageView) findViewById(R.id.activity_book_entries_notFoundPic);
        activity_book_entries_title = (TextView) findViewById(R.id.activity_book_entries_title);
        activity_book_entries_title.setText("欢迎选择 '" + bookMark + "' 类书籍");
        urlFromString = getResources().getString(R.string.url);
        jdbc = getResources().getString(R.string.jdbc);
        list = new ArrayList<>();
        new SearchEntries(BookEntriesActivity.this).execute();
    }

    private class SearchEntries extends AsyncTask{

        private Context context;
        private ProgressDialog progressDialog;
        public SearchEntries(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("书籍资源正在加载中,请稍后•••");
            progressDialog.setCancelable(false);
            progressDialog.create();
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
            String sql = "select * from book_detailinfo where book_mark =(select book_mark FROM book_all where book_name ='"+bookMark+"')";
            String sqlOne = "select * from  book_detailinfo where book_mark ='computer'";
            String sqlTest = "select * from  book_detailinfo";
            String url = "jdbc:mysql://"+urlFromString+":3306/usedbook?useUnicode=true&characterEncoding=UTF-8";
            System.out.println(sql);
            try {
                Class.forName(jdbc);
                connection = DriverManager.getConnection(url,"root","131365");
                statement = connection.createStatement();
                resultSet = statement.executeQuery(sql);
                bookInfo bookData;
                while (resultSet.next()){
                    bookData = new bookInfo();
                    bookData.setSeller_sellbook_isbn(resultSet.getString("seller_sellbook_isbn"));
                    bookData.setBook_name(resultSet.getString("book_name"));
                    bookData.setBook_mark(resultSet.getString("book_mark"));
                    bookData.setBook_version(resultSet.getString("book_version"));
                    bookData.setBook_author(resultSet.getString("book_author"));
                    bookData.setBook_price(resultSet.getInt("book_price")+"");
                    bookData.setBook_sell_declare(resultSet.getString("book_sell_declare"));
                    bookData.setBook_time(resultSet.getString("book_time"));
                    bookData.setSeller_register_name(resultSet.getString("seller_register_name"));
                    bookData.setBookImgUrl(resultSet.getString("book_img_url"));
                    list.add(bookData);
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
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (progressDialog!=null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }
            if (list.size()>0){
                activity_book_entries_listView.setAdapter(new ListHotBookAdapter(BookEntriesActivity.this,list));
            } else {
                activity_book_entries_listView.setVisibility(View.GONE);
                activity_book_entries_notFoundPic.setVisibility(View.VISIBLE);
            }
            activity_book_entries_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bookInfo",list.get(position));
                    Intent intent = new Intent(BookEntriesActivity.this,DetailListviewBookInfoActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }
}
