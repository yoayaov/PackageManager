package com.example.myapplication;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvEmpty;
    private ListView listView;
    private Button btnScan;
    private EditText editInput;
    private List<SimpleAppInfo> filterAppList = new ArrayList<>();
    private BaseAdapter appListAdapter;
    private Switch switchType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEmpty = findViewById(R.id.tv_empty);
        listView = findViewById(R.id.list_view);
        btnScan = findViewById(R.id.btn_scan);
        editInput = findViewById(R.id.edit_input);
        switchType = findViewById(R.id.switch_type);
        switchType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                appListAdapter.notifyDataSetChanged();
            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止重复点击
                btnScan.setEnabled(false);
                listView.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("正在扫描");
                //开子线程做耗时的工作
                new Thread() {
                    @Override
                    public void run() {
                        final List<SimpleAppInfo> appsInfo = getAppsInfo();
                        //切换到主线程 更新数据
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String filterStr = editInput.getText().toString().trim();
                                if (TextUtils.isEmpty(filterStr)) {
                                    filterAppList = appsInfo;
                                } else {//过滤
                                    filterAppList.clear();
                                    for (SimpleAppInfo appInfo : appsInfo) {
                                        if (appInfo.getAppPackage().startsWith(filterStr)) {
                                            filterAppList.add(appInfo);
                                        }
                                    }
                                }
                                if (filterAppList.size() == 0) {
                                    tvEmpty.setVisibility(View.VISIBLE);
                                    listView.setVisibility(View.GONE);
                                    tvEmpty.setText("没有任何数据");
                                } else {
                                    tvEmpty.setVisibility(View.GONE);
                                    listView.setVisibility(View.VISIBLE);
                                    appListAdapter.notifyDataSetChanged();
                                }
                                //更新完成后  开放 按钮
                                btnScan.setEnabled(true);
                            }
                        });
                    }
                }.start();
            }
        });

        appListAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return filterAppList.size();
            }

            @Override
            public Object getItem(int position) {
                return filterAppList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = View.inflate(MainActivity.this, R.layout.item, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                SimpleAppInfo appInfo = filterAppList.get(position);
                holder.tvAppPackage.setText(appInfo.getAppPackage());
                if (switchType.isChecked()) {
                    //这里是直接展示appInfo提供的 应用名\包名\Icon
                    holder.tvAppName.setText(appInfo.getAppName());
                    holder.ivAppImage.setImageDrawable(appInfo.getAppIcon());
                } else {
                    //使用 指定资源数据
                    holder.tvAppName.setText(appInfo.getValuesAppName());
                    holder.ivAppImage.setImageDrawable(appInfo.getDrawablePreiewImg());
                }
                return convertView;
            }
        };
        listView.setAdapter(appListAdapter);
    }

    public static class ViewHolder {
        TextView tvAppName;
        TextView tvAppPackage;
        ImageView ivAppImage;

        public ViewHolder(View view) {
            tvAppName = view.findViewById(R.id.tv_app_name);
            tvAppPackage = view.findViewById(R.id.tv_app_package);
            ivAppImage = view.findViewById(R.id.iv_app_icon);
        }
    }

    public List<SimpleAppInfo> getAppsInfo() {
        List<SimpleAppInfo> list = new ArrayList<>();
        PackageManager pm = getPackageManager();
        if (pm == null) return list;
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo pi : installedPackages) {
            SimpleAppInfo ai = getBean(pm, pi);
            if (ai == null) continue;
            list.add(ai);
        }
        return list;
    }

    private SimpleAppInfo getBean(final PackageManager pm, final PackageInfo pi) {
        if (pi == null) return null;
        ApplicationInfo ai = pi.applicationInfo;
        String packageName = pi.packageName;
        String name = ai.loadLabel(pm).toString();
        Drawable icon = ai.loadIcon(pm);
        String packagePath = ai.sourceDir;
        String versionName = pi.versionName;
        int versionCode = pi.versionCode;
        boolean isSystem = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != 0;
//        return new SimpleAppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem);
        SimpleAppInfo simpleAppInfo = new SimpleAppInfo(name, packageName, icon, isSystem, packagePath, versionName, versionCode);

        try {
            Resources resources = pm.getResourcesForApplication(packageName);
            int appNameId = resources.getIdentifier("app_name", "string", packageName);
            if (appNameId > 0) {
                String appName = resources.getString(appNameId);
                simpleAppInfo.setValuesAppName(appName);
            }
            int previewImgId = resources.getIdentifier("preview_img", "drawable", packageName);
//                                    int previewImgId = resources.getIdentifier("ic_launcher", "drawable", packageName);
            if (previewImgId > 0) {
                Drawable drawable = resources.getDrawable(previewImgId);
                simpleAppInfo.setDrawablePreiewImg(drawable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return simpleAppInfo;
    }
}