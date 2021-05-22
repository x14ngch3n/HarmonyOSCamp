package com.ohos.demo.uitest.adapter;

import com.ohos.demo.uitest.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;

import java.util.List;

public class SampleItemProvider extends BaseItemProvider {
    private List<SampleItem> mList;
    private AbilitySlice mSlice;

    public SampleItemProvider(List<SampleItem> list, AbilitySlice slice) {
        super();
        mList = list;
        mSlice = slice;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 :mList.size();
    }

    @Override
    public Object getItem(int i) {
        if (mList != null && i >= 0 && i < mList.size()){
            return mList.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        final Component cpt;
        if (component == null) {
            cpt = LayoutScatter.getInstance(mSlice).parse(ResourceTable.Layout_item_sample, null, false);
        } else {
            cpt = component;
        }
        SampleItem sampleItem = mList.get(i);
        Text text = (Text) cpt.findComponentById(ResourceTable.Id_title);
        text.setText(sampleItem.getmTitle());

        Text content = (Text) cpt.findComponentById(ResourceTable.Id_content);
        content.setText(sampleItem.getmString());
        return cpt;
    }

    public void refreshList(List<SampleItem> list) {
        mList = list;
        notifyDataChanged();
    }
}
