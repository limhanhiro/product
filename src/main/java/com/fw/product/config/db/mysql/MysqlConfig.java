package com.fw.product.config.db.mysql;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "mysqlEntityManagerFactory", transactionManagerRef = "mysqlTransactionManager", basePackages = {
        "com.fw.product.repository.mysql" })
@MapperScan(basePackages = "com.fw.product.mapper.mysql", sqlSessionFactoryRef = "mysqlSqlSessionFactory")
@RequiredArgsConstructor
public class MysqlConfig {

    private final Environment environment;


    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource mysqlDataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        // 설정값 직접 확인
        System.out.println("JDBC URL: " + environment.getProperty("spring.datasource.mysql.url"));
        System.out.println("Username: " + environment.getProperty("spring.datasource.mysql.username"));
        System.out.println("Driver: " + environment.getProperty("spring.datasource.mysql.driver-class-name"));

        return dataSource;
    }


    @Bean(name = "mysqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("mysqlDataSource") DataSource mysqlDataSource) {
        String ddlAuto = environment.getProperty("spring.jpa.mysql.hibernate.ddl-auto");
        String dialect = environment.getProperty("spring.jpa.mysql.properties.hibernate.dialect");
        String showSql = environment.getProperty("spring.jpa.mysql.show-sql");

        // 디버깅 또는 로그 출력
        System.out.println("mysqlEntityManagerFactory");
        System.out.println("DDL Auto: " + ddlAuto);
        System.out.println("Dialect: " + dialect);
        System.out.println("Show SQL: " + showSql);

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto",
                environment.getProperty("spring.jpa.mysql.hibernate.ddl-auto", "update"));
        jpaProperties.put("hibernate.dialect", environment.getProperty("spring.jpa.mysql.properties.hibernate.dialect",
                "org.hibernate.dialect.MySQLDialect"));
        jpaProperties.put("hibernate.show_sql", environment.getProperty("spring.jpa.mysql.show-sql", "true"));

        return builder
                .dataSource(mysqlDataSource)
                .packages("com.fw.product.model.mysql")
                .persistenceUnit("mysql")
                .properties(jpaProperties)
                .build();
    }


    @Bean(name = "mysqlTransactionManager")
    public PlatformTransactionManager mysqlTransactionManager(
            @Qualifier("mysqlEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory mysqlSqlSessionFactory(@Qualifier("mysqlDataSource") DataSource mysqlDataSource,
            ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(mysqlDataSource);
        sqlSessionFactoryBean.setTypeAliasesPackage("com.fw.product.model.mysql");
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/mysql/**/*.xml"));
        
        // org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        // configuration.getTypeAliasRegistry().registerAliases("com.fw.product.model.mysql");
        // sqlSessionFactoryBean.setConfiguration(configuration);
        
        return sqlSessionFactoryBean.getObject();
    }

    
}
