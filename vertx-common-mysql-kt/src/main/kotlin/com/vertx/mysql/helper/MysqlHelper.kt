package com.vertx.mysql.helper

import cn.hutool.core.collection.CollUtil
import cn.hutool.log.StaticLog
import com.vertx.common.config.active
import com.vertx.common.entity.mysql.PageResult
import com.vertx.common.enums.EnvEnum
import com.vertx.common.utils.underlineName
import com.vertx.mysql.client.mysqlPoolClient
import com.vertx.mysql.utils.getTableName
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLClient
import io.vertx.sqlclient.SqlConnection
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.conf.ParamType
import org.jooq.conf.Settings
import org.jooq.impl.DSL

/**
 * mysql帮助类
 * 用于简化mysql操作
 * 所有建表都应存在id字段，且为自增主键
 */
object MysqlHelper {
    /**
     * 插入数据
     * 如果对象不包含id主键,则默认返回0
     * @param data 数据对象
     * @return 获取最新的id
     */
    suspend fun insert(data: Any, connection: SqlConnection? = null): Long {
        val sql = buildInsertSql(listOf(data))
        val query = if (connection != null) {
            connection.query(sql)
        } else {
            mysqlPoolClient.query(sql)
        }
        val rowRowSet = query.execute().await()
        // 检查对象是否存在id主键
        return try {
            data.javaClass.getDeclaredField("id")
            rowRowSet.property(MySQLClient.LAST_INSERTED_ID)
        } catch (_: NoSuchFieldException) {
            StaticLog.warn("对象${data::class.java.name}不存在id主键")
            0
        }
    }

    /**
     * 批量插入数据
     * 会根据batchSize分批次插入,每次一个事务,如果更新失败会回滚
     * 如何数据量大于batchSize,则会分批次插入
     * @param data 数据对象
     * @param batchSize 批量大小
     * @param connection 数据库连接
     * @return 受影响的行数
     */
    suspend fun insertBatch(data: List<Any>, batchSize: Int = 100, connection: SqlConnection? = null): Int {
        if (data.isEmpty()) {
            StaticLog.warn("批量插入数据为空")
            return 0
        }
        // 影响行数
        var count = 0
        //批量插入数据
        val lists = CollUtil.split(data, batchSize)
        val connect = connection ?: mysqlPoolClient.connection.await()
        //开启事务
        val transaction = connect.begin().await()
        try {
            for (list in lists) {
                val querySql = buildInsertSql(list)
                val rows = connect.query(querySql).execute().await()
                count += rows.rowCount()
            }
            //提交事务
            transaction.commit().await()
        } catch (e: Throwable) {
            //回滚事务
            transaction.rollback().await()
            StaticLog.error(e, "批量插入数据失败")
        } finally {
            connect.close().await()
        }
        return count
    }

    /**
     * 更新数据
     * @param data 数据对象
     * @param where 条件
     * @param isNll 是否更新空值
     * @param connection 数据库连接
     * @return 受影响的行数
     */
    suspend fun update(data: Any, where: Condition, isNll: Boolean = false, connection: SqlConnection? = null): Int {
        val sql = buildUpdateSql(data, where, isNll)
        val rowRowSet = if (connection != null) {
            connection.query(sql).execute().await()
        } else {
            mysqlPoolClient.query(sql).execute().await()
        }
        return rowRowSet.rowCount()
    }

