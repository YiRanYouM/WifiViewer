package org.wuwz.wifiviewer;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.wuwz.wifiviewer.adapter.WifiInfoAdapter;
import org.wuwz.wifiviewer.model.WifiInfo;
import org.wuwz.wifiviewer.utils.ShellUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity {

    private ListView _lvWifiInfo = null;
    private LinearLayout _layoutFullMsg = null;
    private TextView _tvFullMsg = null;
    private boolean _isReadCompile = false;
    private List<WifiInfo> _dataList = new ArrayList<>();
    private WifiInfoAdapter _wifiInfoAdapter;
    private ClipboardManager _clipboardManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        _clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    private void initViews() {
        _lvWifiInfo = (ListView) findViewById(R.id.listview_wifi_info);

        _wifiInfoAdapter = new WifiInfoAdapter(this, _dataList);
        _lvWifiInfo.setAdapter(_wifiInfoAdapter);

        /*_lvWifiInfo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(MainActivity.this, "show details dialog..", Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/

        _lvWifiInfo.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                // show details menu dialog..
                menu.setHeaderTitle("选择操作");
                menu.add(v.getId(), 0, 0, "复制WIFI密码");
                menu.add(v.getId(), 1, 1, "复制WIFI名字+密码");
            }
        });

        _tvFullMsg = (TextView) findViewById(R.id.tv_full_msg);
        _layoutFullMsg = (LinearLayout) findViewById(R.id.layout_full_msg);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        WifiInfo data = _dataList.get(info.position);
        if (data.getWifiPwd().equals(getString(R.string.wifi_no_pwd))) {
            Toast.makeText(MainActivity.this, "这个WIFI没有密码,换一个试试吧！", Toast.LENGTH_LONG).show();
        } else {

            String content = "";
            switch (item.getItemId()) {
                case 0:
                    content = data.getWifiPwd();
                    break;
                case 1:
                    content = MessageFormat.format("WIFI名字：{0} 密码：{1}", data.getWifiName(), data.getWifiPwd());
                    break;
            }

            _clipboardManager.setPrimaryClip(ClipData.newPlainText(null, content));
            Toast.makeText(MainActivity.this, "操作成功！", Toast.LENGTH_LONG).show();
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!_isReadCompile) {

            readWifiConfigToListView();
        }
    }

    private void readWifiConfigToListView() {
        String[] commands = new String[]{"cat /data/misc/wifi/wpa_supplicant.conf\n", "exit\n"};

        ShellUtils.CommandResult cr = ShellUtils.execCommand(commands, true, true);

        if (cr.result != 0) {
            _tvFullMsg.setText(getString(R.string.analysis_error_msg));
            _layoutFullMsg.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.analysis_error_msg), Toast.LENGTH_LONG).show();
            return;
        }

        String wifiConfigInfo = String.valueOf(cr.successMsg);
        if (TextUtils.isEmpty(wifiConfigInfo)) {
            // no root.
            _layoutFullMsg.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, getString(R.string.no_root_msg), Toast.LENGTH_LONG).show();
            return;
        }

        // analysis result.
        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Matcher matcher = network.matcher(wifiConfigInfo);
        WifiInfo model;
        while (matcher.find()) {
            String networkBlock = matcher.group();
            Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\"");
            Matcher ssidMatcher = ssid.matcher(networkBlock);
            if (ssidMatcher.find()) {
                model = new WifiInfo();
                model.setWifiName(ssidMatcher.group(1));
                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");
                Matcher pskMatcher = psk.matcher(networkBlock);

                model.setWifiPwd(pskMatcher.find() ? pskMatcher.group(1) : getString(R.string.wifi_no_pwd));
                _dataList.add(model);
            }
        }
        Collections.reverse(_dataList);
        _wifiInfoAdapter.notifyDataSetChanged();

        Toast.makeText(MainActivity.this, getString(R.string.welcome_msg), Toast.LENGTH_SHORT).show();
        _isReadCompile = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Uri uri = Uri.parse("http://github.com/wuwz");
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            return true;
        } else if(id == R.id.action_exit) {
            finish();
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    private long _exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - _exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
                _exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
