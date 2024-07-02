package ob1lab.blocklimiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import ob1lab.blocklimiter.Blocklimiter;
import ob1lab.blocklimiter.Config;

public class AdminBypassCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
        Commands.literal("bl")
        .then(Commands.literal("bypass")
        .requires((commandSource -> commandSource.hasPermission(4)))
        .executes((command) -> changeBypassState(
                command.getSource()
        ))));
    }
    private static int changeBypassState(CommandSourceStack source) {
        if (Blocklimiter.adminBypass) {
            Blocklimiter.adminBypass = false;
            Config.adminBypass.set(false);
            Message.adminBypassText.replace("{state}", "&4выключен").send(source);
        } else {
            Blocklimiter.adminBypass = true;
            Config.adminBypass.set(true);
            Message.adminBypassText.replace("{state}", "&2включен").send(source);
        }
        return 1;
    }
}
