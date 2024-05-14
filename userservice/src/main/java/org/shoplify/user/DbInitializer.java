package org.shoplify.user;

import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

@Service
public class DbInitializer {

    private static final Logger logger = Logger.getLogger(DbInitializer.class.getName());

    @Autowired
    DataSource dataSource;

    @PostConstruct
    @Transactional
    public void init() throws SQLException, IOException {
        logger.info("DB Initializing...");
        Connection connection = dataSource.getConnection();
        Resource resource = new ClassPathResource("populate_db.sql");
        RunScript.execute(connection, new InputStreamReader(resource.getInputStream()));
        connection.commit(); // Commit the transaction
    }


}
