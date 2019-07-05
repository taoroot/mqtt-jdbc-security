package cn.flizi.iot.mq;

/**
 * 设备实体类
 */
public class Device {

    private static final long serialVersionUID = 1L;

    // 设备拥有者
    private Integer userId;

    // 密码
    private String secret;

    // 0: 正常， 1：锁定
    private Integer status;


    public Device() {
    }


    public Device(Integer userId, String secret, Integer state) {
        this.userId = userId;
        this.secret = secret;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
