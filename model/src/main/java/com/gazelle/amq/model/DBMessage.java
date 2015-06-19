package com.gazelle.amq.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Describe a db message bridge message
 */
public class DBMessage implements RowMapper<DBMessage> {

    protected Long id;
    protected String destination;
    protected String message;

    protected DBMessage() {}

    public DBMessage(String destination, String message) {
        this.destination = destination;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public DBMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
        DBMessage result = new DBMessage();
        result.message = rs.getString("message");
        result.destination = rs.getString("destination");
        result.id = rs.getLong("id");
        return result;
    }
}
