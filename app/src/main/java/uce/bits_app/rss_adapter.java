package uce.bits_app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ubbo on 25.08.2016.
 * Wurde anhand eines Online-Tutorials erstellt,
 * es wurden einige Anpassungen vorgenommen damit das ganze in gewünschter Weise funktioniert
 */
public class rss_adapter extends BaseAdapter {

    private List<rss_Item> items;
    private Context context;

    public rss_adapter(Context context, List<rss_Item> items) {
        this.items = items;
        this.context = context;
    }
    //Wieviel Items gibt es
    @Override
    public int getCount() {
        return items.size();
    }
    //Items werde nabgeholt
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }
    //item ID wird abgefragt
    @Override
    public long getItemId(int id) {
        return id;
    }
    //Das Layout des rss_items wird abgefragt,
    // es wird ein ViewHolder erstellt,
    // der die Rss-item-titel darstellt
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.rss_item, null);
            holder = new ViewHolder();
            holder.itemTitle = (TextView) convertView.findViewById(R.id.itemTitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.itemTitle.setText(items.get(position).getTitle());
        return convertView;
    }

    static class ViewHolder {
        TextView itemTitle;
    }
}
