package com.ohos.demo.uitest;

import com.ohos.demo.uitest.slice.PreviewAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class PreviewAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(PreviewAbilitySlice.class.getName());
    }
}
