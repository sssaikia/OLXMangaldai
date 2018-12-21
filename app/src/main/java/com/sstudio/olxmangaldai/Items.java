package com.sstudio.olxmangaldai;

/**
 * Created by Alan on 9/23/2017.
 */

public class Items {
    private String texts,title;
    private String url,url2,url3;
    private String userId;
    private String category;
    long phone,timeSamop;
    public Items() {
    }
    public Items(String title,String texts,String url,String url2,String url3,String userId,String category,long phone,long timeSamop){
        this.title=title;
        this.texts=texts;
        this.url=url;
        this.userId=userId;
        this.url2=url2;
        this.url3=url3;
        this.category=category;
        this.phone=phone;
        this.timeSamop=timeSamop;
    }

    public long getTimeSamop() {
        return timeSamop;
    }

    public void setTimeSamop(long timeSamop) {
        this.timeSamop = timeSamop;
    }

    public long getPhone() {
        return phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public String getTexts() {
        return texts;
    }


    public String getUserId() {
        return userId;
    }

    public String getUrl() {
        return url;
    }

    public String getUrl2() {
        return url2;
    }

    public String getUrl3() {
        return url3;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl3(String url3) {
        this.url3 = url3;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }


    public void setTexts(String texts) {
        this.texts = texts;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
