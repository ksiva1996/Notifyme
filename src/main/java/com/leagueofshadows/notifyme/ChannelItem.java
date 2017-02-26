package com.leagueofshadows.notifyme;

/**
 * Created by siva
 */

class ChannelItem {
    private String name;
    private String id;
    private String minvalue;
    private String maxvalue;
    private String apikey;
    ChannelItem (String name,String id,String maxvalue,String minvalue,String apikey)
    {
        this.name=name;
        this.id=id;
        this.apikey=apikey;
        this.maxvalue=maxvalue;
        this.minvalue=minvalue;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getName()
    {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getMaxvalue() {
        return maxvalue;
    }

    public String getMinvalue() {
        return minvalue;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMaxvalue(String maxvalue) {
        this.maxvalue = maxvalue;
    }

    public void setMinvalue(String minvalue) {
        this.minvalue = minvalue;
    }

    public void setName(String name) {
        this.name = name;
    }
}
