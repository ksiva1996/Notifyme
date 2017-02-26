package com.leagueofshadows.notifyme;

/**
 * Created by siva on 2/26/2017.
 */

public class FeedItem {
    String value;
    String id;
    private String time;
    FeedItem(String value,String id,String time)
    {
        this.value=value;
        this.id=id;
        this.time=time;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getValue() {
        return value;
    }
}
