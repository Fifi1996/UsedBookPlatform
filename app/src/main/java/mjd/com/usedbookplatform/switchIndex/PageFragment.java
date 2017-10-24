package mjd.com.usedbookplatform.switchIndex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mjd.com.usedbookplatform.R;
import mjd.com.usedbookplatform.switchIndex.bookBean.RequireMentBean;
import mjd.com.usedbookplatform.switchIndex.bookBean.SellerInfo;
import mjd.com.usedbookplatform.switchIndex.bookBean.UserBean;
import mjd.com.usedbookplatform.switchIndex.bookBean.bookInfo;
import mjd.com.usedbookplatform.switchIndex.utils.ACache;

/**
 * Created by IT之旅 on 2016-10-1.
 */
public class PageFragment extends Fragment {
    public static final String ARGS_PAGE = "args_page";
    public static Context contextnow;
    private int mPage;
    private List<bookInfo> bookInfoList;
    private ProgressDialog dialog;
    private static boolean isFirstFlag = true;
    private static String urlFromString;
    //pagetwo定义的数据
    private int currentItem = 0;//当前图片的索引号
    private List<ImageView> imageViews;//存放图片的集合
    private String[] titles;//图片标题
    private int[] imageResId; //图片ID
    private List<View> dots; //图片标题正文的点
    private TextView tv_title;
    private boolean isThree=false;
    private ScheduledExecutorService scheduledExecutorService;
    private String jdbcUrl;
//    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private UserBean userBean;
    private SellerInfo sellerInfo;
    public static PageFragment newInstance(int page, Context context,UserBean user){
        contextnow = context;
        Bundle args = new Bundle();
        args.putInt(ARGS_PAGE,page);
        args.putSerializable("user",user);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARGS_PAGE);
        urlFromString = getResources().getString(R.string.url);
        jdbcUrl = getResources().getString(R.string.jdbc);
//        broadcastReceiver = new RegisterBroadcast();
//        intentFilter = new IntentFilter("android.intent.action.MAIN");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment,container,false);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText("文件走丢了，请稍后");

        View hotBook = inflater.inflate(R.layout.hotbook,container,false);
        View bookEntries = inflater.inflate(R.layout.bookentries,container,false);
        View comVoice = inflater.inflate(R.layout.commpanyvoice,container,false);
        View aboutUs = inflater.inflate(R.layout.aboutus,container,false);

