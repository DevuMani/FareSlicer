package com.example.dream.fareslicer.SupportClasses;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class PinMailSender extends AsyncTask<String,Void,Boolean> {


    private final String username = "testingfirebase1@gmail.com";
    private final String password = "Test@1234";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected Boolean doInBackground(String... strings) {

        String user_email=strings[1];
        String pin=strings[2];

        Log.d("Mail", "asyncTask started");
        Log.d("Mail id", user_email);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(user_email));
            message.setSubject("Reply: Forgot Password");
            message.setText("Dear,"
                    + "\n\n You can use "+pin+" as temporary pin to set new password!!!"
                    +  "\n\n Warning:"
                    +  "\n\n Do not share this pin with any one");


            Transport.send(message);

            System.out.println("Done");
            Log.d("Mail", "done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean==true)
        {
            Log.d("Mail", "send");
        }

    }


}
