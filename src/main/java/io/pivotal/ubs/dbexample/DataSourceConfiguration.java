package io.pivotal.ubs.dbexample;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class DataSourceConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceConfiguration.class);

    @Value("${db.baseUrl}")
    private String baseUrl;

    @Value("${db.databaseName}")
    private String databaseName;

    @Value("${db.username}")
    private String username;

    @Value("${db.rootCert}")
    private String rootCert;

    @Value("${db.sslCert}")
    private String sslCert;

    @Value("${db.sslKey}")
    private String sslKey;

    private String url;
    private File rootCertFile;
    private File sslCertFile;
    private File sslKeyFile;

    @PostConstruct
    public void setUp() throws IOException {
        this.rootCertFile = decodeAndSave("root", ".crt", rootCert);
        this.sslCertFile = decodeAndSave("postgresql.crt", ".der", sslCert);
        this.sslKeyFile = decodeAndSave("postgresql.key", ".pk8", sslKey);

        StringBuilder builder = new StringBuilder();
        builder.append(baseUrl);
        builder.append(baseUrl.endsWith("/") ? "" : "/");
        builder.append(databaseName);
        //TODO sslmode should be 'verify-full' but trusted server certificate is needed
        builder.append("?ssl=true&sslfactory=org.postgresql.ssl.jdbc4.LibPQFactory&sslmode=verify-ca");
        builder.append("&sslrootcert=");
        builder.append(rootCertFile.getAbsolutePath());
        builder.append("&sslcert=");
        builder.append(sslCertFile.getAbsolutePath());
        builder.append("&sslkey=");
        builder.append(sslKeyFile.getAbsolutePath());
        this.url = builder.toString();

        LOG.debug("DataSource URL: " + this.url);
    }

    @PreDestroy
    public void tearDown() {
        this.rootCertFile.delete();
        this.sslCertFile.delete();
        this.sslKeyFile.delete();

        LOG.debug("Tmp certificate files cleared.");
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        DataSourceBuilder builder = DataSourceBuilder.create();
        builder.username(username);
        builder.url(url);
        return builder.build();
    }

    private File decodeAndSave(String filename, String suffix, String base64String) throws IOException {
        File file = File.createTempFile(filename, suffix);
        Files.write(Paths.get(file.getPath()), Base64.decodeBase64(base64String));

        return file;
    }

}
