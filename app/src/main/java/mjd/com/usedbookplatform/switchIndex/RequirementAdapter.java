package mjd.com.usedbookplatform.switchIndex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mjd.com.usedbookplatform.R;
import mjd.com.usedbookplatform.switchIndex.bookBean.RequireMentBean;

/**
 * Created by IT之旅 on 2016-10-8.
 */
public class RequirementAdapter extends BaseAdapter {
    private Context context;
    private List<RequireMentBean> list;
    private LayoutInflater layoutInflater;
    private RequireMentBean requireMentBean;
    private int i = 0;

    public RequirementAdapter(Context context, List<RequireMentBean> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.requirement_detail_listview_detail,null);
        requireMentBean = list.get(position);

        ((TextView)linearLayout.findViewById(R.id.requirement_detail_reply_time)).setText(requireMentBean.getRequireTime());
        ((TextView)linearLayout.findViewById(R.id.requirement_detail_reply_content)).setText(requireMentBean.getRequireReply());
        i = position + 1;
        ((TextView)linearLayout.findViewById(R.id.requirement_detail_reply_order)).setText("" + i);

        return linearLayout;
    }
}
