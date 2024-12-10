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

     /**
     * PostgreSQL용 DataSource 생성
     * - `@Primary`를 붙여 기본 데이터 소스로 사용 설정
     * - `@ConfigurationProperties`로 application.yml/properties의 설정을 매핑
     * - DataSourceBuilder를 사용하여 HikariCP DataSource 생성
     * - javaConfig 로 설정할 경우 properties 에 url 이 아닌 jdbc-url 로 명시 해줘야함
     */
    @Primary
    @Bean(name = "postgreDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.postgre")
    public DataSource postgreDataSource() {
        
        return DataSourceBuilder.create().type(HikariDataSource.class).build();

    }
     /**
     * JPA를 위한 EntityManagerFactory 설정
     * - `@Primary`로 기본 설정 지정
     * - Hibernate 속성(ddl-auto, dialect, show_sql) 적용
     * - EntityManagerFactoryBuilder로 JPA 구성
     * - PostgreSQL 설정에 맞춰 persistenceUnit과 모델 패키지 설정
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

    
    /**
     * PostgreSQL용 TransactionManager 설정
     */
    @Primary
    @Bean(name = "postgreTransactionManager")
    public PlatformTransactionManager postgreTransactionManager(
            @Qualifier("postgreEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }


    /**
     * MyBatis용 SqlSessionFactory 설정
     * - MyBatis 매퍼 XML과 DataSource 연결
     * - MapperLocations: mapper/postgre 디렉토리의 XML 파일들
     * - TypeAliasesPackage: MyBatis에서 엔티티 이름을 간단히 사용하도록 설정
     */
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
