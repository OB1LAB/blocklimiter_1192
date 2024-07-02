package ob1lab.blocklimiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

import static ob1lab.blocklimiter.Blocklimiter.*;

public class DeleteItemLimitCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(
    Commands.literal("bl")
    .then(Commands.literal("del")
    .requires((commandSource -> commandSource.hasPermission(4)))
    .executes((command) -> deleteItemLimit(
            command.getSource())
    )));
    }
    private static int deleteItemLimit(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            return -1;
        }
        String blockName = getBlockId(player.getMainHandItem());
        boolean isBlockEntity = Block.byItem(player.getMainHandItem().getItem()).defaultBlockState().hasBlockEntity();
        if (isBlockEntity) {
            if (limitedList.containsKey(blockName)) {
                Message.onDelete.replace("{item}", limitedList.get(blockName).get(0).toString()).send(source);
                limitedList.remove(blockName);
            } else {
                Message.itemNotInList.replace("{item}", blockName).send(source);
            }
            writeLimits();
            return 1;
        } else {
            Message.itemNotTileEntity.replace("{item}", blockName).send(source);
            return -1;
        }
    }
}
