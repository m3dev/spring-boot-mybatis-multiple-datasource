package com.example.springboot.multidatasource.datasourceA.mapper

import com.example.springboot.multidatasource.model.TestModel
import org.apache.ibatis.annotations.*

interface DataSourceAMapper {

    @Update("""
        create table if not exists test(
            id int not null primary key,
            text varchar not null
        )
    """)
    fun createTable()

    @Insert("""insert into test (id, text) values (#{id}, #{text})""")
    fun insert(record: TestModel)

    @Select("""select * from test""")
    @ConstructorArgs(
            Arg(name = "id", column = "id", id = true),
            Arg(name = "text", column = "text")
    )
    fun selectAll(): List<TestModel>
}