package name.bychkov.junit5;

import java.util.Objects;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.auth.LoginFailedException;
import org.subethamail.smtp.auth.PlainAuthenticationHandlerFactory;
import org.subethamail.smtp.auth.UsernamePasswordValidator;
import org.subethamail.smtp.server.SMTPServer;
import org.subethamail.wiser.Wiser;

abstract class AbstractFakeSmtpJUnitExtension implements BeforeAllCallback, AfterAllCallback, AfterEachCallback
{
	protected static Wiser server;
	private int port = 25;
	private String host;
	private String user;
	private String password;
	
	protected AbstractFakeSmtpJUnitExtension port(int port)
	{
		this.port = port;
		return this;
	}
	
	protected AbstractFakeSmtpJUnitExtension host(String host)
	{
		this.host = host;
		return this;
	}
	
	protected AbstractFakeSmtpJUnitExtension user(String user)
	{
		this.user = user;
		return this;
	}
	
	protected AbstractFakeSmtpJUnitExtension password(String password)
	{
		this.password = password;
		return this;
	}
	
	@Override
	public void afterEach(ExtensionContext context) throws Exception
	{
		server.getMessages().clear();
	}
	
	@Override
	public void afterAll(ExtensionContext context) throws Exception
	{
		server.stop();
	}
	
	@Override
	public void beforeAll(ExtensionContext context) throws Exception
	{
		SMTPServer.Builder serverBuilder = new SMTPServer.Builder();
		serverBuilder.port(port);
		if (host != null)
		{
			serverBuilder.hostName(host);
		}
		if (user != null && password != null)
		{
			serverBuilder.authenticationHandlerFactory(new PlainAuthenticationHandlerFactory(new UsernamePasswordValidator()
			{
				@Override
				public void login(String user, String password, MessageContext context) throws LoginFailedException
				{
					if (!Objects.equals(AbstractFakeSmtpJUnitExtension.this.user, user)
							|| !Objects.equals(AbstractFakeSmtpJUnitExtension.this.password, password))
					{
						throw new LoginFailedException();
					}
				}
			}));
		}
		server = Wiser.create(serverBuilder);
		server.start();
	}
}