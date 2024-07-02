package ob1lab.blocklimiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import ob1lab.blocklimiter.Blocklimiter;

public class UpdateConfigCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
        Commands.literal("bl")
        .then(Commands.literal("reload")
        .requires((commandSource -> commandSource.hasPermission(4)))
        .executes((command) -> updateConfig(
                command.getSource()
        ))));
    }
    private static int updateConfig(CommandSourceStack source) {
        Blocklimiter.loadConfig();
        Message.updateConfigText.send(source);
        return 1;
    }
}
