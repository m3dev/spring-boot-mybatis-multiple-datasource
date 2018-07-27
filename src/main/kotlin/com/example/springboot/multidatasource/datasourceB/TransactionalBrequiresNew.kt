package com.example.springboot.multidatasource.datasourceB

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.lang.annotation.Inherited

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Inherited
@Transactional(DataSourceBConfig.TX_MANAGER_NAME, propagation = Propagation.REQUIRES_NEW, rollbackFor = [ Exception::class ])
annotation class TransactionalBrequiresNew
