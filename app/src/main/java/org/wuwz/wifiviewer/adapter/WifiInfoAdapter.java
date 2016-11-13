package org.wuwz.wifiviewer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.wuwz.wifiviewer.R;
import org.wuwz.wifiviewer.model.WifiInfo;

import java.text.MessageFormat;
import java.util.List;

/**
 * @Author: wuwz.
 * @EMail: wuwz@live.com.
 * @Date: 2016/11/14.
 * @Url: http://www.github.com/wuwz
 */
public class WifiInfoAdapter extends BaseAdapter {

    private Context _context = null;
    private List<WifiInfo> _dataList = null;
    private LayoutInflater _layoutInflater = null;

    public WifiInfoAdapter(Context context, List<WifiInfo> dataList) {
        this._context = context;
        this._dataList = dataList;
        this._layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return _dataList != null ? _dataList.size() : 0;
    }

    @Override
    public WifiInfo getItem(int position) {
        return _dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = _layoutInflater.inflate(R.layout.wifi_info_item, null);
            viewHolder.tvWifiName = (TextView) convertView.findViewById(R.id.tv_wifi_name);
            viewHolder.tvWifiPwd = (TextView) convertView.findViewById(R.id.tv_wifi_pwd);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // banding data.
        WifiInfo data = _dataList.get(position);

        viewHolder.tvWifiName.setText(MessageFormat.format(_context.getString(R.string.wifi_name), data.getWifiName()));
        viewHolder.tvWifiPwd.setText(MessageFormat.format(_context.getString(R.string.wifi_pwd), data.getWifiPwd()));

        return convertView;
    }

    public class ViewHolder {
        public TextView tvWifiName, tvWifiPwd;
    }
}
