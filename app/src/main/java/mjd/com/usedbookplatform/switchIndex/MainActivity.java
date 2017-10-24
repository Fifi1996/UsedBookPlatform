package mjd.com.usedbookplatform.switchIndex;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import mjd.com.usedbookplatform.R;
import mjd.com.usedbookplatform.switchIndex.bookBean.SellerInfo;
import mjd.com.usedbookplatform.switchIndex.bookBean.UserBean;


public class MainActivity extends AppCompatActivity {
    private UserBean userBean;
    private SellerInfo sellerInfo;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bundle = getIntent().getExtras();
        if ( bundle!=null ){
            userBean = (UserBean) bundle.get("user");
        }
        initTablayout();
    }

    private void initTablayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PageFragmentAdapter pageFragmentAdapter = new PageFragmentAdapter(getSupportFragmentManager(),MainActivity.this,userBean);

        viewPager.setAdapter(pageFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void searchActivity(View view) {
        Intent intent = new Intent(MainActivity.this,SearchActivity.class);
        startActivity(intent);
    }

//    //注册登录，跳转到，RegisterAndLoginActivity
//    public void userRegister(View view) {
//        Intent intent = new Intent(MainActivity.this,RegisterAndLoginActivity.class);
//        startActivity(intent);
//    }


    //关于我们版块的关于我们
    public void aboutUs(View view) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.description,null);
        new AlertDialog.Builder(this).setTitle("关于我们").setView(linearLayout).setCancelable(false).
                setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this,"欢迎您联系我们",Toast.LENGTH_SHORT).show();
            }
        }).create().show();
    }
}
