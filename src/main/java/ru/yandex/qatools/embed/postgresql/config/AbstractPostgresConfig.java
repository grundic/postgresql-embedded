package ru.yandex.qatools.embed.postgresql.config;

import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;
import de.flapdoodle.embed.process.io.file.Files;
import ru.yandex.qatools.embed.postgresql.Command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.flapdoodle.embed.process.runtime.Network.getFreeServerPort;
import static de.flapdoodle.embed.process.runtime.Network.getLocalHost;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Common postgres config
 */
public abstract class AbstractPostgresConfig<C extends AbstractPostgresConfig> extends ExecutableProcessConfig {

    private final Storage storage;
    private final Locale locale;
    protected final Net network;
    protected final Timeout timeout;
    protected final Credentials credentials;
    protected List<String> args = new ArrayList<>();

    protected AbstractPostgresConfig(AbstractPostgresConfig config) {
        this(config.version, config.net(), config.storage, config.locale, config.timeout(), config.credentials);
    }

    public AbstractPostgresConfig(IVersion version, Net network, Storage storage, Locale locale, Timeout timeout, Credentials cred) {
        super(version, new SupportConfig(Command.Postgres));
        this.network = network;
        this.timeout = timeout;
        this.storage = storage;
        this.locale = locale;
        this.credentials = cred;
    }

    public AbstractPostgresConfig(IVersion version, Net network, Storage storage, Locale locale, Timeout timeout) {
        this(version, network, storage, locale, timeout, null);
    }

    public AbstractPostgresConfig(IVersion version, Net network, Storage storage, Timeout timeout) {
        this(version, network, storage, new Locale(), timeout, null);
    }

    public AbstractPostgresConfig(IVersion version, Net network, Storage storage, Timeout timeout, Credentials cred) {
        this(version, network, storage, new Locale(), timeout, cred);
    }

    public Net net() {
        return network;
    }

    public Timeout timeout() {
        return timeout;
    }

    public Storage storage() {
        return storage;
    }

    public Locale locale() {
        return locale;
    }

    public Credentials credentials() {
        return credentials;
    }

    public List<String> args() {
        return args;
    }

    public C withArgs(String... args) {
        args().addAll(asList(args));
        return (C) this;
    }


    public static class Storage {
        private final File dbDir;
        private final String dbName;
        private final boolean isTmpDir;

        public Storage(String dbName) throws IOException {
            this(dbName, null);
        }

        public Storage(String dbName, String databaseDir) throws IOException {
            this.dbName = dbName;
            if (isEmpty(databaseDir)) {
                isTmpDir = true;
                dbDir = Files.createTempDir(PropertyOrPlatformTempDir.defaultInstance(), "embedpostgres-db");
            } else {
                dbDir = Files.createOrCheckDir(databaseDir);
                isTmpDir = false;
            }
        }

        public File dbDir() {
            return dbDir;
        }

        public boolean isTmpDir() {
            return isTmpDir;
        }

        public String dbName() {
            return dbName;
        }
    }

    public static class Locale {
        private final String encoding;
        private final String locale;
        private final String lcCollate;
        private final String lcCtype;
        private final String lcMessages;
        private final String lcMonetary;
        private final String lcNumeric;
        private final String lcTime;
        private final boolean noLocale;

        public Locale(String encoding, String locale, String lcCollate, String lcCtype, String lcMessages, String lcMonetary, String lcNumeric, String lcTime, boolean noLocale) {
            this.encoding = encoding;
            this.locale = locale;
            this.lcCollate = lcCollate;
            this.lcCtype = lcCtype;
            this.lcMessages = lcMessages;
            this.lcMonetary = lcMonetary;
            this.lcNumeric = lcNumeric;
            this.lcTime = lcTime;
            this.noLocale = noLocale;
        }

        public Locale() {
            this(null, null, null, null, null, null, null, null, false);
        }

        public String getEncoding() {
            return encoding;
        }

        public String getLocale() {
            return locale;
        }

        public String getLcCollate() {
            return lcCollate;
        }

        public String getLcCtype() {
            return lcCtype;
        }

        public String getLcMessages() {
            return lcMessages;
        }

        public String getLcMonetary() {
            return lcMonetary;
        }

        public String getLcNumeric() {
            return lcNumeric;
        }

        public String getLcTime() {
            return lcTime;
        }

        public boolean isNoLocale() {
            return noLocale;
        }

        public List<String> buildCommandLine() {
            List<String> cmd = new ArrayList<>();
            if (encoding != null) {
                cmd.add("--encoding=" + encoding);
            }
            if (locale != null) {
                cmd.add("--locale=" + locale);
            }
            if (lcCollate != null) {
                cmd.add("--lc-collate=" + lcCollate);
            }
            if (lcCtype != null) {
                cmd.add("--lc-ctype=" + lcCtype);
            }
            if (lcMessages != null) {
                cmd.add("--lc-messages=" + lcMessages);
            }
            if (lcMonetary != null) {
                cmd.add("--lc-monetary=" + lcMonetary);
            }
            if (lcNumeric != null) {
                cmd.add("--lc-numeric=" + lcNumeric);
            }
            if (lcTime != null) {
                cmd.add("--lc-time=" + lcTime);
            }
            if (noLocale) {
                cmd.add("--no-locale");
            }

            return cmd;
        }
    }

    public static class Credentials {

        private final String username;
        private final String password;


        public Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String username() {
            return username;
        }

        public String password() {
            return password;
        }
    }

    public static class Net {

        private final String host;
        private final int port;

        public Net() throws IOException {
            this(getLocalHost().getHostAddress(), getFreeServerPort());
        }

        public Net(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public int port() {
            return port;
        }

        public String host() {
            return host;
        }
    }

    public static class Timeout {

        private final long startupTimeout;

        public Timeout() {
            this(2000);
        }

        public Timeout(long startupTimeout) {
            this.startupTimeout = startupTimeout;
        }

        public long startupTimeout() {
            return startupTimeout;
        }
    }

}