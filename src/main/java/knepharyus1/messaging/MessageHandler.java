package knepharyus1.messaging;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class MessageHandler {

  private static final String CONFIGPATH = "META-INF/MessageHandlerConfig.xml";
  private static Logger logger = Logger.getLogger(MessageHandler.class);

  private String host;
  private String exchange;
  private String username;
  private Connection conn;
  private Channel channel;
  private QueueingConsumer consumer;

  public MessageHandler() throws ConfigurationException, IOException {
    this(CONFIGPATH);
  }

  public MessageHandler(String configFile) throws ConfigurationException, IOException {
    super();

    XMLConfiguration xmlConf = new XMLConfiguration();
    xmlConf.load(configFile);

    host = xmlConf.getProperty("host").toString();
    exchange = xmlConf.getProperty("exchange").toString();
    username = xmlConf.getProperty("username").toString();

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    factory.setUsername(username);
    factory.setPassword(username);

    conn = factory.newConnection();
    channel = conn.createChannel();

  }

  public void publish(String message, String routingKey) throws IOException {
    channel.basicPublish(exchange, routingKey, null, message.getBytes());
  }

  public void publishWithProperties(String message, Map<String, Object> props, String routingKey) {
    Builder builder = new Builder();
    builder.headers(props);
    BasicProperties basicProps = builder.build();
    for (String key : basicProps.getHeaders.keySet()) {

    }
    channel.basicPublish(exchange, routingKey, basicProps, message.getBytes());
  }

  public void consume(String queue) throws IOException {
    this.basicConsume(queue);
  }

  public void close() throws IOException {
    conn.close();
  }

  public QueueingConsumer getConsumer() {
    return consumer;
  }

  public void basicConsume(String queue) throws IOException {
    consumer = new QueueingConsumer(channel);
    channel.basicConsume(queue, true, consumer);
  }

}
