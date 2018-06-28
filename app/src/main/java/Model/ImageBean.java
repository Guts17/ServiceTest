package model;

public class ImageBean {
    private String imgName;
    private String imgUrl;
    private String imgDesc;
    private String imgDetail;

    public ImageBean(String imgName, String imgUrl, String imgDesc, String imgDetail) {
        this.imgName = imgName;
        this.imgUrl = imgUrl;
        this.imgDesc = imgDesc;
        this.imgDetail = imgDetail;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgDesc() {
        return imgDesc;
    }

    public void setImgDesc(String imgDesc) {
        this.imgDesc = imgDesc;
    }

    public String getImgDetail() {
        return imgDetail;
    }

    public void setImgDetail(String imgDetail) {
        this.imgDetail = imgDetail;
    }

    @Override
    public String toString() {
        return "ImageBean{" +
                "imgName='" + imgName + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", imgDesc='" + imgDesc + '\'' +
                ", imgDetail='" + imgDetail + '\'' +
                '}';
    }
}
