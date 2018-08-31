package hx.http2.gson;

import java.io.Serializable;
import java.util.Date;

/**
 * @author andy
 * @date 2018-08-31
 */
public class Persion implements Serializable {

    private String name;
    private String add;
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
