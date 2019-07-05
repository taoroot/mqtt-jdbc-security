package cn.flizi.iot.mq;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 注入插件
 */
public class AuthPlugin implements BrokerPlugin {

    // jdbc
    JdbcTemplate jdbcTemplate;

    // 管理员账号
    private String adminName;

    // 管理员密码
    private String adminPass;

    public AuthPlugin(JdbcTemplate jdbcTemplate, String username, String password) {
        this.jdbcTemplate = jdbcTemplate;
        this.adminName = username;
        this.adminPass = password;

    }

    @Override
    public Broker installPlugin(Broker broker) throws Exception {
        return new AuthBroker(broker, jdbcTemplate, adminName, adminPass);
    }

}
