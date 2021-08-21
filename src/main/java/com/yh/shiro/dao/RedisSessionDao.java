package com.yh.shiro.dao;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "shiro", name = "cluster", havingValue = "true")
public class RedisSessionDao extends AbstractSessionDAO {

    @Value("${shiro.session.prefix}")
    private String prefix;

    private static final Logger logger = LoggerFactory.getLogger(RedisSessionDao.class);

    @Resource
    private RedisTemplate<String, Session> redisTemplate;

    @Override
    public void update(final Session session) throws UnknownSessionException {
        if (session != null && session.getId() != null) {
            String sessionkey = keyString(session.getId().toString());
            redisTemplate.opsForValue().set(sessionkey, session, 86400, TimeUnit.SECONDS);
            logger.info("update redis session key: " + sessionkey);
        }
    }

    @Override
    public void delete(Session session) {
        if (session != null && session.getId() != null) {
            String sessionkey = keyString(session.getId().toString());
            redisTemplate.opsForValue().getOperations().delete(sessionkey);
            logger.info("delete redis session key: " + sessionkey);
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<Session> sessions = new HashSet<>();
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            List<Session> sessionList = redisTemplate.opsForValue().multiGet(keys);
            if (!CollectionUtils.isEmpty(sessionList)) {
                sessions.addAll(sessionList);
            }
        }
        return sessions;
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        String sessionkey = keyString(session.getId().toString());
        redisTemplate.opsForValue().setIfAbsent(sessionkey, session, 86400, TimeUnit.SECONDS);
        logger.info("create redis session key: " + sessionkey);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            return null;
        }
        String sessionkey = keyString(sessionId.toString());
        Session session = (Session) redisTemplate.opsForValue().get(sessionkey);
        logger.info("read redis session key: " + sessionkey);
        return session;
    }


    public String keyString(String id) {
        return prefix + id;
    }

}
