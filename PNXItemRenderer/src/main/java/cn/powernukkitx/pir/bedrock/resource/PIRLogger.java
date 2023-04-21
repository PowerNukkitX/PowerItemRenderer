package cn.powernukkitx.pir.bedrock.resource;

import cn.nukkit.command.utils.CommandLogger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface PIRLogger {
    void info(String message);

    void warn(String message);

    @Contract(value = "!null -> new", pure = true)
    static @NotNull PIRLogger fromCommandLogger(CommandLogger commandLogger) {
        return new PIRLogger() {
            @Override
            public synchronized void info(String message) {
                commandLogger.addSuccess(message).output();
            }

            @Override
            public synchronized void warn(String message) {
                commandLogger.addError(message).output();
            }
        };
    }
}
