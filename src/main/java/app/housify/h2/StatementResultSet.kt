package app.housify.h2

import java.sql.ResultSet
import java.sql.Statement

/**
 * Util class for autoclosing statement and resultset objects
 */
class StatementResultSet(
        val statement: Statement,
        val resultSet: ResultSet
) : AutoCloseable {
    override fun close() {
        resultSet.close()
        statement.close()
    }
}