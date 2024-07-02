package ob1lab.blocklimiter;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ob1lab.blocklimiter.commands.*;

public class ModCommands {
    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
        AddItemLimitCommand.register(commandDispatcher);
        DeleteItemLimitCommand.register(commandDispatcher);
        DeleteItemLimitPageCommand.register(commandDispatcher);
        ListCommand.register(commandDispatcher);
        ListPageCommand.register(commandDispatcher);
        InfoCommand.register(commandDispatcher);
        AdminBypassCommand.register(commandDispatcher);
        UpdateConfigCommand.register(commandDispatcher);
        CheckChunkCommand.register(commandDispatcher);
    }
}
