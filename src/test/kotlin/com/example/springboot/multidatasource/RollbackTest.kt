package com.example.springboot.multidatasource

import com.example.springboot.multidatasource.datasourceA.TransactionalArequiresNew
import com.example.springboot.multidatasource.datasourceA.mapper.DataSourceAMapper
import com.example.springboot.multidatasource.model.TestModel
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit4.SpringRunner
import java.io.IOException

@RunWith(SpringRunner::class)
@SpringBootTest
class RollbackTest {

    @Autowired
    private lateinit var datasourceAMapper: DataSourceAMapper

    @Autowired
    private lateinit var rollbackActionA: RollbackActionA

    @Test
    @TransactionalArequiresNew
    @Rollback
    fun testA() {
        datasourceAMapper.createTable()
        try {
            rollbackActionA.action()
        } catch (e: IOException) {}

        Assert.assertEquals("should be rollbacked", 0, datasourceAMapper.selectAll().size)
    }
}

@Component
class RollbackActionA(val datasourceAMapper: DataSourceAMapper) {

    @TransactionalArequiresNew
    @Throws(IOException::class)
    fun action() {
        datasourceAMapper.insert(TestModel(123, "should be rollbacked"))
        throw IOException()
    }
}