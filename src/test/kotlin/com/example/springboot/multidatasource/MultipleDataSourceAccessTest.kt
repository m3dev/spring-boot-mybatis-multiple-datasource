package com.example.springboot.multidatasource

import com.example.springboot.multidatasource.datasourceA.TransactionalArequiresNew
import com.example.springboot.multidatasource.datasourceA.mapper.DataSourceAMapper
import com.example.springboot.multidatasource.datasourceB.TransactionalBrequiresNew
import com.example.springboot.multidatasource.datasourceB.mapper.DataSourceBMapper
import com.example.springboot.multidatasource.model.TestModel
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.interceptor.TransactionAspectSupport

@RunWith(SpringRunner::class)
@SpringBootTest
class MultipleDataSourceAccessTest {

    @Autowired
    private lateinit var manipulatorA: ManipulatorA
    @Autowired
    private lateinit var manipulatorB: ManipulatorB

    @Test
    // Because this test requires transactions to both A and B, I don't add @Transactional annotation here.
    // But I defined two Component and each has @Transactional annotation.
    // Each component establish transaction for each database.
    //
    // In other way, you can use TransactionTemplate to explicitly establish transactions to two databases.
    fun test() {
        manipulatorA.action {
            Assert.assertEquals(1, manipulatorA.mapper.selectAll().size) // Inserted by ManipulatorA

            manipulatorB.action {
                Assert.assertEquals(1, manipulatorB.mapper.selectAll().size) // Inserted by ManipulatorB
            }
        }
    }
}

@Component
class ManipulatorA(val mapper: DataSourceAMapper) {

    @TransactionalArequiresNew
    fun action(callback: () -> Unit) {
        mapper.createTable()
        Assert.assertEquals(0, mapper.selectAll().size)
        mapper.insert(TestModel(123, "test record"))
        Assert.assertEquals(1, mapper.selectAll().size)

        // Execute given callback within transaction of A
        callback()

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
    }
}

@Component
class ManipulatorB(val mapper: DataSourceBMapper) {

    @TransactionalBrequiresNew
    @Rollback
    fun action(callback: () -> Unit) {
        mapper.createTable()
        Assert.assertEquals(0, mapper.selectAll().size) // Should be zero. Different database with ManipulatorB
        mapper.insert(TestModel(123, "test record"))
        Assert.assertEquals(1, mapper.selectAll().size)

        callback()

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
    }
}