    /**
     * 批量更新数据
     * 会根据batchSize分批次更新,每次一个事务,如果更新失败会回滚
     * 如何数据量大于batchSize,则会分批次更新
     * @param data 数据对象
     * @param where 条件
     * @param isNll 是否更新空值
     * @param batchSize 批量大小
     * @param connection 数据库连接
     * @return 受影响的行数
     */
    suspend fun updateBatch(
        data: List<Any>,
        where: Condition,
        isNll: Boolean = false,
        batchSize: Int = 100,
        connection: SqlConnection? = null
    ): Int {
        if (data.isEmpty()) {
            StaticLog.warn("批量更新数据为空")
            return 0
        }
        // 影响行数
        var count = 0
        //批量更新数据
        val sqlList = data.map { buildUpdateSql(it, where, isNll) }
        val lists = CollUtil.split(sqlList, batchSize)
        val connect = connection ?: mysqlPoolClient.connection.await()
        //开启事务
        val transaction = connect.begin().await()
        try {
            for (list in lists) {
                for (querySql in list) {
                    val rows = connect.query(querySql).execute().await()
                    count += rows.rowCount()
                }
            }
            //提交事务
            transaction.commit().await()
        } catch (e: Throwable) {
            //回滚事务
            transaction.rollback().await()
            StaticLog.error(e, "批量更新数据失败", lists)
        } finally {
            connect.close().await()
        }
        return count
    }

    /**
     * 删除数据
     * @param clazz 类对象
     * @param where 条件
     * @param connection 数据库连接
     * @return 受影响的行数
     */
    suspend fun delete(clazz: Class<*>, where: Condition, connection: SqlConnection? = null): Int {
        val sql = buildDeleteSql(clazz, where)
        val rowRowSet = if (connection != null) {
            connection.query(sql).execute().await()
        } else {
            mysqlPoolClient.query(sql).execute().await()
        }
        return rowRowSet.rowCount()
    }

    /**
     * 查询数据
     * @param clazz 类对象
     * @param where 条件
     * @param fields 查询字段
     * @return 查询结果
     */
    suspend fun <T> select(
        clazz: Class<T>, where: Condition, fields: List<String> = listOf(), lastSql: String = ""
    ): List<T> {
        val sql = buildSelectSql(clazz, where, fields, lastSql)
        val rowRowSet = mysqlPoolClient.query(sql).execute().await().map {
            it.toJson().mapTo(clazz)
        }
        return rowRowSet
    }

    /**
     * 查询分页
     * @param clazz 类对象
     * @param where 条件
     * @param fields 查询字段
     * @param page 页码 默认为1
     * @param pageSize 每页条数 默认为10
     * @return 查询结果
     */
    suspend fun <T> selectPage(
        clazz: Class<T>,
        where: Condition,
        fields: List<String> = listOf(),
        page: Int = 1,
        pageSize: Int = 10,
    ): PageResult<T>? {
        if (page < 1) {
            StaticLog.warn("分页查询页码不能小于1")
            return null
        }
        if (pageSize < 1) {
            StaticLog.warn("分页查询每页条数不能小于1")
            return null
        }
        val sql = buildSelectSql(
            clazz, where, fields, lastSql = " order by id desc limit ${(page - 1) * pageSize},$pageSize"
        )
        val rowRowSet = mysqlPoolClient.query(sql).execute().await().map {
            it.toJson().mapTo(clazz)
        }
        val selectCountSql = buildCountSql(clazz, where)
        val count = mysqlPoolClient.query(selectCountSql).execute().await().firstOrNull()?.getLong(0) ?: 0
        return PageResult(page, pageSize, count, (count / pageSize).toInt() + 1, rowRowSet)
    }


    /**
     *  jooq 上下文
     */
    private val dslContext = DSL.using(SQLDialect.MYSQL, Settings())


    private fun buildCountSql(clazz: Class<*>, where: Condition): String {
        val selectCount =
            dslContext.selectCount().from(DSL.table(getTableName(clazz))).where(where).getSQL(ParamType.INLINED)
        if (active != EnvEnum.PROD.env) {
            StaticLog.info("查询语句：${selectCount}")
        }
        return selectCount
    }

