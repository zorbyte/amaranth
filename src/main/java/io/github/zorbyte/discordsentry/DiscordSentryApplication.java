package io.github.zorbyte.discordsentry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

@SpringBootApplication
public class DiscordSentryApplication {
	private static final Logger log = LoggerFactory.getLogger(DiscordSentryApplication.class);

	public static void main(String[] args) {
		try {
			log.debug("Loading .env configuration(s)...");

			Dotenv
					.configure()
					.systemProperties()
					.load();
		} catch (DotenvException e) {
			// Shouldn't crash the app, Spring has many other ways for us to attempt the
			// correct configs.
			log.debug("An exception occurred while loading .env configuration(s): ", e);
		}

		new SpringApplicationBuilder(DiscordSentryApplication.class)
				.bannerMode(Banner.Mode.OFF)
				.build()
				.run(args);
	}
}
