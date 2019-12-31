# Email
Rudimentary offers a very nice and simple way to send emails.

## Configure SMTP
To send emails you need to configure session pool and SMTP connection. Session pool is needed to avoid connection creation on the fly which makes sending email a little bit faster.
```properties
# Configure session pool
email.smtp.enabled=true # Enable SMTP
email.smtp.pool.minSize=25 # Minimim number of javax.mail.Session in the pool
email.smtp.pool.maxSize=50 # Maximum number of javax.mail.Session in the pool
email.smtp.pool.validationInterval=30 # Time between two checks of pool status in seconds
email.smtp.pool.awaitTerminationInterval=15 # Time to wait for tasks to finish before termination in seconds

# Configure SMTP server connection
email.smtp.user= # username
email.smtp.password= # password
email.smtp.properties.*= # Check possible * values at https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html
```

## Send email
```java
Email.send((message) -> {
    message.setFrom(new InternetAddress("dummy@yeti-it.hr"));
    message.setSubject("Rudimentary email", StandardCharsets.UTF_8.name());
    message.setText("Hello!", StandardCharsets.UTF_8.name());
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("incognito@yeti-it.hr", false));
});
```

