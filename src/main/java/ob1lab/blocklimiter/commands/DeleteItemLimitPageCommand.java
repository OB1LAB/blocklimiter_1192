package ob1lab.blocklimiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static ob1lab.blocklimiter.Blocklimiter.limitedList;
import static ob1lab.blocklimiter.Blocklimiter.writeLimits;

public class DeleteItemLimitPageCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
        Commands.literal("bl")
        .then(Commands.literal("del")
        .requires((commandSource -> commandSource.hasPermission(4)))
        .then(Commands.argument("blockName", StringArgumentType.greedyString())
        .executes((command) -> deleteItemLimit(
                command.getSource(),
                StringArgumentType.getString(command, "blockName"))
        ))));
    }
    private static int deleteItemLimit(CommandSourceStack source, String blockName){
        if (limitedList.containsKey(blockName)) {
            Message.onDelete.replace("{item}", limitedList.get(blockName).get(0).toString()).send(source);
            limitedList.remove(blockName);
        } else {
            Message.itemNotInList.replace("{item}", blockName).send(source);
        }
        writeLimits();
        return 1;
    }
}
