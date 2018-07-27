package com.example.springboot.multidatasource.datasourceB

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.SqlSessionTemplate
import org.mybatis.spring.annotation.MapperScan
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import javax.sql.DataSource

/**
 * Same as [com.example.springboot.multidatasource.datasourceA.datasourceAConfig], see it for more explanation.
 */
@Configuration
@Import(DataSourceBConfig.MyBatisConfig::class)
class DataSourceBConfig {
    companion object {
        const val TX_MANAGER_NAME = "transactionManagerB"
        const val TX_TEMPLATE_NAME = "transactionTemplateB"

        private const val HIKARI_CONFIG_NAME = "hikariConfigB"
        private const val DATA_SOURCE_NAME = "datasourceB"
        private const val MYBATIS_SESSION_FACTORY_NAME = "sqlSessionFactoryB"
        private const val MYBATIS_SESSION_TEMPLATE_NAME = "sqlSessionTemplateB"
    }

    /**
     * To enable connection pooling, use HikariCP.
     * If you really don't need connection pooling, just use DataSource class from your JDBC client library.
     */
    @Bean(name = [ HIKARI_CONFIG_NAME ])
    @ConfigurationProperties("spring.datasource.b") // see application.yml
    fun datasourceBHikariConfig() = HikariConfig()

    @Bean(name = [ DATA_SOURCE_NAME ])
    fun datasourceB(@Qualifier(HIKARI_CONFIG_NAME) hikariConfig: HikariConfig) = HikariDataSource(hikariConfig)

    @Bean(name = [ TX_MANAGER_NAME ]) // Note: name of this Bean is important for @Transactional annotation.
    fun transactionManagerB(
            @Qualifier(DATA_SOURCE_NAME) dataSource: DataSource,
            transactionManagerCustomizers: ObjectProvider<TransactionManagerCustomizers>
    ) = DataSourceTransactionManager(dataSource).also { txMgr ->
        transactionManagerCustomizers.ifAvailable {
            it.customize(txMgr)
        }
    }

    @Bean(name = [ TX_TEMPLATE_NAME ])
    fun transactionTemplateB(@Qualifier(TX_MANAGER_NAME) txMgr: PlatformTransactionManager) = TransactionTemplate(txMgr)


    @Configuration
    @MapperScan(basePackageClasses = [ DataSourceBConfig::class ], sqlSessionFactoryRef = MYBATIS_SESSION_FACTORY_NAME, sqlSessionTemplateRef = MYBATIS_SESSION_TEMPLATE_NAME)
    class MyBatisConfig {

        @Bean(name = [ MYBATIS_SESSION_FACTORY_NAME ])
        fun sqlSessionFactoryB(
                @Qualifier(DATA_SOURCE_NAME) dataSource: DataSource,
                @Autowired(required = false) configurationCustomizerList: List<ConfigurationCustomizer>?
        ) = SqlSessionFactoryBean().let {
            it.setDataSource(dataSource)
            it.setConfiguration(org.apache.ibatis.session.Configuration().also { config ->
                configurationCustomizerList?.forEach{ it.customize(config) }
            })
            it.`object` as SqlSessionFactory
        }

        @Bean(name = [ MYBATIS_SESSION_TEMPLATE_NAME ])
        fun sqlSessinTemplateB(@Qualifier(MYBATIS_SESSION_FACTORY_NAME) sqlSessionFactory: SqlSessionFactory) = SqlSessionTemplate(sqlSessionFactory)
    }
}