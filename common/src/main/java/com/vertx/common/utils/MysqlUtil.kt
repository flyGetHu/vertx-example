package com.vertx.common.utils

import cn.hutool.log.StaticLog
import com.vertx.common.config.active
import org.jooq.Condition
import org.jooq.SQLDialect
import org.jooq.conf.ParamType
import org.jooq.conf.Settings
import org.jooq.impl.DSL


/**
 * mysql工具类
 */
object MysqlUtil {
    //jooq上下文
    private val dslContext = DSL.using(SQLDialect.MYSQL, Settings())


    /**
     * 封装插入语句
     * @param data 数据对象
     */
    fun buildInsertSql(data: Any): String {
        //获取类对象
        val clazz = data::class.java
        //获取类的属性
        val fields = clazz.declaredFields
        //获取类的属性名
        val fileKeyValue = mutableMapOf<String, Any?>()
        for (field in fields) {
            field.isAccessible = true
            val name = underlineName(field.name)
            val value = field.get(data)
            //如果属性值不为空，则添加到map中
            if (value != null) {
                fileKeyValue[name] = value
            }
        }
        val insertIntoStep = dslContext.insertInto(DSL.table(underlineName(clazz.simpleName)),
            fileKeyValue.keys.toList().map { DSL.field(it) }).values(fileKeyValue.values)
        //返回组装好的sql语句,insertIntoStep.sql是带占位符的sql语句 insertIntoStep.params是占位符对应的参数
        //例如：insertIntoStep.sql = insert into user (id,name,age) values (?,?,?) insertIntoStep.params = [1, test, 18]
        //最终返回的sql语句为：insert into user (id,name,age) values (1, 'test', 18);
        val finalSql = insertIntoStep.getSQL(ParamType.INLINED)
        if (active != "prod") {
            StaticLog.debug("插入语句：${finalSql}")
        }
        return "$finalSql;"
    }

    /**
     * 封装更新语句 默认主键为id
     * @param data 数据对象
     * @param where 条件
     * @param isNll 是否更新空值
     */
    fun buildUpdateSql(data: Any, where: Condition, isNll: Boolean = false): String {
        //获取类对象
        val clazz = data::class.java
        //获取类的属性
        val fields = clazz.declaredFields
        //获取类的属性名
        val fileKeyValue = mutableMapOf<String, Any?>()
        for (field in fields) {
            field.isAccessible = true
            val name = underlineName(field.name)
            val value = field.get(data)
            //如果属性值不为空，则添加到map中,忽略主键
            if (value != null && name != "id") {
                fileKeyValue[name] = value
            } else if (isNll) {
                fileKeyValue[name] = null
            }
        }
        val updateStep = dslContext.update(DSL.table(underlineName(clazz.simpleName))).set(fileKeyValue).where(where)
        val finalSql = updateStep.getSQL(ParamType.INLINED)
        if (active != "prod") {
            StaticLog.debug("更新语句：${finalSql}")
        }
        return "$finalSql;"
    }


    /**
     * 封装查询语句
     * @param clazz 类对象
     * @param where 条件
     * @param columns 查询的列 默认为*
     * @param lastSql 最后的sql语句 例如：order by id desc limit 1
     */
    fun <T> buildSelectSql(
        clazz: Class<T>, where: Condition, columns: List<String> = listOf(), lastSql: String = ""
    ): String {
        var fields = clazz.declaredFields.map { DSL.field(underlineName(it.name)) }
        //如果查询的列不为空，则使用查询的列
        if (columns.isNotEmpty()) {
            fields = columns.map { DSL.field(underlineName(it)) }
        }
        val selectStep = dslContext.select(fields).from(DSL.table(underlineName(clazz.simpleName))).where(where)
        var finalSql = selectStep.getSQL(ParamType.INLINED)
        if (lastSql.isNotBlank()) {
            finalSql += " $lastSql"
        }
        if (active != "prod") {
            StaticLog.debug("查询语句：${finalSql}")
        }
        return "$finalSql;"
    }

    /**
     * 封装删除语句
     * @param clazz 类对象
     * @param where 条件
     */
    fun <T> buildDeleteSql(clazz: Class<T>, where: Condition): String {
        val deleteStep = dslContext.delete(DSL.table(underlineName(clazz.simpleName))).where(where)
        val finalSql = deleteStep.getSQL(ParamType.INLINED)
        if (active != "prod") {
            StaticLog.debug("删除语句：${finalSql}")
        }
        return "$finalSql;"
    }
}