package com.bill.zhihu.api.cookie;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Realm orm 对象
 * <p/>
 * Created by bill_lv on 2015/9/20.
 */
public class CookieTable extends RealmObject {

    @PrimaryKey
    private String name = "";
    private String domain = "";
    private long maxAge = 0;
    private String path = "";
    private String value = "";
    private String uri = "";

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
