
package org.apache.tika.tach3Securite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


// Concaténation de username à la requete SQL peut modifier struc requete
public class UnsafeSQL {

    public ResultSet findUser(Connection connection, String username)
            throws SQLException {

        String query = "SELECT * FROM users WHERE username = '"
                + username + "'";

        Statement statement = connection.createStatement();

        return statement.executeQuery(query);
    }
}
