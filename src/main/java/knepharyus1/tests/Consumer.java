package knepharyus1.tests;

import messaging.MessageHandler;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.ArrayUtils;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class Consumer {

  public static void main(String[] args)
  throws ConfigurationException, IOException, ConsumerCancelledException, InterruptedException,
  KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException {

    // MessageHandler handler = new MessageHandler();
    SslMessageHandler handler;
    String[] arguments = args;
    String opt = args[0];

    File configFile = new File(opt);

    if (configFile.exists()) {
      handler = new SslMessageHandler(opt);
      arguments = (String[]) ArrayUtils.remove(args, 0);
    } else {
      handler = new SslMessageHandler();
    }

    try {

      handler.consume(arguments[0]);

      System.out.println(" [*] Waiting for messages SSL. To exit press Ctrl+C");

      int count = 0;
      QueueingConsumer.Delivery delivery = null;
      while (true) {
        try {
          delivery = handler.getConsumer().nextDelivery();
        } catch (Exception e) {
          if (e instanceof ShutdownSignalException) {
            System.out.println("ERROR: Message not delivered; Shutdown signal exception thrown, retrying...");
            handler.consume(arguments[0]);
          } else {
            System.out.println("Exception...");
            e.printStackTrace();
            handler.close();
          }
        }

        String message = new String(delivery.getBody());
        String key = delivery.getEnvelope().getRoutingKey();
        System.out.println(" [x] Received '" + key + "':'" + message + "' TOTAL: " + ++count);
      }

    } catch (IOException iox) {

      if (iox.getCause() instanceof ShutdownSignalException) {

      } else {

      }

      handler.close();
    }
  }
}
