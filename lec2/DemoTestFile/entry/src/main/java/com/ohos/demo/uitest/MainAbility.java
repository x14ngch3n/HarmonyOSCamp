package com.ohos.demo.uitest;

import com.ohos.demo.uitest.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        requestPermission();
    }

    private void requestPermission() {
        String[] permissions = {
                "ohos.permission.READ_USER_STORAGE",
                "ohos.permission.WRITE_USER_STORAGE",
                "ohos.permission.DISTRIBUTED_DATASYNC"
        };
        List<String> applyPermissions = new ArrayList<>();
        for (String element : permissions) {
            if (verifySelfPermission(element) != 0) {
                if (canRequestPermission(element)) {
                    applyPermissions.add(element);
                }
            }
        }
        requestPermissionsFromUser(applyPermissions.toArray(new String[0]), 0);
    }

    @Override
    protected void onActive() {
        super.onActive();
        Util.setSaveContext(getContext());
    }
}
