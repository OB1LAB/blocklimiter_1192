package ob1lab.blocklimiter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ob1lab.blocklimiter.commands.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ob1lab.blocklimiter.Blocklimiter.*;

@Mod.EventBusSubscriber(modid=Blocklimiter.MODID, bus= Mod.EventBusSubscriber.Bus.FORGE, value= Dist.DEDICATED_SERVER)
public class ModEvents {
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        Entity player = event.getEntity();
        String blockName = getBlockId(event.getPlacedBlock());
        if (limitedList.containsKey(blockName) && (!adminBypass || !Objects.requireNonNull(player).hasPermissions(4))) {
            int count = 0;
            int max_count = (int) limitedList.get(blockName).get(1);
            ChunkAccess chunk = event.getLevel().getChunk(event.getPos());
            for (BlockPos tile : chunk.getBlockEntitiesPos()) {
                if (getBlockId(chunk.getBlockEntity(tile)).equals(blockName)) {
                    count = count + 1;
                    if (count > max_count) {
                        if (player != null) {
                            Message.onLimit.replace("{item}", (String) limitedList.get(blockName).get(0)).replace("{limit}", String.valueOf(max_count)).send(player.createCommandSourceStack());
                        }
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onChunkLoaded(ChunkEvent.Load event) {
        if (!isStarted || !useWebhook) {
            return;
        }
        ChunkAccess chunk =  event.getChunk();
        Map<String, Integer> amount_blocks = new HashMap<>();
        for (String blockName: limitedList.keySet()) {
            amount_blocks.put(blockName, 0);
        }
        for (BlockPos tile: chunk.getBlockEntitiesPos()) {
            String blockName = getBlockId(chunk.getBlockEntity(tile));
            if (amount_blocks.containsKey(blockName)) {
                amount_blocks.replace(blockName, amount_blocks.get(blockName)+1);
            }
        }
        for (String blockName: limitedList.keySet()) {
            if (amount_blocks.get(blockName) > (int) limitedList.get(blockName).get(1)) {
                Map<String, String> webhook = new HashMap<>();
                webhook.put("content", webhookMsg.replace("{item}", blockName).replace("{x}", String.valueOf(chunk.getPos().x*16)).replace("{z}", String.valueOf(chunk.getPos().z*16)).replace("{world}", (event.getChunk().getPos().toString()).replace("{limit}", String.valueOf(limitedList.get(blockName).get(1))).replace("{set}", String.valueOf(amount_blocks.get(blockName)))));
                Gson GSON = new GsonBuilder().create();
                Thread request = new Thread(() -> Requests.post(webhookToken, GSON.toJson(webhook)));
                request.start();
            }
        }
    }
}