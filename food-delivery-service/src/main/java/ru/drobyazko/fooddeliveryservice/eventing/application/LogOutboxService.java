package ru.drobyazko.fooddeliveryservice.eventing.application;

import jakarta.annotation.PreDestroy;
import org.postgresql.PGProperty;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.drobyazko.fooddeliveryservice.eventing.infrastructure.EventEntity;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

// Experimental
/*
@Component
public class LogOutboxService implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(LogOutboxService.class);
    // TODO: do not forget to close
    private PgConnection pgConnection;
    private PGReplicationStream stream;
    private final Environment environment;
    private final KafkaSenderService kafkaSenderService;
    private final ExecutorService taskExecutor;
    private Future task;

    @Autowired
    public LogOutboxService(Environment environment, KafkaSenderService kafkaSenderService) {
        this.environment = environment;
        this.kafkaSenderService = kafkaSenderService;
        this.taskExecutor = Executors.newSingleThreadExecutor();
    }

    private void readFromStream() throws SQLException{
        while (!Thread.currentThread().isInterrupted()) {
            ByteBuffer msg = stream.readPending();
            if (msg == null) {
                try {
                    Thread.sleep(1000L);
                    continue;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            EventEntity eventEntity = null;
            char c = (char) msg.get();
            if (c == 'I') {
                int relationId = msg.getInt();
                char newTupleByte = (char) msg.get();
                byte[][] tupleData = readTupleData(msg);
                // TODO: do we need eventEntity? maybe deserialize payload only
                eventEntity = deserializeEventEntity(tupleData);
                // TODO: now we need to deserialize this to an actual event class (OrderCreatedMessage for example)
                //  using json and send it into amqp. Maybe depending on the type of event we can send it to different external systems
            }

            // here we try to send to kafka and if we fail we need to retry somehow
            if (eventEntity != null) {
                kafkaSenderService.send(eventEntity.getPayload());
            }

            LogSequenceNumber lastReceivedLsn = stream.getLastReceiveLSN();
            stream.setAppliedLSN(lastReceivedLsn);
            stream.setFlushedLSN(lastReceivedLsn);
        }
    }

    private byte[][] readTupleData(ByteBuffer msg) {
        short numberOfColumns = msg.getShort();
        short currentColumn = 0;
        byte[][] tupleData = new byte[numberOfColumns][];
        while (msg.hasRemaining()) {
            char valueIdentifier = (char) msg.get(); // n or u or t
            if (valueIdentifier == 't') {
                int valueLength = msg.getInt();
                byte[] value = new byte[valueLength];
                msg.get(value);
                tupleData[currentColumn] = value;
                ++currentColumn;
            }
        }
        return tupleData;
    }

    private EventEntity deserializeEventEntity(byte[][] tupleData) {
        Long id = Long.parseLong(new String(tupleData[0]));
        String type = new String(tupleData[1]);
        String payload = new String(tupleData[2]);
        return new EventEntity(id, type, payload);
    }

    @Override
    public void start() {
        try {
            initializeReplicationStream();
            task = taskExecutor.submit(() -> {
                try {
                    readFromStream();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeReplicationStream() throws SQLException {
        String url = environment.getProperty("spring.datasource.url");
        Properties properties = new Properties();
        PGProperty.USER.set(properties, environment.getProperty("spring.datasource.username"));
        PGProperty.PASSWORD.set(properties, environment.getProperty("spring.datasource.password"));
        PGProperty.REPLICATION.set(properties, "database");
        PGProperty.ASSUME_MIN_SERVER_VERSION.set(properties, "9.4");
        PGProperty.PREFER_QUERY_MODE.set(properties, "simple");

        Connection connection = DriverManager.getConnection(url, properties);
        pgConnection = connection.unwrap(PgConnection.class);

        stream = pgConnection.getReplicationAPI()
                .replicationStream()
                .logical()
                .withSlotName("event_slot")
                .withSlotOption("proto_version", 4)
                .withSlotOption("publication_names", "outbox")
                .withStatusInterval(20, TimeUnit.SECONDS)
                .start();
    }

    @Override
    public void stop() {
        task.cancel(true);
    }

    @PreDestroy
    public void shutdown() {
        try {
            shutdownReplicationStream();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdownReplicationStream() throws SQLException {
        pgConnection.close();
    }

    @Override
    public boolean isRunning() {
        try {
            return pgConnection != null && !pgConnection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
*/
