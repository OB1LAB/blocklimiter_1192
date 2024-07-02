package ob1lab.blocklimiter;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import ob1lab.blocklimiter.commands.Message;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

@Mod(Blocklimiter.MODID)
public class Blocklimiter {
    public static final String MODID = "blocklimiter";
    public static Boolean isStarted = false;
    private static final Gson GSON = new GsonBuilder().create();
    private static final File file = new File(FMLPaths.GAMEDIR.get().resolve(Paths.get("config/BlockLimit.json")).toUri());
    public static List<LimitBlock> limitJson;
    public static Map<String, List<Object>> limitedList = new HashMap<>();
    public static Boolean adminBypass;
    public static Boolean useWebhook;
    public static String webhookToken;
    public static String webhookMsg;
    public Blocklimiter() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, "BlockLimit-config.toml");
        MinecraftForge.EVENT_BUS.register(ModCommands.class);
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) throws IOException {
        limitJson = readLimits();
        for (LimitBlock block: limitJson) {
            List<Object> data = Lists.newArrayList();
            data.add(block.viewName);
            data.add(block.limit);
            limitedList.put(block.id, data);
        }
        loadConfig();
        isStarted = true;
    }
    private List<LimitBlock> readLimits() throws IOException {
        try {
            String json = Files.asCharSource(file, StandardCharsets.UTF_8).read();
            return Arrays.asList(GSON.fromJson(json, LimitBlock[].class));
        } catch (IOException e) {
            Files.write(GSON.toJson(Lists.newArrayList()), file, StandardCharsets.UTF_8);
            return Lists.newArrayList();
        }
    }
    public static void writeLimits() {
        try {
            List<LimitBlock> writeLimitJson = Lists.newArrayList();
            for (String key: limitedList.keySet()) {
                String viewName = (String) limitedList.get(key).get(0);
                int limit = (int) limitedList.get(key).get(1);
                writeLimitJson.add(new LimitBlock(key, viewName, limit));
            }
            Files.write(GSON.toJson(writeLimitJson.toArray(new LimitBlock[0])), file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getBlockId(BlockEntity block) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(Objects.requireNonNull(block).getBlockState().getBlock().asItem())).toString();
    }
    public static String getBlockId(BlockState block) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(block.getBlock().asItem())).toString();
    }
    public static String getBlockId(ItemStack block) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(block.getItem())).toString();
    }
    public static void loadConfig() {
        adminBypass = Config.adminBypass.get();
        useWebhook = Config.useWebhook.get();
        webhookToken = Config.webhookToken.get();
        webhookMsg = Config.webhookMsg.get();
        Map<String, String> messageList = new HashMap<>();
        messageList.put("onLimit", Config.onLimit.get());
        messageList.put("onDelete", Config.onDelete.get());
        messageList.put("itemNotInList", Config.itemNotInList.get());
        messageList.put("itemNotTileEntity", Config.itemNotTileEntity.get());
        messageList.put("addItemToLimitList", Config.addItemToLimitList.get());
        messageList.put("onUpdateItemToLimitList", Config.onUpdateItemToLimitList.get());
        messageList.put("incorrectPage", Config.incorrectPage.get());
        messageList.put("pageUp", Config.pageUp.get());
        messageList.put("pageDown", Config.pageDown.get());
        messageList.put("pageDownZero", Config.pageDownZero.get());
        messageList.put("pageDownFirst", Config.pageDownFirst.get());
        messageList.put("pageDownLast", Config.pageDownLast.get());
        messageList.put("delete", Config.delete.get());
        messageList.put("documentation", Config.documentation.get());
        messageList.put("adminBypassText", Config.adminBypassText.get());
        messageList.put("updateConfigText", Config.updateConfigText.get());
        messageList.put("inChunkOnLimit", Config.inChunkOnLimit.get());
        messageList.put("onChunkScanEnd", Config.onChunkScanEnd.get());
        Message.load(messageList);
    }
}
