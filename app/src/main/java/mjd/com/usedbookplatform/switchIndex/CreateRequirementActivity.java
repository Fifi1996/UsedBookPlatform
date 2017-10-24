package mjd.com.usedbookplatform.switchIndex;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import mjd.com.usedbookplatform.switchIndex.bookBean.RequireMentBean;
import mjd.com.usedbookplatform.switchIndex.bookBean.UserBean;

public class CreateRequirementActivity extends AppCompatActivity implements View.OnClickListener{

    private UserBean userBean;
    private TextView create_requirement,create_requirement_wait;
    private Button create_requirement_newRequire,create_requirement_cancel;
    private ListView create_requirement_list;
    private ProgressDialog dialog;
    private String jdbcString,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_requirement);
        userBean = (UserBean) getIntent().getExtras().get("user");
        jdbcString = getResources().getString(R.string.jdbc);
        url = getResources().getString(R.string.url);
        initViews();

    }

    private void initViews() {
        create_requirement = (TextView) findViewById(R.id.create_requirement);
        create_requirement.append(userBean.getUser_name());
        create_requirement_wait = (TextView) findViewById(R.id.create_requirement_wait);

        create_requirement_wait.setVisibility(View.VISIBLE);

        create_requirement_list = (ListView) findViewById(R.id.create_requirement_list);
        new ListRequire(userBean.getUser_require_id_time()).execute();

        create_requirement_newRequire = (Button) findViewById(R.id.create_requirement_newRequire);
        create_requirement_cancel = (Button) findViewById(R.id.create_requirement_cancel);

        create_requirement_newRequire.setOnClickListener(this);
        create_requirement_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_requirement_cancel:
                finish();
                break;
            case R.id.create_requirement_newRequire:
                createRequirement();
                break;
        }
    }

    private void createRequirement() {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(CreateRequirementActivity.this).inflate(R.layout.create_new_require,null);
        final EditText requirement = (EditText) layout.findViewById(R.id.create_new_requirement);
        new AlertDialog.Builder(CreateRequirementActivity.this).setTitle("需求写入中").setView(layout).setPositiveButton("发布", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str_requirement = requirement.getText().toString().trim();
                if (TextUtils.isEmpty(str_requirement)){
                    Toast.makeText(CreateRequirementActivity.this,"您未键入任何内容，发布失败",Toast.LENGTH_LONG).show();
                    return;
                }
                new CreateNewRequirement(str_requirement).execute();
            }
        }).setNegativeButton("放弃",null).setCancelable(false).create().show();
    }

    class CreateNewRequirement extends AsyncTask{
        private String str_requirement;
        private String url_str;
        private String sql;
        private boolean flag;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        private String registerTime = simpleDateFormat.format(date);

        public CreateNewRequirement(String str_requirement) {
            this.str_requirement = str_requirement;
            sql = "insert into user_require values('" + userBean.getUser_require_id_time() + "','" + userBean.getUser_contact_information() + "','" +
                    str_requirement + "','" + registerTime + "',0,0)";
            System.out.println(sql);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CreateRequirementActivity.this);
            dialog.setMessage("火速送递需求中，请稍后.....");
            dialog.create();
            dialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            url_str = "jdbc:mysql://"+url+":3306/usedbook";
            Connection connection = null;
            Statement statement = null;
            try {
                Class.forName(jdbcString);
                connection = DriverManager.getConnection(url_str,"root","131365");
                statement = connection.createStatement();
                flag = statement.execute(sql);
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
            if (dialog!=null){
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }
            if (!flag){
                Toast.makeText(CreateRequirementActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
            }
            if (flag){
                Toast.makeText(CreateRequirementActivity.this,"发布失败，请稍后再试",Toast.LENGTH_SHORT).show();
            }

        }
    }

    class ListRequirementAdapter extends BaseAdapter{
        private List<RequireMentBean> requireMentBeen;

        public ListRequirementAdapter(List<RequireMentBean> requireMentBeen) {
            this.requireMentBeen = requireMentBeen;
        }

        @Override
        public int getCount() {
            return requireMentBeen.size();
        }

        @Override
        public Object getItem(int position) {
            return requireMentBeen.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(CreateRequirementActivity.this).inflate(R.layout.company_voice_detail,null);
            TextView voice_id = (TextView) layout.findViewById(R.id.voice_id);
            TextView voice_time = (TextView) layout.findViewById(R.id.voice_time);
            TextView voice_needs = (TextView) layout.findViewById(R.id.voice_needs);
            TextView voice_reply = (TextView) layout.findViewById(R.id.voice_reply);
            TextView voice_needs_connect = (TextView) layout.findViewById(R.id.voice_needs_connect);
            TextView voice_isResolve = (TextView) layout.findViewById(R.id.voice_isResolve);

            RequireMentBean requireMentBeanEvery = requireMentBeen.get(position);

            if (requireMentBeanEvery.getIsResolve() == 1){
                voice_isResolve.setVisibility(View.VISIBLE);
            }
            voice_id.append(requireMentBeanEvery.getRequireIdTime());
            voice_time.append(requireMentBeanEvery.getRequireTime());
            voice_needs.append(requireMentBeanEvery.getRequireReply());
            voice_reply.append(requireMentBeanEvery.getRequireAmount()+"");
            voice_needs_connect.append(requireMentBeanEvery.getRequireIdTimeConnect());

            return layout;
        }
    }

    class ListRequire extends AsyncTask{

        private String url_str;
        private String idTimeStamp;
        private String sql;
        private List<RequireMentBean> requireMentBeen;

        public ListRequire(String idTimeStamp) {
            this.idTimeStamp = idTimeStamp;
            sql = "select * from user_require where require_id_time = '" + this.idTimeStamp + "'";
            requireMentBeen = new ArrayList<>();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            url_str = "jdbc:mysql://"+url+":3306/usedbook?useUnicode=true&characterEncoding=UTF-8";
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
                try {
                    Class.forName(jdbcString);
                    connection = DriverManager.getConnection(url_str,"root","131365");
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(sql);
                    while (resultSet.next()){
                        RequireMentBean requirement = new RequireMentBean();
                        requirement.setRequireIdTime(resultSet.getString("require_id_time"));
                        requirement.setRequireReply(resultSet.getString("require_content"));
                        requirement.setRequireTime(resultSet.getString("require_time"));
                        requirement.setRequireAmount(resultSet.getInt("require_amount"));
                        requirement.setRequireIdTimeConnect(resultSet.getString("require_id_time_connect"));
                        requirement.setIsResolve(resultSet.getInt("require_flag"));
                        requireMentBeen.add(requirement);
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

            if (requireMentBeen == null){
                create_requirement_wait.setText("您还没有发不过任何需求哦");
                create_requirement_list.setVisibility(View.GONE);
            }
            //适配器对象
            final ListRequirementAdapter listRequirementAdapter = new ListRequirementAdapter(requireMentBeen);
            create_requirement_list.setAdapter(listRequirementAdapter);
            create_requirement_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final RequireMentBean requirement =requireMentBeen.get(position);
                    LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(CreateRequirementActivity.this).inflate(R.layout.user_require_modify_detail,null);
                    TextView amount = (TextView) linearLayout.findViewById(R.id.user_require_modify_detail_replyAmount);
                    TextView content = (TextView) linearLayout.findViewById(R.id.user_require_modify_detail_content);
                    amount.append(requirement.getRequireAmount()+"");
                    content.setText("  " + requirement.getRequireReply());
                    new AlertDialog.Builder(CreateRequirementActivity.this).setTitle("需求详情，关闭需求也提示").setView(linearLayout).setPositiveButton("关闭需求", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AsyncTask(){
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    String sql_makeRequireSolved = "UPDATE user_require set require_flag = 1  where require_content = '" +
                                            requirement.getRequireReply() + "' and require_id_time = '"  + requirement.getRequireIdTime() + "'";
                                    System.out.println(sql_makeRequireSolved);
                                    url_str = "jdbc:mysql://"+url+":3306/usedbook?useUnicode=true&characterEncoding=UTF-8";
                                    Connection connection = null;
                                    Statement statement = null;
                                    ResultSet resultSet = null;
                                    try {
                                        Class.forName(jdbcString);
                                        connection = DriverManager.getConnection(url_str,"root","131365");
                                        statement = connection.createStatement();
                                        statement.execute(sql_makeRequireSolved);
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
                                    Toast.makeText(CreateRequirementActivity.this,"需求已经关闭成功",Toast.LENGTH_SHORT).show();
                                    listRequirementAdapter.notifyDataSetChanged();
                                }
                            }.execute();
                        }
                    }).setNegativeButton("取消",null).create().show();

                }
            });
            create_requirement_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    new AlertDialog.Builder(CreateRequirementActivity.this).setTitle("删除需求提示").setMessage("需求删除后，将不可恢复，请谨慎删除").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final RequireMentBean requirement =requireMentBeen.get(position);
                            new AsyncTask(){
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    String sql_delete = "DELETE FROM  user_require where require_content = '" + requirement.getRequireReply()
                                            + "' and require_id_time = '" +requirement.getRequireIdTime() +"'";
                                    System.out.println(sql_delete);
                                    url_str = "jdbc:mysql://"+url+":3306/usedbook?useUnicode=true&characterEncoding=UTF-8";
                                    Connection connection = null;
                                    Statement statement = null;
                                    ResultSet resultSet = null;
                                    try {
                                        Class.forName(jdbcString);
                                        connection = DriverManager.getConnection(url_str,"root","131365");
                                        statement = connection.createStatement();
                                        statement.execute(sql_delete);
                                        listRequirementAdapter.notifyDataSetChanged();
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
                                    Toast.makeText(CreateRequirementActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                }
                            }.execute();
                        }
                    }).setNegativeButton("取消",null).create().show();
                    return false;
                }
            });

        }
    }
}
