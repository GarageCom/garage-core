package com.garage.core.repository;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.garage.core.config.CassandraConfig;
import com.garage.core.entity.UserEntity;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.shaded.com.google.common.collect.ImmutableSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.cql.CqlIdentifier;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class UserRepositoryIntegrationTest {

    public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS garage_test WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";
    public static final String KEYSPACE_ACTIVATE_QUERY = "USE garage_test;";
    public static final String DATA_TABLE_NAME = "users";

    @Autowired
    private CassandraAdminOperations adminTemplate;

    @Autowired
    UserRepository userRepository;

    @BeforeClass
    public static void startCassandraEmbedded() throws InterruptedException, TTransportException, ConfigurationException, IOException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        final Cluster cluster = Cluster.builder().addContactPoints("127.0.0.1").withPort(9142).build();
        final Session session = cluster.connect();
        session.execute(KEYSPACE_CREATION_QUERY);
        session.execute(KEYSPACE_ACTIVATE_QUERY);
    }

    @AfterClass
    public static void stopCassandraEmbedded() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Before
    public void createTable() {
        adminTemplate.createTable(false, CqlIdentifier.cqlId(DATA_TABLE_NAME), UserEntity.class, new HashMap<String, Object>());
    }

    @After
    public void dropTable() {
        adminTemplate.dropTable(CqlIdentifier.cqlId(DATA_TABLE_NAME));
    }

    @Test
    public void whenSavingUser_thenAvailableOnRetrieval() {
        final UserEntity user = new UserEntity(UUIDs.timeBased(), "testUser", "qwerty11");
        userRepository.save(ImmutableSet.of(user));
        final Iterable<UserEntity> users = userRepository.findByUserName("testUser");
        assertEquals(user.getId(), users.iterator().next().getId());
    }
}
