package ob1lab.blocklimiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class InfoCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
        Commands.literal("bl")
        .then(Commands.literal("info")
        .requires((commandSource -> commandSource.hasPermission(4)))
        .executes((command) -> info(
                command.getSource()
        ))));
    }
    private static int info(CommandSourceStack source) {
        Message.documentation.send(source);
        return 1;
    }

}
