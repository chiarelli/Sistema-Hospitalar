package com.chiarelli.hospitalsystem.identityAndAccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.chiarelli.hospitalsystem.identityAndAccess.config.PostgresDataSourceConfig;

@SpringBootTest
public class PostgresConnectionTests {

    private DataSource postgresDataSource;
    
    @Autowired
    public PostgresConnectionTests(PostgresDataSourceConfig config) {
        postgresDataSource = config.postgresDataSource();
    }

    @Test
    public void testDataSource() {
        assertNotNull(postgresDataSource);
    }

   @Test
    public void testCreateAndQueryTable() {
        try (Connection connection = postgresDataSource.getConnection()) {
            assertNotNull(connection);

            String createTableQuery = "CREATE TABLE usersTest (id SERIAL, name VARCHAR(255))";
            try (PreparedStatement createStatement = connection.prepareStatement(createTableQuery)) {
                createStatement.execute();
            }

            String insertQuery = "INSERT INTO usersTest (name) VALUES (?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, "John Doe");
                insertStatement.executeUpdate();
            }

            String selectQuery = "SELECT name FROM usersTest WHERE id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setInt(1, 1);
                ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    assertEquals("John Doe", name);
                } else {
                    throw new SQLException("No data found");
                }
            }

            String dropQuery = "DROP TABLE usersTest";
            try (PreparedStatement dropStatement = connection.prepareStatement(dropQuery)) {
                dropStatement.execute();
            }

        } catch (SQLException e) {
            assertNull(e);
        }
    }
    
}
