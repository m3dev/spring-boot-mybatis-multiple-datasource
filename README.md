# Spring Boot + MyBatis multiple DataSource sample

Sample project to show how to use multiple `DataSource` (different database connections) with Spring Boot + MyBatis.

Key of this sample is `@Configuration` classes ([DataSourceAConfig.kt](src/main/kotlin/com/example/springboot/multidatasource/datasourceA/DataSourceAConfig.kt), [DataSourceBConfig.kt](src/main/kotlin/com/example/springboot/multidatasource/datasourceB/DataSourceBConfig.kt)).
