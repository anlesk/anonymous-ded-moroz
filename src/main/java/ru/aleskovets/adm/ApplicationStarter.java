package ru.aleskovets.adm;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.skuptsov.telegram.bot.platform.config.BotPlatformStarter;

/**
 * Created by ad on 10/29/2016.
 */
@Configuration
@ComponentScan(value = "ru.aleskovets.adm.telegram")
public class ApplicationStarter {
    public static void main(String[] args) {
        BotPlatformStarter.start(ApplicationStarter.class, args);
    }
}
