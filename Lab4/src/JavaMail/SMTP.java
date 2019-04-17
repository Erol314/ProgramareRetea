package JavaMail;


import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SMTP {
    public static void main(String[] args) {
        // Recipient's email ID needs to be mentioned.
        String to = "sezghin314@gmail.com";

        // Sender's email ID needs to be mentioned // useless
        String from = "fromemail@gmail.com";

        final String username = "SMTPandPOP3";//change accordingly
        final String password = "SuperPass321.";//change accordingly

        // Assuming you are sending email through relay.jangosmtp.net
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("Testing Subject");

            // Create a multipart message
//            Multipart multipart = new MimeMultipart();
            MimeMultipart multipart = new MimeMultipart("related");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText = "<H1>Hello</H1><img src=\"cid:image\">";
            messageBodyPart.setContent(htmlText, "text/html");

            multipart.addBodyPart(messageBodyPart);
            // add it
            // Now set the actual message
//            messageBodyPart.setText("This is message body");

            // Set text message part
//            multipart.addBodyPart(messageBodyPart);

            // first part (the html)
//            BodyPart messageBodyPart = new MimeBodyPart();
//            String htmlText = "<H1>Hello</H1><img src=\"cid:image\">";
//            messageBodyPart.setContent(htmlText, "text/html");
//             add it
//            multipart.addBodyPart(messageBodyPart);

            // second part (the image)
            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource(
                    "C:\\Users\\ionsam14\\Desktop\\Anul_3\\Semestru 2\\PR\\Lab4\\src\\JavaMail\\picolas.jpg");

            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");

            // add image to the multipart
            multipart.addBodyPart(messageBodyPart);

            // Part thee is attachment
            messageBodyPart = new MimeBodyPart();
            String filename = "C:\\Users\\ionsam14\\Desktop\\Anul_3\\Semestru 2\\PR\\Lab4\\src\\JavaMail\\TestDocument.txt";
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}