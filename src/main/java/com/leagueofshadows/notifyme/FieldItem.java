package com.leagueofshadows.notifyme;

/**
 * Created by siva
 */

class FieldItem {
    private String name;
    private String last;
    private String average;
    FieldItem(String name, String average, String last)
    {
        this.name=name;
        this.average=average;
        this.last=last;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setlast(String last) {
        this.last = last;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public String getAverage() {
        return average;
    }

    public String getLast() {
        return last;
    }


    public String getName() {
        return name;
    }
}
