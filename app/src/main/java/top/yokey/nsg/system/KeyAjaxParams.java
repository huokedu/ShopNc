package top.yokey.nsg.system;

import net.tsz.afinal.http.AjaxParams;

import java.io.File;
import java.io.FileNotFoundException;

import top.yokey.nsg.activity.home.NcApplication;

public class KeyAjaxParams extends AjaxParams {

    public KeyAjaxParams(NcApplication application) {
        this.put("key", application.userKeyString);
    }

    @Override
    public void put(String key, String value) {
        super.put(key, value);
    }

    @Override
    public void put(String key, File file) throws FileNotFoundException {
        super.put(key, file);
    }

    public void putAct(String value) {
        this.put("act", value);
    }

    public void putOp(String value) {
        this.put("op", value);
    }

}