package mjd.com.usedbookplatform.switchIndex;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import mjd.com.usedbookplatform.R;
import mjd.com.usedbookplatform.switchIndex.bookBean.SellerInfo;
import mjd.com.usedbookplatform.switchIndex.bookBean.bookInfo;
import mjd.com.usedbookplatform.switchIndex.utils.CreateTableAndQueryFromDatabase;
import mjd.com.usedbookplatform.switchIndex.utils.ImageLoaderPictuer;

public class DetailListviewBookInfoActivity extends AppCompatActivity {

    private bookInfo bookData;
    private TextView list_detail_text_bookInfo, list_detail_text_isbn, list_detail_text_bookName, list_detail_text_version, list_detail_text_bookAutho,
            list_detail_text_bookPrice, list_detail_text_bookTime, list_detail_text_bookDecalre, list_detail_text_sellerName, list_detail_text_sellerTel,
            list_detail_text_sellerExitAmount;
    private ImageView list_detail_bookInfoActivity_img;
    private Button detail_bookInfo_editDiscuss, detail_bookInfo_lookDiscuss, detail_bookInfo_buy, detail_bookInfo_connectAuthor;
    private SearchSellerInfo searchSellerInfo;
    private SellerInfo sellerInfo;
    private String urlFromString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_listview_book_info);
        urlFromString = getResources().getString(R.string.url);
        bookData = (bookInfo) getIntent().getExtras().getSerializable("bookInfo");
        findWidget();//找到各种控件
        setContentForWidget();

    }

    private void setContentForWidget() {
        list_detail_text_isbn.append(bookData.getSeller_sellbook_isbn());
        list_detail_text_bookName.append(bookData.getBook_name());
        list_detail_text_version.append(bookData.getBook_version());
        list_detail_text_bookAutho.append(bookData.getBook_author());
        list_detail_text_bookPrice.append(bookData.getBook_price());
        list_detail_text_bookTime.append(bookData.getBook_time());
        list_detail_text_bookDecalre.append(bookData.getBook_sell_declare());
        list_detail_text_sellerName.append(bookData.getSeller_register_name());
        list_detail_text_sellerExitAmount.append(bookData.getBook_amount() + " 本");
        //获取卖者信息
        searchSellerInfo = new SearchSellerInfo(DetailListviewBookInfoActivity.this);
        searchSellerInfo.execute();

    }

    private void findWidget() {
        list_detail_text_bookInfo = (TextView) findViewById(R.id.list_detail_text_bookInfo);
        list_detail_text_bookInfo.setText(bookData.toString());
        list_detail_text_isbn = (TextView) findViewById(R.id.list_detail_text_isbn);
        list_detail_text_bookName = (TextView) findViewById(R.id.list_detail_text_bookName);
        list_detail_text_version = (TextView) findViewById(R.id.list_detail_text_version);
        list_detail_text_bookAutho = (TextView) findViewById(R.id.list_detail_text_bookAutho);
        list_detail_text_bookPrice = (TextView) findViewById(R.id.list_detail_text_bookPrice);
        list_detail_text_bookTime = (TextView) findViewById(R.id.list_detail_text_bookTime);
        list_detail_text_bookDecalre = (TextView) findViewById(R.id.list_detail_text_bookDecalre);
        list_detail_text_sellerName = (TextView) findViewById(R.id.list_detail_text_sellerName);
        list_detail_text_sellerTel = (TextView) findViewById(R.id.list_detail_text_sellerTel);
        list_detail_text_sellerExitAmount = (TextView) findViewById(R.id.list_detail_text_sellerExitAmount);
        list_detail_bookInfoActivity_img = (ImageView) findViewById(R.id.list_detail_bookInfoActivity_img);
        System.out.println(bookData);

        ImageLoader.getInstance().displayImage(bookData.getBookImgUrl(),list_detail_bookInfoActivity_img,new ImageLoaderPictuer(this).getOptions(),new SimpleImageLoadingListener());

        detail_bookInfo_connectAuthor = (Button) findViewById(R.id.detail_bookInfo_connectAuthor);
        detail_bookInfo_buy = (Button) findViewById(R.id.detail_bookInfo_buy);
        detail_bookInfo_editDiscuss = (Button) findViewById(R.id.detail_bookInfo_editDiscuss);
        detail_bookInfo_lookDiscuss = (Button) findViewById(R.id.detail_bookInfo_lookDiscuss);


        detail_bookInfo_connectAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 ){
                    if (ActivityCompat.checkSelfPermission(DetailListviewBookInfoActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DetailListviewBookInfoActivity.this,new String[]{Manifest.permission.CALL_PHONE},200);
                        return;
                    }
                }else {
                    callConnecor();
                    return;
                }


            }
        });
        detail_bookInfo_lookDiscuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailListviewBookInfoActivity.this,DiscussActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("data",bookData);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
        detail_bookInfo_editDiscuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(DetailListviewBookInfoActivity.this)
                        .inflate(R.layout.activity_discuss_dialog_editdiscuss,null);
                final EditText discuss_edit_dialog = (EditText) linearLayout.findViewById(R.id.discuss_edit_dialog);
                final EditText discuss_name = (EditText) linearLayout.findViewById(R.id.discuss_name);
                new AlertDialog.Builder(DetailListviewBookInfoActivity.this).setTitle("请输入评论内容: ").setView(linearLayout)
                        .setPositiveButton("发送", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String content = discuss_edit_dialog.getText().toString();
                                String name = discuss_name.getText().toString();
                                sendDiscuss(content,name);
                            }
                        }).setNegativeButton("取消",null).setCancelable(false).create().show();
            }
        });
        detail_bookInfo_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(DetailListviewBookInfoActivity.this)
