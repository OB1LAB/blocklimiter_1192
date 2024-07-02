package ob1lab.blocklimiter.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

import java.util.List;


import static ob1lab.blocklimiter.Blocklimiter.*;


public class AddItemLimitCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
        Commands.literal("bl")
        .then(Commands.literal("add")
        .requires((commandSource -> commandSource.hasPermission(4)))
        .then(Commands.argument("limit", IntegerArgumentType.integer())
        .then(Commands.argument("view name", StringArgumentType.greedyString())
        .executes((command) -> addItemLimit(
            command.getSource(),
            IntegerArgumentType.getInteger(command, "limit"),
            StringArgumentType.getString(command, "view name")))
        ))));
    }
    private static int addItemLimit(CommandSourceStack source, int limit, String viewName) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            return -1;
        }
        String blockName = getBlockId(player.getMainHandItem());
        boolean isBlockEntity = Block.byItem(player.getMainHandItem().getItem()).defaultBlockState().hasBlockEntity();
        if (isBlockEntity) {
            List<Object> data = Lists.newArrayList();
            data.add(viewName);
            data.add(limit);
            if (limitedList.containsKey(blockName)) {
                limitedList.replace(blockName, data);
                Message.onUpdateItemToLimitList.replace("{item}", viewName).replace("{amount}", String.valueOf(limit)).send(source);
            } else {
                limitedList.put(blockName, data);
                Message.addItemToLimitList.replace("{item}", viewName).replace("{amount}", String.valueOf(limit)).send(source);
            }
            writeLimits();
            return 1;
        } else {
            Message.itemNotTileEntity.replace("{item}", blockName).send(source);
            return -1;
        }
    }
}
