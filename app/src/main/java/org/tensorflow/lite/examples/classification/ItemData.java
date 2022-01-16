package org.tensorflow.lite.examples.classification;

public class ItemData {
    private String image;
    private String name;
    private String price;
    private String link;

    // 생성자
    // 기본
    public ItemData() {}

    // 사용자 지정
    public ItemData(String image, String name, String price, String link){
        this.image = image;
        this.name = name;
        this.price = price;
        this.link = link;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getLink() {
        return link;
    }
}
