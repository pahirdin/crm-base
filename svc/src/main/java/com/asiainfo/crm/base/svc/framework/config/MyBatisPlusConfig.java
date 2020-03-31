package com.asiainfo.crm.base.svc.framework.config;

import com.asiainfo.bits.core.util.PackageUtils;
import com.asiainfo.bits.skeleton.database.DataSourceKey;
import com.asiainfo.bits.skeleton.database.MyGlobalConfig;
import com.asiainfo.bits.skeleton.database.MySqlSessionTemplate;
import com.asiainfo.bits.skeleton.database.aop.SqlPerformanceInterceptor;
import com.asiainfo.bits.skeleton.database.threadlocal.ShardTableContextHolder;
import com.asiainfo.crm.base.svc.framework.aop.BaseAutoSetMetaObjectAdvice;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.parsers.BlockAttackSqlParser;
import com.baomidou.mybatisplus.extension.parsers.DynamicTableNameParser;
import com.baomidou.mybatisplus.extension.parsers.ITableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.SqlExplainInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.*;

/**
 * @author Steven
 * @date 2020-03-05
 */
@EnableTransactionManagement
@Slf4j
@Configuration
@MapperScan(basePackages = {"com.asiainfo.bits.**.mapper", "com.asiainfo.crm.**.mapper"}, sqlSessionTemplateRef = "sqlSessionTemplate")
public class MyBatisPlusConfig {

    @Autowired(required = false)
    private OptimisticLockerInterceptor optimisticLockerInterceptor;

    @Autowired(required = false)
    private PaginationInterceptor paginationInterceptor;

    @Autowired(required = false)
    private SqlExplainInterceptor sqlExplainInterceptor;

    @Autowired(required = false)
    private SqlPerformanceInterceptor sqlPerformanceInterceptor;

    @Autowired
    private BaseAutoSetMetaObjectAdvice baseAutoSetMetaObjectAdvice;

    @Bean
    public IKeyGenerator keyGenerator() {
        return new OracleKeyGenerator();
    }

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.atomikos.base")
    public AtomikosNonXADataSourceBean baseDataSource() {
        return DataSourceBuilder.create().type(AtomikosNonXADataSourceBean.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.atomikos.cp")
    public AtomikosNonXADataSourceBean cpDataSource() {
        return DataSourceBuilder.create().type(AtomikosNonXADataSourceBean.class).build();
    }

    @Bean(name = "sqlSessionTemplate")
    public MySqlSessionTemplate customSqlSessionTemplate() throws Exception {
        Map<String, SqlSessionFactory> sqlSessionFactoryMap = new HashMap<String, SqlSessionFactory>(3) {{
            put("base", createSqlSessionFactory(baseDataSource()));
            put("cp", createSqlSessionFactory(cpDataSource()));
        }};
        MySqlSessionTemplate sqlSessionTemplate = new MySqlSessionTemplate(sqlSessionFactoryMap.get(DataSourceKey.BASE));
        sqlSessionTemplate.setTargetSqlSessionFactories(sqlSessionFactoryMap);
        return sqlSessionTemplate;
    }

    /**
     * 创建数据源
     *
     * @param dataSource
     * @return
     */
    private SqlSessionFactory createSqlSessionFactory(AtomikosNonXADataSourceBean dataSource) throws Exception {

        log.info("初始化数据源: {}", dataSource.getUser());
        dataSource.init();

        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/mapper/**/*.xml"));
        sqlSessionFactory.setVfs(SpringBootVFS.class);

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(true);

        sqlSessionFactory.setConfiguration(configuration);
        sqlSessionFactory.setPlugins(fetchInterceptors());

        // 重写了 GlobalConfig 的 MyGlobalConfig 注入到 sqlSessionFactory 使其生效
        MyGlobalConfig globalConfig = new MyGlobalConfig();
        globalConfig.setBanner(false);
        log.info("元数据自动填充: {}", baseAutoSetMetaObjectAdvice);
        globalConfig.setMetaObjectHandler(baseAutoSetMetaObjectAdvice);

        // 注册序列生成器
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setKeyGenerator(keyGenerator());
        globalConfig.setDbConfig(dbConfig);

        sqlSessionFactory.setGlobalConfig(globalConfig);
        sqlSessionFactory.afterPropertiesSet();

        return sqlSessionFactory.getObject();
    }

    /**
     * 各类拦截器
     */
    private Interceptor[] fetchInterceptors() {

        List<Interceptor> interceptors = new ArrayList<>();

        if (null != optimisticLockerInterceptor) {
            interceptors.add(optimisticLockerInterceptor);
        }

        if (null != paginationInterceptor) {
            interceptors.add(paginationInterceptor);
        }

        if (null != sqlExplainInterceptor) {
            interceptors.add(sqlExplainInterceptor);
        }

        if (null != sqlPerformanceInterceptor) {
            interceptors.add(sqlPerformanceInterceptor);
        }

        for (Interceptor interceptor : interceptors) {
            log.info("添加拦截器: {}", PackageUtils.compactPackage(interceptor.getClass()));
        }

        return interceptors.toArray(new Interceptor[0]);
    }

    /**
     * 乐观锁拦截器
     *
     * @return 返回乐观锁拦截器
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        OptimisticLockerInterceptor optimisticLockerInterceptor = new OptimisticLockerInterceptor();
        return optimisticLockerInterceptor;
    }

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setSqlParserList(Collections.singletonList(shardTableParser()));
        return paginationInterceptor;
    }

    /**
     * 分表解析器
     *
     * @return
     */
    private ISqlParser shardTableParser() {

        log.info("配置分表解析器...");

        DynamicTableNameParser dynamicTableNameParser = new DynamicTableNameParser();
        Map<String, ITableNameHandler> tableNameHandlerMap = new HashMap<>(16);
        dynamicTableNameParser.setTableNameHandlerMap(tableNameHandlerMap);

        // 指定要分表的表名
        tableNameHandlerMap.put("TEST_OP_LOG", (metaObject, sql, tableName) -> StringUtils.joinWith("_", tableName, ShardTableContextHolder.peek()));

        tableNameHandlerMap.keySet().forEach(tableName -> log.info("  分表: {} ", tableName));
        return dynamicTableNameParser;
    }

    /**
     * 利用攻击 SQL 阻断解析器，来防止 "全表更新"，"全表删除" 等高危操作。
     *
     * @return
     */
    @Bean
    public SqlExplainInterceptor sqlExplainInterceptor() {
        SqlExplainInterceptor sqlExplainInterceptor = new SqlExplainInterceptor();

        List<ISqlParser> sqlParserList = new ArrayList<>();
        sqlParserList.add(new BlockAttackSqlParser());

        sqlExplainInterceptor.setSqlParserList(sqlParserList);
        return sqlExplainInterceptor;
    }

    /**
     * 性能分析拦截器，用于输出每条 SQL 语句及其执行时间，仅开发、测试环境使用，生产环境不推荐。
     */
    @Bean
//    @Profile({"dev", "test"})
    public SqlPerformanceInterceptor performanceInterceptor() {
        SqlPerformanceInterceptor sqlPerformanceInterceptor = new SqlPerformanceInterceptor();
        // SQL 格式化开关
        sqlPerformanceInterceptor.setFormat(false);
        // SQL 最长执行时间，超过自动停止运行，单位毫秒
        sqlPerformanceInterceptor.setMaxTime(5000);
        // SQL 告警时间，超过在控制台以红色打印，单位毫秒
        sqlPerformanceInterceptor.setWarnTime(80);
        return sqlPerformanceInterceptor;
    }

}
