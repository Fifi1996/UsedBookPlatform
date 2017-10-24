package mjd.com.usedbookplatform.switchIndex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import mjd.com.usedbookplatform.R;
import mjd.com.usedbookplatform.switchIndex.bookBean.DIscussBean;

/**
 * Created by IT之旅 on 2016-10-3.
 */
public class DiscussAdapter extends BaseAdapter {
    private List<DIscussBean> listDiscuss;
    private DIscussBean disContent;
    private LayoutInflater layoutInflater;
    private Context context;
    private String urlFromString;
    private String tableName;

    public DiscussAdapter(Context context, List<DIscussBean> listDiscuss,String tableName) {
        this.listDiscuss = listDiscuss;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        urlFromString = context.getResources().getString(R.string.url);
        this.tableName = tableName;
    }

    @Override
    public int getCount() {
        return listDiscuss.size();
    }

    @Override
    public Object getItem(int position) {
        return listDiscuss.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.activity_discuss_detail,null);
        TextView discuss_user_name = (TextView) layout.findViewById(R.id.discuss_user_name);
        TextView discuss_user_time = (TextView) layout.findViewById(R.id.discuss_user_time);
        TextView discuss_user_content = (TextView) layout.findViewById(R.id.discuss_user_content);
        TextView discuss_agree_amount = (TextView) layout.findViewById(R.id.discuss_agree_amount);

        disContent = listDiscuss.get(position);

        layout.findViewById(R.id.discuss_agree_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"hello",Toast.LENGTH_SHORT).show();
                TextView textView = (TextView) layout.findViewById(R.id.discuss_agree_amount);
                int nowAmount = Integer.parseInt(textView.getText().toString())+1;
                textView.setText(nowAmount+"");
                addAmountToDataBase(position,nowAmount,disContent.getUser_name());
                //notifyDataSetChanged();
            }
        });
        discuss_user_name.setText(disContent.getUser_name());
        discuss_user_time.setText(disContent.getDiscuss_time());
        discuss_user_content.setText(disContent.getDiscuss_content());
        discuss_agree_amount.setText(disContent.getDiscuss_amount());

        System.out.println(disContent+"=========>>>>>>");
        return layout;
    }

    private void addAmountToDataBase(int nowPosition, final int amount, final String userName) {
        listDiscuss.get(nowPosition).getUser_name();

        new Thread(){
            @Override
            public void run() {
                super.run();
                Connection connection = null;
                String url = "jdbc:mysql://"+urlFromString+":3306/usedbook";
                Statement statement = null;
                ResultSet result = null;
                String sql = "update "+tableName +" set discuss_amount = "+ amount +" where user_name = '"+userName+"'";
                System.out.println(sql);
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection(url,"root","131365");
                    statement = connection.createStatement();
                    boolean flag = statement.execute(sql);
                    if (flag){
                        System.out.println("成功");
                    }else {
                        System.out.println("失败");
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
        }.start();
    }
}
