package JavaMail;

import java.util.Properties;
import javax.mail.*;
import com.sun.mail.pop3.POP3SSLStore;
import javax.mail.internet.*;
import java.util.Date;

public class POP3 {

    static String indentStr = "                                               ";
    static int level = 0;

    public static void pr(String s) {

        System.out.print(indentStr.substring(0, level * 2));
        System.out.println(s);
    }

    public static void dumpEnvelope(Message m) throws Exception {
        pr(" ");
        Address[] a;
        // FROM
        if ((a = m.getFrom()) != null) {
            for (int j = 0; j < a.length; j++)
                pr("FROM: " + a[j].toString());
        }

        // TO
        if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
            for (int j = 0; j < a.length; j++) {
                pr("TO: " + a[j].toString());
            }
        }

        // SUBJECT
        pr("SUBJECT: " + m.getSubject());

        // DATE
        Date d = m.getSentDate();
        pr("SendDate: " +
                (d != null ? d.toString() : "UNKNOWN"));


    }

    public static void dumpPart(Part p) throws Exception {
        if (p instanceof Message)
            dumpEnvelope((Message)p);

        String ct = p.getContentType();
        try {
            pr("CONTENT-TYPE: " + (new ContentType(ct)).toString());
        } catch (ParseException pex) {
            pr("BAD CONTENT-TYPE: " + ct);
        }

        /*
         * Using isMimeType to determine the content type avoids
         * fetching the actual content data until we need it.
         */
        if (p.isMimeType("text/plain")) {
            pr("This is plain text");
            pr("---------------------------");
            System.out.println((String)p.getContent());
        } else {

            // just a separator
            pr("---------------------------");

        }
    }

    public static void main(String args[]) throws Exception {

        // mail server connection parameters
        String host = "pop.gmail.com";
        String user = "SMTPandPOP3";
        String password = "SuperPass321.";

        // connect to my pop3 inbox
        Properties props = new Properties();
        props.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.pop3.socketFactory.port", "995");
        props.put("mail.pop3.port", "888");
        props.put("mail.pop3.host", host);
        props.put("mail.pop3.user", user);
        props.put("mail.store.protocol", "pop3");




        URLName url = new URLName("pop3", "pop.gmail.com", 995, "",
                user, password);

        Session session = Session.getInstance(props, null);
        Store store = new POP3SSLStore(session, url);
        store.connect();

        Folder inbox = store.getFolder("Inbox");
        inbox.open(Folder.READ_ONLY);

        // get the list of inbox messages
        Message[] messages = inbox.getMessages();

        // Use a suitable FetchProfile
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        inbox.fetch(messages, fp);

        for (int i = 0; i < messages.length; i++) {
            System.out.println("--------------------------");
            System.out.println("MESSAGE #" + (i + 1) + ":");
            dumpPart(messages[i]);
        }


//        if (messages.length == 0) System.out.println("No messages found.");
//
//        for (int i = 0; i < messages.length; i++) {
//            // stop after listing ten messages
//            if (i > 10) {
//                System.exit(0);
//                inbox.close(true);
//                store.close();
//            }
//
//            System.out.println("Message " + (i + 1));
//            System.out.println("From : " + messages[i].getFrom()[0]);
//            System.out.println("Subject : " + messages[i].getSubject());
//            System.out.println("Sent Date : " + messages[i].getSentDate());
//            System.out.println();
//        }

        inbox.close(true);
        store.close();
    }
}
