package com.hexagon.hrms.config.spring;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.flywaydb.core.Flyway;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

/*
 ServletContext 수준(전역)의 빈 설정: DB, MyBatis, 트랜잭션, Flyway, 서비스, 시큐리티
 시큐리티는 필터이므로 서블릿보다 먼저 만들어져야 해서 Root 수준에 등록한다
*/
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = { "com.hexagon.hrms.mapper" })
@ComponentScan(basePackages = {
	"com.hexagon.hrms.service",
	"com.hexagon.hrms.security"
})
@PropertySource("classpath:db.properties") // DB 접속 정보는 팀원마다 다를 수 있어 파일로 분리
public class RootConfig {

	@Value("${jdbc.driver}")
	private String driver;
	@Value("${jdbc.url}")
	private String url;
	@Value("${jdbc.username}")
	private String username;
	@Value("${jdbc.password}")
	private String password;

	// @Value("${...}") 를 해석해 주는 빈 (static 이어야 함)
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	/*----------------------------------------------
	 커넥션 풀 (히카리)
	----------------------------------------------*/
	@Bean
	public DataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setJdbcUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setMaximumPoolSize(10);
		dataSource.setMinimumIdle(5);
		return dataSource;
	}

	/*----------------------------------------------
	 Flyway: 톰캣 시작 시 db/migration 의 V1, V2 ... 를 순서대로 실행
	----------------------------------------------*/
	@Bean(initMethod = "migrate")
	public Flyway flyway(DataSource dataSource) {
		return Flyway.configure()
			.dataSource(dataSource)
			.locations("classpath:db/migration")
			.baselineOnMigrate(true)
			.baselineVersion("0")
			.load();
	}

	/*----------------------------------------------
	 MyBatis (DB 구축이 끝난 뒤 초기화)
	----------------------------------------------*/
	@Bean
	@DependsOn("flyway")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource());
		factoryBean.setConfigLocation(new ClassPathResource("com/hexagon/hrms/config/mybatis/mybatis-config.xml"));
		factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mappers/**/*.xml"));
		return factoryBean.getObject();
	}

	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	/*----------------------------------------------
	 트랜잭션 관리자 (@Transactional 이 붙은 서비스에서 예외가 나면 rollback)
	----------------------------------------------*/
	@Bean
	public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
