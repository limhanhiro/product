package com.fw.product.config.db.postgre;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
@EnableJpaRepositories(entityManagerFactoryRef = "postgreEntityManagerFactory", transactionManagerRef = "postgreTransactionManager", basePackages = {
        "com.fw.product.repository.postgre" })
@MapperScan(basePackages = "com.fw.product.mapper.postgre", sqlSessionFactoryRef = "postgreSqlSessionFactory")
@RequiredArgsConstructor
public class PostgreConfig {

    private final Environment environment;

    @Primary
    @Bean(name = "postgreDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.postgre")
    public DataSource postgreDataSource() {
        
        return DataSourceBuilder.create().type(HikariDataSource.class).build();

    }

    /**
     * EntityManagerFactory 설정 (PostgreSQL).
     */
    @Primary
    @Bean(name = "postgreEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean postgreEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("postgreDataSource") DataSource postgreDataSource) {

        String ddlAuto = environment.getProperty("spring.jpa.postgre.hibernate.ddl-auto");
        String dialect = environment.getProperty("spring.jpa.postgre.properties.hibernate.dialect");
        String showSql = environment.getProperty("spring.jpa.postgre.show-sql");

        // 디버깅 또는 로그 출력
        System.out.println("postgreEntityManagerFactory");
        System.out.println("DDL Auto: " + ddlAuto);
        System.out.println("Dialect: " + dialect);
        System.out.println("Show SQL: " + showSql);

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto",
                environment.getProperty("spring.jpa.postgre.hibernate.ddl-auto", "update"));
        jpaProperties.put("hibernate.dialect", environment.getProperty(
                "spring.jpa.postgre.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"));
        jpaProperties.put("hibernate.show_sql", environment.getProperty("spring.jpa.postgre.show-sql", "true"));

        return builder
                .dataSource(postgreDataSource)
                .packages("com.fw.product.model.postgre")
                .persistenceUnit("postgre")
                .properties(jpaProperties)
                .build();
    }
    @Primary
    @Bean(name = "postgreTransactionManager")
    public PlatformTransactionManager postgreTransactionManager(
            @Qualifier("postgreEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
    
    @Primary
    @Bean(name = "postgreSqlSessionFactory")
    public SqlSessionFactory postgreSqlSessionFactory(@Qualifier("postgreDataSource") DataSource postgreDataSource,
            ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(postgreDataSource);
        sqlSessionFactoryBean.setTypeAliasesPackage("com.fw.product.model.postgre");
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/postgre/**/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }
}
