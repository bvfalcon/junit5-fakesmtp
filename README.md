# JUnit5-FakeSMTP
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/name.bychkov/junit5-fakesmtp/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/name.bychkov/junit5-fakesmtp)

Fake SMTP server for testing with JUnit5

# Unit-testing with fake smtp-server

## Problem description

Suppose, your application sends emails to users with such or similar code:

```java
public class SendEmailService {

	public void sendMessage(String email, String subject, String body) throws MessagingException {
		Properties props = System.getProperties();
		props.put("mail.smtp.host", "localhost");
		props.put("mail.smtp.port", "25");
		Session session = Session.getInstance(props, null);
		
		Message simpleMail = new MimeMessage(session);
	
		simpleMail.setSubject(subject);
		simpleMail.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
	
		MimeMultipart mailContent = new MimeMultipart();
	
		MimeBodyPart mailMessage = new MimeBodyPart();
		mailMessage.setContent(body, "text/html; charset=utf-8");
		mailContent.addBodyPart(mailMessage);
	
		simpleMail.setContent(mailContent);
	
		Transport.send(simpleMail);
	}
}
```

You must be sure this functionality will work orrect in future and not break, while the code changes. How can you do this? Every time you can start simple smpt-server locally. After tests runned, see messages and prove it. For small projects this is only uncomfortable, for large - impossible.

## Solution

These actions can be performed automatically. Use in code of your unit-test special extension and smtp-server will start and stop automatically:

```java
	@RegisterExtension
	static FakeSmtpJUnitExtension fakeSmtp = new FakeSmtpJUnitExtension();
	
	@Test
	public void testSendMessage() {
		String expectedReceiver = "test-email-" + new Random().nextInt(Integer.MAX_VALUE) + "@example.com";
		String expectedSubject = "test-subject-" + new Random().nextInt(Integer.MAX_VALUE);
		
		try {
			SendEmailService testedService = new SendEmailService();
			testedService.sendMessage(expectedReceiver, expectedSubject, "text of body");
			
			Assertions.assertEquals(1, fakeSmtp.getMessages().size());
			MimeMessage actualMail = fakeSmtp.getMessages().iterator().next();
			Assertions.assertEquals(expectedReceiver, actualMail.getAllRecipients()[0].toString());
			Assertions.assertEquals(expectedSubject, actualMail.getSubject());
		} catch (MessagingException e) {
			Assertions.fail(e);
		}
	}
```

# Minimum requirements
- Java 8
- Maven 3.2.5
- JUnit 5.1

# Using in your project

Add in your pom.xml these modifications

```xml
<dependencies>
	...
	<!-- other dependencies -->
	<!-- JUnit 5 dependencies -->
	...
	<dependency>
		<groupId>name.bychkov</groupId>
		<artifactId>junit5-fakesmtp</artifactId>
		<version>1.0.0-RC1</version>
		<scope>test</scope>
	</dependency>
</dependencies>

<build>
	<plugins>
		...
		<plugin>
			<artifactId>maven-surefire-plugin</artifactId>
			<version>2.22.0</version>
		</plugin>
	</plugins>
</build>
```

Notes:

1) maven-surefire-plugin must have version >= 2.22.0

## JavaMail and Jakarta Mail

By default, implementation uses JavaMail realization (namespaces `javax.mail.`). If you use Jakarta Mail (namespaces `jakarta.mail.`), use dependency with classifier `jakarta`:

```xml
<dependency>
	<groupId>name.bychkov</groupId>
	<artifactId>junit5-fakesmtp</artifactId>
	<version>1.0.0-RC1</version>
	<classifier>jakarta</classifier>
	<scope>test</scope>
</dependency>
```

## More samples

You can see full examples of usage JUnit5-FakeSMTP with [JavaMail](./src/it/javamail/) and [Jakarta Mail](./src/it/jakartamail/).
