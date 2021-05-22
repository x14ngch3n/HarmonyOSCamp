package com.ohos.demo.uitest.slice;

import com.ohos.demo.uitest.PreferencesHelper;
import com.ohos.demo.uitest.ResourceTable;
import com.ohos.demo.uitest.adapter.SampleItem;
import com.ohos.demo.uitest.adapter.SampleItemProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.BaseItemProvider;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.ListContainer;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.IDialog;
import ohos.app.Context;
import ohos.data.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainAbilitySlice extends AbilitySlice {
    private ListContainer mlistContainer;
    private Button mBtn;
    private PreferencesChangeCounter mCounter;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_page_listcontainer);
        initListContainer();
        addObserver();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    protected void onStop() {
        removeObserver();
        super.onStop();
    }

    private void initListContainer() {
        mlistContainer = (ListContainer) findComponentById(ResourceTable.Id_list_container);
        List<SampleItem> list = getData();
        SampleItemProvider sampleItemProvider = new SampleItemProvider(list, this);
        mlistContainer.setItemProvider(sampleItemProvider);
        mlistContainer.setItemClickedListener(new ListContainer.ItemClickedListener() {
            @Override
            public void onItemClicked(ListContainer listContainer, Component component, int i, long l) {
                SampleItem item = (SampleItem) listContainer.getItemProvider().getItem(i);
                startAbility(item.getmTitle());
            }
        });
        mlistContainer.setItemLongClickedListener(new ListContainer.ItemLongClickedListener() {
            @Override
            public boolean onItemLongClicked(ListContainer listContainer, Component component, int i, long l) {
                SampleItem item = (SampleItem) listContainer.getItemProvider().getItem(i);
                showDeleteDialog(item.getmTitle());
                return true;
            }
        });
        mBtn = (Button) findComponentById(ResourceTable.Id_new_record);
        mBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                startAbility("");
            }
        });
    }

    private ArrayList<SampleItem> getData() {
        Preferences preferences = PreferencesHelper.getInstance().getPreference(getContext());
        Map<String, String> records = (Map<String, String>) preferences.getAll();
        ArrayList<SampleItem> list = new ArrayList<>();
        for (String key : records.keySet()) {
            list.add(new SampleItem(key, records.getOrDefault(key, "")));
        }
        return list;
    }

    private void addObserver() {
        Preferences preferences = PreferencesHelper.getInstance().getPreference(getContext());
        mCounter = new PreferencesChangeCounter();
        preferences.registerObserver(mCounter);
    }

    private void removeObserver() {
        Preferences preferences = PreferencesHelper.getInstance().getPreference(getContext());
        preferences.unregisterObserver(mCounter);
    }

    private void startAbility(String key) {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName(getBundleName())
                .withAbilityName("com.ohos.demo.uitest.EditAbility")
                .build();
        intent.setOperation(operation);
        intent.setParam("key", key);
        intent.setFlags(Intent.FLAG_ABILITY_NEW_MISSION);
        startAbility(intent);
    }

    private class PreferencesChangeCounter implements Preferences.PreferencesObserver {
        @Override
        public void onChange(Preferences preferences, String key) {
            BaseItemProvider itemProvider = mlistContainer.getItemProvider();
            if (itemProvider instanceof SampleItemProvider) {
                ((SampleItemProvider) itemProvider).refreshList(getData());
            }
        }
    }

    private void showDeleteDialog(final String key) {
        if (key == null || "".equals(key)) {
            return;
        }
        Context context = getContext();
        CommonDialog commonDialog = new CommonDialog(context);
        commonDialog.setTitleText("是否删除" + key + "?");
        commonDialog.setButton(1, "确定", new IDialog.ClickedListener() {
            @Override
            public void onClick(IDialog iDialog, int i) {
                Preferences preferences = PreferencesHelper.getInstance().getPreference(getApplicationContext());
                preferences.delete(key);
                preferences.flush();
                iDialog.hide();
            }
        });
        commonDialog.setButton(2, "取消", new IDialog.ClickedListener() {
            @Override
            public void onClick(IDialog iDialog, int i) {
                iDialog.hide();
            }
        });
        commonDialog.show();
    }
}
