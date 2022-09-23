package name.bychkov.junit5;

import java.util.ArrayList;
import java.util.List;

import org.subethamail.wiser.WiserMessage;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public class FakeSmtpJUnitExtension extends AbstractFakeSmtpJUnitExtension
{
	public FakeSmtpJUnitExtension port(int port)
	{
		super.port(port);
		return this;
	}
	
	public FakeSmtpJUnitExtension host(String host)
	{
		super.host(host);
		return this;
	}
	
	public FakeSmtpJUnitExtension user(String user)
	{
		super.user(user);
		return this;
	}
	
	public FakeSmtpJUnitExtension password(String password)
	{
		super.password(password);
		return this;
	}
	
	public List<MimeMessage> getMessages() throws MessagingException
	{
		List<MimeMessage> messages = new ArrayList<>(server.getMessages().size());
		for (WiserMessage wMessage : server.getMessages())
		{
			messages.add(wMessage.getMimeMessage());
		}
		return messages;
	}
}