    /**
     * 封装插入语句
     * @param dataList 数据对象
     */
    private fun buildInsertSql(dataList: List<Any>): String {
        if (dataList.isEmpty()) {
            StaticLog.warn("批量插入数据为空")
            return ""
        }
        val clazz = dataList[0]::class.java
        //获取类的属性
        val fields = clazz.declaredFields
        //获取类的属性名
        val fileKeyValueList = mutableListOf<MutableMap<String, Any?>>()
        for (item in dataList) {
            val map = mutableMapOf<String, Any?>()
            for (field in fields) {
                field.isAccessible = true
                val name = field.name.underlineName()
                val value = field.get(item)
                //如果属性值不为空，则添加到map中,忽略主键
                if (name != "id") {
                    map[name] = value
                }
            }
            fileKeyValueList.add(map)
        }
        val insertIntoStep = dslContext.insertInto(
            DSL.table(getTableName(clazz)),
            fileKeyValueList[0].keys.toList().map { DSL.field(it) })
        for (mutableMap in fileKeyValueList) {
            insertIntoStep.values(mutableMap.values.toList())
        }
        //返回组装好的sql语句,insertIntoStep.sql是带占位符的sql语句 insertIntoStep.params是占位符对应的参数
        //例如：insertIntoStep.sql = insert into user (id,name,age) values (?,?,?) insertIntoStep.params = [1, test, 18]
        //最终返回的sql语句为：insert into user (id,name,age) values (1, 'test', 18);
        val finalSql = "${insertIntoStep.getSQL(ParamType.INLINED)};"
        if (active != EnvEnum.PROD.env) {
            StaticLog.info("插入语句：${finalSql}")
        }
        return finalSql
    }

    /**
     * 封装更新语句 默认主键为id
     * @param data 数据对象
     * @param where 条件
     * @param isNll 是否更新空值
     */
    private fun buildUpdateSql(data: Any, where: Condition, isNll: Boolean = false): String {
        //获取类对象
        val clazz = data::class.java
        //获取类的属性
        val fields = clazz.declaredFields
        //获取类的属性名
        val fileKeyValue = mutableMapOf<String, Any?>()
        for (field in fields) {
            field.isAccessible = true
            val name = field.name.underlineName()
            val value = field.get(data)
            //如果属性值不为空，则添加到map中,忽略主键
            if (value != null && name != "id") {
                fileKeyValue[name] = value
            } else if (isNll) {
                fileKeyValue[name] = null
            }
        }
        val updateStep = dslContext.update(DSL.table(getTableName(clazz))).set(fileKeyValue).where(where)
        val finalSql = "${updateStep.getSQL(ParamType.INLINED)};"
        if (active != EnvEnum.PROD.env) {
            StaticLog.info("更新语句：${finalSql}")
        }
        return finalSql
    }


    /**
     * 封装查询语句
     * @param clazz 类对象
     * @param where 条件
     * @param columns 查询的列 默认为*
     * @param lastSql 最后的sql语句 例如：order by id desc limit 1
     */
    private fun <T> buildSelectSql(
        clazz: Class<T>, where: Condition, columns: List<String> = listOf(), lastSql: String = ""
    ): String {
        var fields = clazz.declaredFields.map { DSL.field(it.name.underlineName()) }
        //如果查询的列不为空，则使用查询的列
        if (columns.isNotEmpty()) {
            fields = columns.map { DSL.field(it.underlineName()) }
        }
        val selectStep = dslContext.select(fields).from(DSL.table(getTableName(clazz))).where(where)
        var finalSql = selectStep.getSQL(ParamType.INLINED)
        if (lastSql.isNotBlank()) {
            finalSql += " $lastSql"
        }
        finalSql += ";"
        if (active != EnvEnum.PROD.env) {
            StaticLog.info("查询语句：${finalSql}")
        }
        return finalSql
    }

    /**
     * 封装删除语句
     * @param clazz 类对象
     * @param where 条件
     */
    private fun <T> buildDeleteSql(clazz: Class<T>, where: Condition): String {
        val deleteStep = dslContext.delete(DSL.table(getTableName(clazz))).where(where)
        val finalSql = "${deleteStep.getSQL(ParamType.INLINED)};"
        if (active != EnvEnum.PROD.env) {
            StaticLog.info("删除语句：${finalSql}")
        }
        return "$finalSql;"
    }
}