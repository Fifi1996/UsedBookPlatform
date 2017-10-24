package mjd.com.usedbookplatform.switchIndex;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mjd.com.usedbookplatform.switchIndex.bookBean.UserBean;

/**
 * Created by IT之旅 on 2016-10-1.
 */
public class PageFragmentAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"热门书籍","书籍条目","同伴需求","用户相关"};
    private Context context;
    private UserBean userBean;
    public PageFragmentAdapter(FragmentManager fm, Context context, UserBean userBean) {
        super(fm);
        this.context = context;
        this.userBean = userBean;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position+1,context,userBean);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
