package mjd.com.usedbookplatform.switchIndex;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mjd.com.usedbookplatform.R;
import mjd.com.usedbookplatform.switchIndex.bookBean.SellerInfo;
import mjd.com.usedbookplatform.switchIndex.bookBean.bookInfo;

public class BuyBookDetail extends AppCompatActivity {
    private bookInfo bookData;
    private SellerInfo sellerInfo;
    private NumberPicker numberPicker;
    private EditText buyAddress,buyTel,buyAddition;
    private TextView buy_book_sellerInfo,buy_book_info;
    private Button confirmBuyIt;
    private int buyBookNumber = 0;
    private String strBuyAddress,strBuyTel,strBuyAddition;
    private String sendMessage;
    private String urlFromString;
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_book_detail);
        setTitle("交易详情页");
        bookData = (bookInfo) getIntent().getExtras().getSerializable("bookData");
        sellerInfo = (SellerInfo) getIntent().getExtras().getSerializable("sellerInfo");
        urlFromString = getResources().getString(R.string.url);
        pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,BuyBookDetail.class),0);
        initWidget();
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                buyBookNumber = newVal;
            }
        });
    }

    private void initWidget() {
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(0);
        buyAddress = (EditText) findViewById(R.id.buyAddress);
        buyTel = (EditText) findViewById(R.id.buyTel);
        buy_book_sellerInfo = (TextView) findViewById(R.id.buy_book_sellerInfo);
        confirmBuyIt = (Button) findViewById(R.id.confirmBuyIt);
        buyAddition = (EditText) findViewById(R.id.buyAddition);
        buy_book_info = (TextView) findViewById(R.id.buy_book_info);
        buy_book_info.setText("书籍的姓名: ' "+bookData.getBook_name()+" '");

        if (sellerInfo!=null){
            buy_book_sellerInfo.setText("售卖书籍者的姓名: " + sellerInfo.getSeller_name() + "  " + "联系方式: " + sellerInfo.getSeller_tel());
        }

        confirmBuyIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = checkTelAndAddress();
                if (!flag){
                    return;
                }
                buyBook(v);
            }
        });
    }
    //检查地址和用户号码是否合法;,后期判断优化(优化后删除此话)
    private boolean checkTelAndAddress() {
        strBuyAddress = buyAddress.getText().toString().trim();
        strBuyTel = buyTel.getText().toString().trim();
        strBuyAddition = buyAddition.getText().toString().trim();
        sendMessage = "1,"+strBuyAddress+";2,"+strBuyTel+";3,"+strBuyAddition;
        if (strBuyTel == null){
            Toast.makeText(BuyBookDetail.this,"请填写联系方式",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            String regEx = "[0-9]{11}";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(strBuyTel);
            boolean rs = matcher.matches();
            if (!rs){
                Toast.makeText(BuyBookDetail.this,"手机号码填写失败，请检查",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public void goBack(View view) {
        this.finish();
    }

    public void buyBook(View view) {
        if (buyBookNumber == 0){
            Toast.makeText(BuyBookDetail.this,"您还没有选择任何书籍",Toast.LENGTH_SHORT).show();
        }else if(Integer.parseInt(bookData.getBook_amount()) < buyBookNumber){
            Toast.makeText(BuyBookDetail.this,"您选择的数量超过余量了,当前剩余 : " + Integer.parseInt(bookData.getBook_amount()),Toast.LENGTH_SHORT).show();
        }else {
            new AlertDialog.Builder(BuyBookDetail.this).setTitle("购买提示: ").setMessage("您购买的书籍为:"+bookData.getBook_name()+",数量为:"+buyBookNumber+"\n"+
                "您的信息如下:\n   联系方式:"+strBuyTel+",住址为:"+strBuyAddress+",附加信息: " + strBuyAddition)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendMessageToSeller(sendMessage);
                        }
                    }).setNegativeButton("取消",null).setCancelable(false).create().show();
        }
    }

    private void sendMessageToSeller(String sendMessage) {
        //将信息发送给卖家,将数据库中的数量减少buyBookNumber；
        sendMessageDetail();
        new Thread(new Runnable() {
            @Override
            public void run()  {
                Connection connection = null;
                String url = "jdbc:mysql://"+urlFromString+":3306/usedbook";
                Statement statement = null;
                ResultSet result = null;
                int leaveBookNumber = Integer.parseInt(bookData.getBook_amount()) - buyBookNumber;
                String sql = "update book_detailinfo set book_amount = "+ leaveBookNumber +" where seller_sellbook_isbn = '"+bookData.getSeller_sellbook_isbn()+"'";
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection(url,"root","131365");
                    statement = connection.createStatement();
                    final boolean flag = statement.execute(sql);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!flag){
                                Toast.makeText(BuyBookDetail.this,"购买成功,及时联系售卖进行付费哦",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(BuyBookDetail.this,"购买失败",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
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
    }

    private void sendMessageDetail() {
        if (Build.VERSION.SDK_INT >= 23 ){
            if (ActivityCompat.checkSelfPermission(BuyBookDetail.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BuyBookDetail.this,new String[]{Manifest.permission.SEND_SMS},200);
                return;
            }
        }else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(strBuyTel,null,sendMessage,pendingIntent,null);
            Toast.makeText(BuyBookDetail.this,"信息发送成功",Toast.LENGTH_SHORT).show();
            return;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(strBuyTel,null,sendMessage,pendingIntent,null);
            Toast.makeText(BuyBookDetail.this,"信息发送成功",Toast.LENGTH_SHORT).show();
            return;
        }else {
            Toast.makeText(BuyBookDetail.this,"请授权，这样才可以进行购买",Toast.LENGTH_LONG).show();
        }
    }
}
