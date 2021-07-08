###uid-spring-boot-starter是一个基于推特雪花算法的采用redis/zookeeper获取机器id来生成workId和dataCenterId的分布式ID生成器。

#####使用方式

1.使用redis策略
```
uid.generator.config.strategy=redis
uid.generator.config.redis.timeout=
uid.generator.config.redis.host=
uid.generator.config.redis.port=
uid.generator.config.redis.password=
uid.generator.config.redis.database=
uid.generator.config.redis.lettuce.pool.max-active=
uid.generator.config.redis.lettuce.pool.max-wait=
uid.generator.config.redis.lettuce.pool.max-idle=
uid.generator.config.redis.lettuce.pool.min-idle=
```

2.使用zookeeper的策略
```
uid.generator.config.strategy=zookeeper
uid.generator.config.zookeeper.connect-string=
uid.generator.config.zookeeper.namespace=
uid.generator.config.zookeeper.max-retries=
uid.generator.config.zookeeper.base-sleep-time-ms=
```
