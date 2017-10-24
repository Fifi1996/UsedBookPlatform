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
 * Created by IT之旅 on 2016-10-7.
 */
public class CompanyVoiceAdapter extends BaseAdapter {
    private Context context;
    private List<RequireMentBean> list;
    private LayoutInflater layoutInflater;
    public CompanyVoiceAdapter(Context context, List list) {
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
        LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.company_voice_detail,null);
        TextView voice_id = (TextView) layout.findViewById(R.id.voice_id);
        TextView voice_time = (TextView) layout.findViewById(R.id.voice_time);
        TextView voice_needs = (TextView) layout.findViewById(R.id.voice_needs);
        TextView voice_reply = (TextView) layout.findViewById(R.id.voice_reply);
        TextView voice_needs_connect = (TextView) layout.findViewById(R.id.voice_needs_connect);
        TextView voice_isResolve = (TextView) layout.findViewById(R.id.voice_isResolve);

        RequireMentBean requireMentBean = list.get(position);

        if (requireMentBean.getIsResolve() == 1){
            voice_isResolve.setVisibility(View.VISIBLE);
        }
        voice_id.append(requireMentBean.getRequireIdTime());
        voice_time.append(requireMentBean.getRequireTime());
        voice_needs.append(requireMentBean.getRequireReply());
        voice_reply.append(requireMentBean.getRequireAmount()+"");
        voice_needs_connect.append(requireMentBean.getRequireIdTimeConnect());

        return layout;
    }
}
