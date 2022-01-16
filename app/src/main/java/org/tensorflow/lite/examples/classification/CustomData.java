package org.tensorflow.lite.examples.classification;

public class CustomData {
    private int iv_icon;
    private String tv_name;

    public CustomData(int iv_icon, String tv_name) {
        this.iv_icon = iv_icon;
        this.tv_name = tv_name;
    }

    public int getIv_icon() {
        return iv_icon;
    }

    public String getTv_name() {
        return tv_name;
    }

    public void setIv_icon(int iv_icon) {
        this.iv_icon = iv_icon;
    }

    public void setTv_name(String tv_name) {
        this.tv_name = tv_name;
    }
}
