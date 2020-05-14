package com.xum.cmnd.pojo;

import java.util.Date;

public class RedPacket {
    private Integer id;

    private Integer redpacketid;

    private Integer userid;

    private Long money;

    private Date createtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRedpacketid() {
        return redpacketid;
    }

    public void setRedpacketid(Integer redpacketid) {
        this.redpacketid = redpacketid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}