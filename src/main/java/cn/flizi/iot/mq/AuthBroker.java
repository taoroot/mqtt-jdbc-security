package cn.flizi.iot.mq;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.Message;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

/**
 * 插件实现
 */
public class AuthBroker extends AbstractAuthenticationBroker {

    // 管理员账号
    private String adminName;
    // 管理员密码
    private String adminPass;

    // 设备离线状态标志
    private static final int upFlag = 0;

    // 设备上线状态标志
    private static final int offFlag = 1;

    // 设备锁定标志
    private static final int lockFlag = 1;

    private JdbcTemplate jdbcTemplate;

    public AuthBroker(Broker next, JdbcTemplate jdbcTemplate, String adminName, String password) {
        super(next);
        this.adminName = adminName;
        this.adminPass = password;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
        SecurityContext securityContext = context.getSecurityContext();
        if (securityContext == null) {
            securityContext = authenticate(info.getUserName(), info.getPassword(), null);
            context.setSecurityContext(securityContext);
            securityContexts.add(securityContext);
        }

        try {
            super.addConnection(context, info);
        } catch (Exception e) {
            securityContexts.remove(securityContext);
            context.setSecurityContext(null);
            throw e;
        }
        System.out.println("设备上线 " + context.getUserName());
        updateDevice(upFlag, context.getUserName());
    }

    @Override
    public SecurityContext authenticate(String username, String password, X509Certificate[] peerCertificates) throws SecurityException {
        SecurityContext securityContext = null;
        // 超级管理员认证
        if (adminName.equals(username) && adminPass.equals(password)) {
            securityContext = new SecurityContext(username) {
                @Override
                public Set<Principal> getPrincipals() {
                    Set<Principal> groups = new HashSet<>();
                    groups.add(new GroupPrincipal(adminName));
                    return groups;
                }
            };
            return securityContext;
        }
        // 设备认证
        final Device device = getDevice(username);
        securityContext = new SecurityContext(username) {
            @Override
            public Set<Principal> getPrincipals() {
                Set<Principal> groups = new HashSet<>();
                groups.add(new GroupPrincipal(String.valueOf(device.getUserId())));
                return groups;
            }
        };
        return securityContext;
    }

    // 发送鉴权
    @Override
    public void send(ProducerBrokerExchange producerExchange, Message messageSend) throws Exception {
        // 主题
        String destination = messageSend.getDestination().getPhysicalName();
        // 设备id
        String username = producerExchange.getConnectionContext().getUserName();

        if (!adminName.equals(username)) {
            // 权限判断
            boolean flag = false;
            for (Principal principal : producerExchange.getConnectionContext().getSecurityContext().getPrincipals()) {
                String name = "\"iot." + principal.getName() + ".";
                if (destination.startsWith(name)) {
                    flag = true;
                }
            }
            if (!flag) {
                throw new SecurityException("权限不足");
            }
            // 记录发送者属性
            messageSend.setProperty("name", username);
        }
        // 载体不能为空
        if (messageSend.getContent().length == 0) {
            throw new SecurityException("数据包长度不能为空");
        }
        super.send(producerExchange, messageSend);
    }

    // 接听鉴权
    @Override
    public Subscription addConsumer(ConnectionContext context, ConsumerInfo info) throws Exception {
        String destination = info.getDestination().getPhysicalName();
        String username = context.getSecurityContext().getUserName();
        if (!adminName.equals(username)) {
            boolean flag = false;
            for (Principal principal : context.getSecurityContext().getPrincipals()) {
                String name = "\"iot." + principal.getName() + ".";
                if (destination.startsWith(name)) {
                    flag = true;
                }
            }
            if (!flag) {
                throw new SecurityException("权限不足");
            }
        }
        return super.addConsumer(context, info);
    }

    // 设备下线
    @Override
    public void removeConnection(ConnectionContext context, ConnectionInfo info, Throwable error) throws Exception {
        if (!adminName.equals(context.getUserName())) {
            updateDevice(offFlag, context.getUserName());
        }
        super.removeConnection(context, info, error);
    }

    // 查询设备信息
    private Device getDevice(String username) {
        String sql = "select * from iot_device where id=?";
        try {
            Device device = jdbcTemplate.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper<>(Device.class));
            if (device.getStatus() == lockFlag) {
                throw new SecurityException("设备已被锁定");
            }
            return device;
        } catch (Exception e) {
            throw new SecurityException("验证失败");
        }
    }

    // 更新设备状态
    private void updateDevice(Integer state, String username) {
        String sql = "update  iot_device set state = ?, last_online_time = now() where id=?";
        try {
            jdbcTemplate.update(sql, state, username);
        } catch (EmptyResultDataAccessException e) {
            throw new SecurityException("刷新设备状态失败");
        }
    }
}