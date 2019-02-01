package top.zlm.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * JPA配置
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"top.zlm.dao"})
public class JpaConfig {

    private DataSource dataSource;

    /**
     * 创建数据源管理器
     * @return
     */
    @SuppressWarnings("ContextJavaBeanUnresolvedMethodsInspection")
    @Bean(destroyMethod =  "close")
    public DataSource dataSource() {
/*        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://hd2:3306/db1?useUnicode=true&characterEncoding=utf8");
        dataSource.setUsername("root");
        dataSource.setPassword("root");*/
        //单例构造
        if(null == this.dataSource){
            synchronized (JpaConfig.class){
                if(null == this.dataSource){
                    DruidDataSource dataSource = new DruidDataSource();
                    dataSource.setUrl("jdbc:mysql://hd2:3306/db1?useUnicode=true&characterEncoding=utf8");
                    dataSource.setUsername("root");//用户名
                    dataSource.setPassword("root");//密码
                    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
                    dataSource.setInitialSize(1);//初始化时建立物理连接的个数
                    dataSource.setMaxActive(10);//最大连接池数量
                    dataSource.setMinIdle(0);//最小连接池数量
                    dataSource.setMaxWait(60000);//获取连接时最大等待时间，单位毫秒。
                    dataSource.setValidationQuery("SELECT 1");//用来检测连接是否有效的sql
                    dataSource.setTestOnBorrow(false);//申请连接时执行validationQuery检测连接是否有效
                    dataSource.setTestWhileIdle(true);//建议配置为true，不影响性能，并且保证安全性。
                    this.dataSource = dataSource;
                }
            }
        }
        return this.dataSource;
    }


    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.show_sql","true");
        properties.setProperty("hibernate.format_sql","true");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect","org.hibernate.dialect.MySQL5Dialect");
        return properties;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        // 把 package names 修改为你的 domain 类所在的包名就可以了
        em.setPackagesToScan("top.zlm.domain");
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    /**
     * 开启jdbcTemplate
     * @return
     */
    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource());
    }

}