        if (mPage == 1){
            isThree = false;
            initHotBook(hotBook);
            return hotBook;
        }else if (mPage == 2){
            isThree = true;
            initBookEntries(bookEntries);
            return bookEntries;
        }else if (mPage == 3){
            userBean = (UserBean) getArguments().get("user");
            isThree = true;
            initComVoice(comVoice);
            return comVoice;
        }else if (mPage == 4){
            isThree = true;
            if (userBean == null){
                userBean = (UserBean) getArguments().get("user");
            }
//            contextnow.registerReceiver(broadcastReceiver,intentFilter);
            initAboutUs(aboutUs);
            return aboutUs;
        }
        return view;
    }

    private void initAboutUs(View aboutUs) {
       TextView  aboutUs_showWeatherAndLocation = (TextView) aboutUs.findViewById(R.id.aboutUs_showWeatherAndLocation);
       TextView aboutUs_showWeatherAndLocationDetail = (TextView) aboutUs.findViewById(R.id.aboutUs_showWeatherAndLocationDetail);
        Button aboutUs_login_userInfo = (Button) aboutUs.findViewById(R.id.aboutUs_login_userInfo);
        TextView aboutUs_userInfo_name = (TextView) aboutUs.findViewById(R.id.aboutUs_userInfo_name);
        aboutUs_login_userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contextnow,RegisterAndLoginActivity.class);
                startActivity(intent);
            }
        });

        if (userBean!=null){
            aboutUs_login_userInfo.setVisibility(View.GONE);
            aboutUs_userInfo_name.setVisibility(View.VISIBLE);
            aboutUs_userInfo_name.setText(userBean.getUser_name());
            System.out.println("返回对象："+userBean.toString());
        }else {
            System.out.println("没有返回对象：");
        }
    }

    private void initComVoice(View comVoice) {
        final ListView companyVoice_listView = (ListView) comVoice.findViewById(R.id.companyVoice_listView);
        final FloatingActionButton companyVoice_floatButton = (FloatingActionButton) comVoice.findViewById(R.id.companyVoice_floatButton);
        final TextView companyVoice_welcome = (TextView) comVoice.findViewById(R.id.companyVoice_welcome_img);
        ImageView companyVoice_errorPage = (ImageView) comVoice.findViewById(R.id.companyVoice_errorPage);
        final List<RequireMentBean> list = new ArrayList();
        companyVoice_floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userBean == null){
                    new AlertDialog.Builder(contextnow).setTitle("撰写需求需要登录提示").setMessage("您还没有进行登录，确认登录以撰写需求？").setCancelable(false).setPositiveButton("登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(contextnow,RegisterAndLoginActivity.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("点错了,不想",null).create().show();
                }else {
                    new AlertDialog.Builder(contextnow).setTitle("撰写需求提示").setMessage("确认撰写需求？").setCancelable(false).setPositiveButton("是的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(contextnow,CreateRequirementActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user",userBean);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }).setNegativeButton("点错了,不想",null).create().show();
                }
            }
        });
        final Handler voiceHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                companyVoice_welcome.setVisibility(View.GONE);
                companyVoice_listView.setVisibility(View.VISIBLE);
                companyVoice_floatButton.setVisibility(View.VISIBLE);
                companyVoice_listView.setAdapter(new CompanyVoiceAdapter(contextnow,list));
                companyVoice_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        RequireMentBean requirement = new RequireMentBean();
                        requirement = list.get(position);
                        //listview中回复详情列表
                        LinearLayout layout = (LinearLayout) LayoutInflater.from(contextnow).inflate(R.layout.requirement_detail_listview_detail,null);
                        //需求对话框整体架构
                        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(contextnow).inflate(R.layout.requirement_details,null);

                        TextView textRequire = (TextView) linearLayout.findViewById(R.id.requirement_detail_require);
                        final LinearLayout editTextCallbackLayout = (LinearLayout) linearLayout.findViewById(R.id.companyVoice_callback_linear);
                        final EditText editTextCallback = (EditText) linearLayout.findViewById(R.id.companyVoice_callback);
                        final TextView companyVoice_not_modify = (TextView) linearLayout.findViewById(R.id.companyVoice_not_modify);
                        final TextView requirement_detail_waiting = (TextView) linearLayout.findViewById(R.id.requirement_detail_waiting);
                        final ListView requirement_detail_list = (ListView) linearLayout.findViewById(R.id.requirement_detail_list);
                        textRequire.append(requirement.getRequireReply());
                        if (requirement.getRequireAmount()!=0){
                            requirement_detail_waiting.setText("需求讨论正在加载中，请稍等...");

                            final RequireMentBean finalRequirement1 = requirement;
                            final Handler requireHandler = new Handler(){
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    List<RequireMentBean> requireMentBeanList = (List<RequireMentBean>) msg.obj;
                                    requirement_detail_waiting.setVisibility(View.GONE);
                                    //对内容显示的判断逻辑，当内容长度>0时，显示requirement_detail_list
                                    if(requireMentBeanList.size()>0){
                                        requirement_detail_list.setVisibility(View.VISIBLE);
                                        requirement_detail_list.setAdapter(new RequirementAdapter(contextnow,requireMentBeanList));
                                        if (finalRequirement1.getIsResolve() == 0){
                                            editTextCallbackLayout.setVisibility(View.VISIBLE);
                                        }else {
                                            companyVoice_not_modify.setVisibility(View.VISIBLE);
                                            companyVoice_not_modify.setText("此需求已经关闭，不提供回复功能");
                                        }
                                        System.out.println(finalRequirement1.getIsResolve()+"=====");
                                        if (requireMentBeanList.size() <=5 ){
                                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) requirement_detail_list.getLayoutParams();
                                            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                            layoutParams.height = 240;
                                            requirement_detail_list.setLayoutParams(layoutParams);
                                        }
                                    }else {
                                        editTextCallbackLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            };
                            final RequireMentBean finalRequirement = requirement;
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    Connection connection = null;
                                    Statement statement = null;
                                    ResultSet resultSet = null;
                                    String url = "jdbc:mysql://"+urlFromString+":3306/usedbook?useUnicode=true&characterEncoding=UTF-8";
                                    String sql = "select * from user_requirereply where require_id_time = '" + finalRequirement.getRequireIdTime()+"'";
                                    System.out.println(sql);
                                    try {
                                        Class.forName("com.mysql.jdbc.Driver");
                                        connection = DriverManager.getConnection(url,"root","131365");
                                        statement = connection.createStatement();
                                        resultSet = statement.executeQuery(sql);
                                        List<RequireMentBean> requireMentBeanList = new ArrayList<RequireMentBean>();
                                        while (resultSet.next()){
                                            RequireMentBean requirementFromDataBase = new RequireMentBean();
                                            requirementFromDataBase.setRequireReply(resultSet.getString("require_reply_content"));
                                            requirementFromDataBase.setRequireTime(resultSet.getString("require_replyTime"));
                                            requireMentBeanList.add(requirementFromDataBase);
                                        }
                                        Message message = Message.obtain();
                                        message.obj = requireMentBeanList;
                                        requireHandler.sendMessage(message);
                                    } catch (ClassNotFoundException e) {
                                        Log.e("Driver:", "run: 驱动错误");
                                    } catch (SQLException e) {
                                        Log.e("Driver:", "run: 获取连接错误");
                                        e.printStackTrace();
                                    }

                                }
                            }.start();
                        }else {
                            //没有回复的时候，对布局进行的操作
                            requirement_detail_waiting.setText("暂未有讨论，如您对此有兴趣，请回复之");
                            requirement_detail_list.setVisibility(View.GONE);
                            editTextCallbackLayout.setVisibility(View.VISIBLE);
                        }
                        TextView requirement_detail_reply_time = (TextView) layout.findViewById(R.id.requirement_detail_reply_time);
                        TextView requirement_detail_reply_content = (TextView) layout.findViewById(R.id.requirement_detail_reply_content);

                        final String requireId = requirement.getRequireIdTime();
                        final int requirementAmount = requirement.getRequireAmount();
                        if (requirement.getIsResolve() == 1 ){
                            new AlertDialog.Builder(contextnow).setTitle(requireId+" 的需求详情").setView(linearLayout)
                                    .setNegativeButton("取消",null).setCancelable(false).create().show();
                        }else {
                            new AlertDialog.Builder(contextnow).setTitle(requireId+" 的需求详情").setView(linearLayout).setPositiveButton("提交回复", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String callbackMent = editTextCallback.getText().toString();
                                    if (callbackMent.isEmpty()){
                                        new AlertDialog.Builder(contextnow).setTitle("提交回复提示:").setMessage("您未输入任何内容").setCancelable(false).setNegativeButton("取消",null).create().show();
                                    }else {
                                        new AlertDialog.Builder(contextnow).setTitle("提交回复提示:").setMessage("确认提交回复信息？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                new SendRequirement(callbackMent,requireId,requirementAmount).execute();
                                            }
                                        }).setCancelable(false).setNegativeButton("取消",null).create().show();
                                    }
                                }
                            }).setNegativeButton("取消",null).setCancelable(false).create().show();
                        }
                    }
                });
            }
        };
        if (ACache.get(contextnow).getAsJSONArray("rquirement")!=null){
            JSONArray jsonArray = ACache.get(contextnow).getAsJSONArray("rquirement");
            final int currentRequirementLength = jsonArray.length();
            for (int i = 0; i < currentRequirementLength; i++) {
                try {
                    JSONObject resultSet = jsonArray.getJSONObject(i);
                    RequireMentBean requirement = new RequireMentBean();

                    requirement.setRequireIdTime(resultSet.getString("require_id_time"));
                    requirement.setRequireReply(resultSet.getString("require_content"));
                    requirement.setRequireTime(resultSet.getString("require_time"));
                    requirement.setRequireAmount(resultSet.getInt("require_amount"));
                    requirement.setRequireIdTimeConnect(resultSet.getString("require_id_time_connect"));
                    requirement.setIsResolve(resultSet.getInt("require_flag"));

                    list.add(requirement);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            voiceHandler.sendMessage(new Message());

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    Connection connection = null;
                    Statement statement = null;
                    ResultSet resultSet = null;
                    String url = "jdbc:mysql://"+urlFromString+":3306/usedbook?useUnicode=true&characterEncoding=UTF-8";
                    String sql = "select * from user_require order by require_amount DESC";
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = null;
                    List<RequireMentBean> newList = new ArrayList();
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        connection = DriverManager.getConnection(url,"root","131365");
                        statement = connection.createStatement();
                        resultSet = statement.executeQuery(sql);
                        while (resultSet.next()){
                            jsonObject = new JSONObject();
                            RequireMentBean requirement = new RequireMentBean();

                            requirement.setRequireIdTime(resultSet.getString("require_id_time"));
                            jsonObject.put("require_id_time",resultSet.getString("require_id_time"));

                            requirement.setRequireReply(resultSet.getString("require_content"));
                            jsonObject.put("require_content",resultSet.getString("require_content"));

                            requirement.setRequireTime(resultSet.getString("require_time"));
                            jsonObject.put("require_time",resultSet.getString("require_time"));

                            requirement.setRequireAmount(resultSet.getInt("require_amount"));
                            jsonObject.put("require_amount",resultSet.getInt("require_amount"));

                            requirement.setRequireIdTimeConnect(resultSet.getString("require_id_time_connect"));
                            jsonObject.put("require_id_time_connect",resultSet.getString("require_id_time_connect"));

                            requirement.setIsResolve(resultSet.getInt("require_flag"));
                            jsonObject.put("require_flag",resultSet.getInt("require_flag"));

                            jsonArray.put(jsonObject);
                            newList.add(requirement);
                        }
                    } catch (ClassNotFoundException e) {
                        Log.e("Driver:", "run: 驱动错误");
                    } catch (SQLException e) {
                        Log.e("Driver:", "run: 获取连接错误");
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (currentRequirementLength != newList.size()){
                        ACache aCache = ACache.get(contextnow);
                        aCache.put("rquirement",jsonArray);
                        System.out.println("需求有更新");
                    }else {
                        System.out.println("需求没有更新");
                    }
                }
            }.start();

        }else {
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    Connection connection = null;
                    Statement statement = null;
                    ResultSet resultSet = null;
                    String url = "jdbc:mysql://"+urlFromString+":3306/usedbook?useUnicode=true&characterEncoding=UTF-8";
                    String sql = "select * from user_require order by require_amount DESC";
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = null;
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        connection = DriverManager.getConnection(url,"root","131365");
                        statement = connection.createStatement();
                        resultSet = statement.executeQuery(sql);
                        while (resultSet.next()){
                            jsonObject = new JSONObject();
                            RequireMentBean requirement = new RequireMentBean();

                            requirement.setRequireIdTime(resultSet.getString("require_id_time"));
                            jsonObject.put("require_id_time",resultSet.getString("require_id_time"));

                            requirement.setRequireReply(resultSet.getString("require_content"));
                            jsonObject.put("require_content",resultSet.getString("require_content"));

                            requirement.setRequireTime(resultSet.getString("require_time"));
                            jsonObject.put("require_time",resultSet.getString("require_time"));

                            requirement.setRequireAmount(resultSet.getInt("require_amount"));
                            jsonObject.put("require_amount",resultSet.getInt("require_amount"));

                            requirement.setRequireIdTimeConnect(resultSet.getString("require_id_time_connect"));
                            jsonObject.put("require_id_time_connect",resultSet.getString("require_id_time_connect"));

                            requirement.setIsResolve(resultSet.getInt("require_flag"));
                            jsonObject.put("require_flag",resultSet.getInt("require_flag"));

                            jsonArray.put(jsonObject);
                            list.add(requirement);
                        }
                    } catch (ClassNotFoundException e) {
                        Log.e("Driver:", "run: 驱动错误");
                    } catch (SQLException e) {
                        Log.e("Driver:", "run: 获取连接错误");
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ACache aCache = ACache.get(contextnow);
                    aCache.put("rquirement",jsonArray);
                    voiceHandler.sendMessage(new Message());
                }
            }.start();
        }


    }

    private void initBookEntries(View bookEntries) {
        final ViewPager viewPager = (ViewPager) bookEntries.findViewById(R.id.bookEntry_viewPager);
        final Handler mHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                viewPager.setCurrentItem(currentItem);
            }
        };
        imageResId = new int[]{R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e};
        titles = new String[imageResId.length];
        titles[0] = "温馨的读书的港湾";
        titles[1] = "图书馆，心灵的栖息地";
        titles[2] = "让每一本二手书回家";
        titles[3] = "在知识的海洋中陶醉自己";
        titles[4] = "生活的艰辛不是我们停止读书的借口";
        imageViews = new ArrayList<ImageView>();
        for (int i = 0; i < imageResId.length; i++) {
            ImageView imageView = new ImageView(contextnow);
            imageView.setImageResource(imageResId[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViews.add(imageView);
        }
        dots = new ArrayList<View>();
        dots.add(bookEntries.findViewById(R.id.v_dot0));
        dots.add(bookEntries.findViewById(R.id.v_dot1));
        dots.add(bookEntries.findViewById(R.id.v_dot2));
        dots.add(bookEntries.findViewById(R.id.v_dot3));
        dots.add(bookEntries.findViewById(R.id.v_dot4));
        tv_title = (TextView) bookEntries.findViewById(R.id.tv_title);
        tv_title.setText(titles[0]);
        viewPager.setAdapter(new PageFragmentTwoViewPagerAdapter());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int oldPosition = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                tv_title.setText(titles[position]);
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                oldPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (viewPager){
                    currentItem = (currentItem + 1) % imageViews.size();
                    mHandle.obtainMessage().sendToTarget();
                }
            }
        }, 1, 2, TimeUnit.SECONDS);

        initBookEntriesBottomContent(bookEntries);
    }
    //对滚动图片的下面进行操作
    private void initBookEntriesBottomContent(View bookEntries) {
        ImageView bookEntries_img_computer,bookEntries_img_math,bookEntries_img_machine;
        TextView bookEntries_text_computer,bookEntries_text_math,bookEntries_text_machine;

        bookEntries_img_computer = (ImageView) bookEntries.findViewById(R.id.bookEntries_img_computer);
        bookEntries_img_math = (ImageView) bookEntries.findViewById(R.id.bookEntries_img_math);
        bookEntries_text_computer = (TextView) bookEntries.findViewById(R.id.bookEntries_text_computer);
        bookEntries_text_math = (TextView) bookEntries.findViewById(R.id.bookEntries_text_math);
        bookEntries_img_machine = (ImageView) bookEntries.findViewById(R.id.bookEntries_img_machine);
        bookEntries_text_machine = (TextView) bookEntries.findViewById(R.id.bookEntries_text_machine);

        bookEntries_img_computer.setOnClickListener(new BookEntriesListener(bookEntries_text_computer.getText().toString()));
        bookEntries_img_math.setOnClickListener(new BookEntriesListener(bookEntries_text_math.getText().toString()));
        bookEntries_img_machine.setOnClickListener(new BookEntriesListener(bookEntries_text_machine.getText().toString()));
    }

    private void initHotBook(final View hotBook) {
        //final List<bookInfo> list = new GetDataForAll(contextNow).getDataFromDatabase();
        //System.out.println("这里出现了错误---->>>++++>>>>"+list);
         if (true){//isFirstFlag
             final Handler handler = new Handler(){
                 @Override
                 public void handleMessage(Message msg) {
                     super.handleMessage(msg);
                     ListView listView = (ListView) hotBook.findViewById(R.id.hotBook_list);
                     TextView hotBook_waiting = (TextView) hotBook.findViewById(R.id.hotBook_waiting);
                     TextView hotBookErrorPage = (TextView) hotBook.findViewById(R.id.hotBook_errorPage);
                     if (dialog!=null && dialog.isShowing()){
                         dialog.dismiss();
                     }
                     switch (msg.what){
                         case 0:
                             Toast.makeText(contextnow,"网络错误，请检查后重新再试",Toast.LENGTH_LONG).show();
                             listView.setVisibility(View.GONE);
                             hotBook_waiting.setVisibility(View.GONE);
                             hotBookErrorPage.setVisibility(View.VISIBLE);
                             isFirstFlag = false;
                             return;
                         case 1:
                             bookInfoList = (List<bookInfo>) msg.obj;
                             System.out.println(bookInfoList);
                             hotBook_waiting.setVisibility(View.GONE);
                             isFirstFlag = false;
                             break;
                     }

                     listView.setAdapter(new ListHotBookAdapter(contextnow,bookInfoList));
                     listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                         @Override
                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                             Bundle bundle = new Bundle();
                             bundle.putSerializable("bookInfo",bookInfoList.get(position));
                             Intent intent = new Intent(contextnow,DetailListviewBookInfoActivity.class);
                             intent.putExtras(bundle);
                             startActivity(intent);

                         }
                     });
                 }
             };
             if (!isThree){
                 dialog = new ProgressDialog(contextnow);
                 dialog.setIcon(R.drawable.hot_dialog_title);
                 dialog.setTitle("加载热门图书信息: ");
                 dialog.setMessage("正在加载中,请稍后...");
                // dialog.show();
                 dialog.setCancelable(false);
                 if (ACache.get(contextnow).getAsJSONArray("object")!=null){
                     Message msg = Message.obtain();
                     bookInfo bookData;
                     List<bookInfo> bookInfoList_bookData = new ArrayList<bookInfo>();
                     JSONArray jsonArray = ACache.get(contextnow).getAsJSONArray("object");
                     final int currentLength = jsonArray.length();
                     for (int i = 0; i < currentLength; i++) {
                         try {
                            JSONObject result = jsonArray.getJSONObject(i);
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
                             bookData.setBook_amount(result.getInt("book_amount")+"");
                             bookData.setBookImgUrl(result.getString("book_img_url"));

                             bookInfoList_bookData.add(bookData);
                         } catch (JSONException e) {
                             e.printStackTrace();
                         }
                     }
                     msg.what = 1;
                     msg.obj = bookInfoList_bookData;
                     System.out.println("缓存来的数据");
                     handler.sendMessage(msg);



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
                                 JSONArray jsonArray = new JSONArray();
                                 JSONObject jsonObject = null;
                                 while (result.next()){

                                     bookData = new bookInfo();
                                     jsonObject = new JSONObject();

                                     bookData.setSeller_sellbook_isbn(result.getString("seller_sellbook_isbn"));
                                     jsonObject.put("seller_sellbook_isbn",result.getString("seller_sellbook_isbn"));

                                     bookData.setBook_name(result.getString("book_name"));
                                     jsonObject.put("book_name",result.getString("book_name"));

                                     bookData.setBook_mark(result.getString("book_mark"));
                                     jsonObject.put("book_mark",result.getString("book_mark"));

                                     bookData.setBook_version(result.getString("book_version"));
                                     jsonObject.put("book_version",result.getString("book_version"));

                                     bookData.setBook_author(result.getString("book_author"));
                                     jsonObject.put("book_author",result.getString("book_author"));

                                     bookData.setBook_price(result.getInt("book_price")+"");
                                     jsonObject.put("book_price",result.getString("book_price")+"");

                                     bookData.setBook_sell_declare(result.getString("book_sell_declare"));
                                     jsonObject.put("book_sell_declare",result.getString("book_sell_declare"));

                                     bookData.setBook_time(result.getString("book_time"));
                                     jsonObject.put("book_time",result.getString("book_time"));

                                     bookData.setSeller_register_name(result.getString("seller_register_name"));
                                     jsonObject.put("seller_register_name",result.getString("seller_register_name"));

                                     bookData.setBook_amount(result.getInt("book_amount")+"");
                                     jsonObject.put("book_amount",result.getString("book_amount")+"");

                                     bookData.setBookImgUrl(result.getString("book_img_url"));
                                     jsonObject.put("book_img_url",result.getString("book_img_url"));

                                     bookInfoList_bookData.add(bookData);
                                     jsonArray.put(jsonObject);
                                 }

                                 if (currentLength != jsonArray.length()){
                                     ACache aCache = ACache.get(contextnow);
                                     aCache.put("object",jsonArray);
                                     System.out.println("增加了书籍");
                                 }else {
                                     System.out.println("没有增加书籍");
                                 }

                             } catch (ClassNotFoundException e) {
                                 e.printStackTrace();
                             } catch (SQLException e) {
                                 e.printStackTrace();
                                 System.out.println("sql 语句错误");
                             } catch (JSONException e) {
                                 e.printStackTrace();
                             } finally {
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








                 }else {
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
                             Message msg = Message.obtain();
                             try {
                                 Class.forName("com.mysql.jdbc.Driver");
                                 connection = DriverManager.getConnection(url,"root","131365");
                                 statement = connection.createStatement();
                                 result = statement.executeQuery(sql);
                                 int maxBook = 0;
                                 JSONArray jsonArray = new JSONArray();
                                 JSONObject jsonObject = null;
                                 while (result.next()){
                                     maxBook ++;
                                     bookData = new bookInfo();
                                     jsonObject = new JSONObject();

                                     bookData.setSeller_sellbook_isbn(result.getString("seller_sellbook_isbn"));
                                     jsonObject.put("seller_sellbook_isbn",result.getString("seller_sellbook_isbn"));

                                     bookData.setBook_name(result.getString("book_name"));
                                     jsonObject.put("book_name",result.getString("book_name"));

                                     bookData.setBook_mark(result.getString("book_mark"));
                                     jsonObject.put("book_mark",result.getString("book_mark"));

                                     bookData.setBook_version(result.getString("book_version"));
                                     jsonObject.put("book_version",result.getString("book_version"));

                                     bookData.setBook_author(result.getString("book_author"));
                                     jsonObject.put("book_author",result.getString("book_author"));

                                     bookData.setBook_price(result.getInt("book_price")+"");
                                     jsonObject.put("book_price",result.getString("book_price")+"");

                                     bookData.setBook_sell_declare(result.getString("book_sell_declare"));
                                     jsonObject.put("book_sell_declare",result.getString("book_sell_declare"));

                                     bookData.setBook_time(result.getString("book_time"));
                                     jsonObject.put("book_time",result.getString("book_time"));

                                     bookData.setSeller_register_name(result.getString("seller_register_name"));
                                     jsonObject.put("seller_register_name",result.getString("seller_register_name"));

                                     bookData.setBook_amount(result.getInt("book_amount")+"");
                                     jsonObject.put("book_amount",result.getString("book_amount")+"");

                                     bookData.setBookImgUrl(result.getString("book_img_url"));
                                     jsonObject.put("book_img_url",result.getString("book_img_url"));

                                     bookInfoList_bookData.add(bookData);
                                     jsonArray.put(jsonObject);
                                     if (maxBook > 20){
                                         break;
                                     }
                                 }
                                 msg.what = 1;
                                 msg.obj = bookInfoList_bookData;
                                 ACache aCache = ACache.get(contextnow);
                                 aCache.put("object",jsonArray);
                                 handler.sendMessage(msg);
                             } catch (ClassNotFoundException e) {
                                 e.printStackTrace();
                             } catch (SQLException e) {
                                 e.printStackTrace();
                                 msg.what = 0;
                                 handler.sendMessage(msg);
                                 System.out.println("sql 语句错误");
                             } catch (JSONException e) {
                                 e.printStackTrace();
                             } finally {
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

         }
    }




    private class PageFragmentTwoViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageResId.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager)container).addView(imageViews.get(position));
            return imageViews.get(position);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager)container).removeView((View) object);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    private class BookEntriesListener implements View.OnClickListener {
        private String bookEntryMark;
        private Intent intent;
        public BookEntriesListener(String bookEntryMark) {
            this.bookEntryMark = bookEntryMark;
            intent = new Intent(contextnow,BookEntriesActivity.class);
            intent.putExtra("bookMark",bookEntryMark);
        }
        @Override
        public void onClick(View v) {
            startActivity(intent);
        }
    }

    class SendRequirement extends AsyncTask{
        private String content;
        boolean isSendTrue = false;
        String id;
        int requirementAmount;

        public SendRequirement(String content, String requireId, int requirementAmount) {
            this.content = content;
            this.id = requireId;
            this.requirementAmount = requirementAmount;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Connection connection = null;
            Statement statement = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String currentDate = simpleDateFormat.format(date);
            //此处更新回复量
            String sql = "insert into user_requirereply values('" + id + "','" + content + "','" + currentDate + "')";
            requirementAmount = requirementAmount + 1;
            String addAmountSQL = "update user_require set require_amount = " + requirementAmount +" where require_id_time = '" + id + "'";
            System.out.println(sql);
            System.out.println(addAmountSQL);
            String url = "jdbc:mysql://"+urlFromString+":3306/usedbook?useUnicode=true&characterEncoding=UTF-8";
            try {
                Class.forName(jdbcUrl);
                connection = DriverManager.getConnection(url,"root","131365");
                statement = connection.createStatement();
                isSendTrue = statement.execute(sql);
                statement.execute(addAmountSQL);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                isSendTrue = true;
            } catch (SQLException e) {
                e.printStackTrace();
                isSendTrue = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (!isSendTrue){
                Toast.makeText(contextnow,"回复成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(contextnow,"回复失败，请检查网络后再次尝试",Toast.LENGTH_SHORT).show();
            }
        }
    }

//     class RegisterBroadcast extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Bundle bundle = intent.getBundleExtra("user");
//            userBean = (UserBean) bundle.get("user");
//        }
//    }

}