//                        .inflate(R.layout.buy_book_detail,null);
//                new AlertDialog.Builder(DetailListviewBookInfoActivity.this).setTitle("购买提示: ").setView(linearLayout)
//                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        }).setNegativeButton("再想一想",null).setCancelable(false).create().show();
                Intent intent = new Intent(DetailListviewBookInfoActivity.this,BuyBookDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bookData",bookData);
                bundle.putSerializable("sellerInfo",sellerInfo);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private void sendDiscuss(String content,String name) {
        String sql = "CREATE TABLE IF NOT EXISTS " + bookData.getBook_name() +
                "(user_name VARCHAR(255), discuss_content VARCHAR(255), discuss_time VARCHAR(255), discuss_amount INT)";
        new CreateTableAndQueryFromDatabase(sql,DetailListviewBookInfoActivity.this,bookData,"create",content,name).execute();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            callConnecor();
            return;
        }else {
            Toast.makeText(DetailListviewBookInfoActivity.this,"请授权，这样才可以进行拨打电话",Toast.LENGTH_LONG).show();
        }
    }
    public void callConnecor(){
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + sellerInfo.getSeller_tel());
        intent.setData(data);
        startActivity(intent);
    }

    class SearchSellerInfo extends AsyncTask{

       private Context context;

        public SearchSellerInfo(Context context) {
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            findSeller();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (sellerInfo == null){
                list_detail_text_sellerTel.setPadding(0,20,0,0);
                list_detail_text_sellerTel.setText("售卖作者联系电话: 暂未获取到相关信息");
            }else {
                list_detail_text_sellerTel.setText("售卖作者联系电话: "+sellerInfo.getSeller_tel());
                detail_bookInfo_connectAuthor.setVisibility(View.VISIBLE);
            }
        }

        void findSeller(){
            Connection connection = null;
            String url = "jdbc:mysql://"+urlFromString+":3306/usedbook?useUnicode=true&characterEncoding=UTF-8";
            Statement statement = null;
            ResultSet result = null;
            String sql = "select * from seller where seller_register_name = '" + bookData.getSeller_register_name() + "'";
            System.out.println(sql);
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(url,"root","131365");
                statement = connection.createStatement();
                result = statement.executeQuery(sql);
                while (result.next()){
                    sellerInfo = new SellerInfo();
                    sellerInfo.setSeller_register_name(result.getString("seller_register_name"));
                    sellerInfo.setSeller_name(result.getString("seller_name"));
                    sellerInfo.setSeller_tel(result.getString("seller_tel"));
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
