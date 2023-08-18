package kr.hqservice.framework.database.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kr.hqservice.framework.global.core.component.Bean
import kr.hqservice.framework.global.core.component.Configuration
import kr.hqservice.framework.global.core.util.AnsiColor
import kr.hqservice.framework.yaml.config.HQYamlConfiguration
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.io.IOException
import java.util.logging.Logger
import javax.sql.DataSource

@Configuration
class DatabaseConfig(
    private val config: HQYamlConfiguration,
    private val logger: Logger
) {
    @Bean
    fun provideDatabase(): Database {
        val type = config.getString("database.type")
        val dataSource = when(type.lowercase()) {
            "mysql" -> buildMySQLDataSource()
            "sqlite" -> buildSQLiteDataSource()
            else -> {
                logger.severe("$type datasource is not supported. using default SQLite datasource.")
                buildSQLiteDataSource()
            }
        }
        return Database.connect(dataSource)
    }

    private fun buildMySQLDataSource(): DataSource {
        val host = config.getString("database.mysql.host")
        val port = config.getInt("database.mysql.port")
        val user = config.getString("database.mysql.user")
        val password = config.getString("database.mysql.password")
        val database = config.getString("database.mysql.database")

        val hikariConfig = HikariConfig().apply {
            this.jdbcUrl = "jdbc:mysql://${host}:${port}/${database}?autoReconnect=true&allowMultiQueries=true"
            this.driverClassName = "com.mysql.cj.jdbc.Driver"
            this.username = user
            this.password = password
            this.connectionTestQuery = "SELECT 1"
            this.poolName = "hqframework"
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            addDataSourceProperty("useServerPrepStmts", "true")
            addDataSourceProperty("useLocalSessionState", "true")
            addDataSourceProperty("rewriteBatchedStatements", "true")
            addDataSourceProperty("cacheResultSetMetadata", "true")
            addDataSourceProperty("cacheServerConfiguration", "true")
            addDataSourceProperty("elideSetAutoCommits", "true")
            addDataSourceProperty("maintainTimeStats", "false")
            addDataSourceProperty("characterEncoding", "utf8")
            addDataSourceProperty("useUnicode", "true")
        }
        return HikariDataSource(hikariConfig).also {
            logger.info("${AnsiColor.CYAN}MySQL DataSource initialized.${AnsiColor.CYAN}")
        }
    }

    private fun buildSQLiteDataSource(): DataSource {
        val databasePath = config.getString("database.sqlite.path")
        val databaseFolder = File(databasePath.split("/").toMutableList().apply { removeLast() }.joinToString("/"))
        if (!databaseFolder.exists()) {
            databaseFolder.mkdirs()
        }
        val databaseFile = File(databasePath)
        try {
            databaseFile.createNewFile()
        } catch (e: IOException) {
            throw IOException("SQLite DataSource 파일을 생성하는 것을 실패하였습니다. 직접 ${databasePath} 경로에 파일을 생성하여주세요.", e)
        }
        val hikariConfig = HikariConfig().apply {
            this.jdbcUrl = "jdbc:sqlite:$databaseFile"
            this.connectionTestQuery = "SELECT 1"
            this.poolName = "hqframework"
        }
        return HikariDataSource(hikariConfig).also {
            logger.info("${AnsiColor.CYAN}SQLite DataSource initialized.${AnsiColor.CYAN}")
        }
    }
}