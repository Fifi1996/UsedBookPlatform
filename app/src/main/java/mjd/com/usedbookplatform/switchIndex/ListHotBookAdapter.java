package mjd.com.usedbookplatform.switchIndex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import mjd.com.usedbookplatform.R;
import mjd.com.usedbookplatform.switchIndex.bookBean.bookInfo;
import mjd.com.usedbookplatform.switchIndex.utils.ImageLoaderPictuer;

/**
 * Created by IT之旅 on 2016-10-1.
 */
public class ListHotBookAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<bookInfo> bookList;
    private Context context;

    public ListHotBookAdapter(Context context, List<bookInfo> bookList) {
        this.context = context;
        this.bookList = bookList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.hotbook_list,null);
        ImageView imageView = (ImageView) linearLayout.findViewById(R.id.book_list_img);
        TextView textView_theme = (TextView) linearLayout.findViewById(R.id.book_list_text_theme);
        TextView textView_declare = (TextView) linearLayout.findViewById(R.id.book_list_text_declare);
        TextView textView_time = (TextView) linearLayout.findViewById(R.id.book_list_text_time);
        TextView book_list_mark = (TextView) linearLayout.findViewById(R.id.book_list_mark);
        bookInfo book = bookList.get(position);

       // imageView.setImageResource(R.drawable.test);
        ImageLoader.getInstance().displayImage(book.getBookImgUrl(),imageView,new ImageLoaderPictuer(context).getOptions(),new SimpleImageLoadingListener());
        textView_theme.append(book.getBook_name());
        textView_declare.append(book.getBook_sell_declare());
        textView_time.append(book.getBook_time());
        book_list_mark.append(book.getBook_mark());
        System.out.println(linearLayout);
        return linearLayout;
    }
}
