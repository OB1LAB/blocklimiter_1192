package ob1lab.blocklimiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ob1lab.blocklimiter.Blocklimiter.getBlockId;
import static ob1lab.blocklimiter.Blocklimiter.limitedList;

public class CheckChunkCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
        Commands.literal("bl")
        .then(Commands.literal("check")
        .requires((commandSource -> commandSource.hasPermission(4)))
        .executes((command) -> checkChunkLimit(
                command.getSource()
        ))));
    }
    private static int checkChunkLimit(CommandSourceStack source) {
        ChunkAccess chunk = Objects.requireNonNull(source.getPlayer()).getLevel().getChunk(source.getPlayer().chunkPosition().x, source.getPlayer().chunkPosition().z);
        Map<String, Integer> amount_blocks = new HashMap<>();
        for (String blockName : limitedList.keySet()) {
            amount_blocks.put(blockName, 0);
        }
        for (BlockPos tile: chunk.getBlockEntitiesPos()) {
            String checkBlockName = getBlockId(chunk.getBlockEntity(tile));
            if (amount_blocks.containsKey(checkBlockName)) {
                amount_blocks.replace(checkBlockName, amount_blocks.get(checkBlockName) + 1);
            }
        }
        for (String blockName : limitedList.keySet()) {
            if (amount_blocks.get(blockName) > (int) limitedList.get(blockName).get(1)) {
                String viewName = (String) limitedList.get(blockName).get(0);
                String limitBlock = String.valueOf(limitedList.get(blockName).get(1));
                String placed = String.valueOf(amount_blocks.get(blockName));
                Message.inChunkOnLimit.replace("{item}", viewName).replace("{placed}", placed).replace("{limit}", limitBlock).send(source);
            }
        }
        Message.onChunkScanEnd.send(source);
        return 1;
    }
}
