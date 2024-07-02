package ob1lab.blocklimiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import ob1lab.blocklimiter.Config;

import static ob1lab.blocklimiter.Blocklimiter.limitedList;

public class ListPageCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
        Commands.literal("bl")
        .then(Commands.literal("list")
        .then(Commands.argument("page", IntegerArgumentType.integer())
        .requires((commandSource -> commandSource.hasPermission(4)))
        .executes((command) -> listItemPageLimit(
                command.getSource(),
                IntegerArgumentType.getInteger(command, "page")
        )))));
    }
    private static int listItemPageLimit(CommandSourceStack source, int page) {
        int itemOnPage = Config.viewItemsInList.get();
        int maxPage = (int) Math.ceil((double) limitedList.size()/itemOnPage);
        if (!(0 < page && page <= maxPage)) {
            Message.incorrectPage.send(source);
            return -1;
        }
        Message.pageUp.replace("{page}", "1").replace("{page}", String.valueOf(page)).replace("{max_page}", String.valueOf(maxPage)).send(source);
        for (int i = 0; i < limitedList.size(); i++) {
            if ((page-1)*itemOnPage <= i && i < page*itemOnPage) {
                Object key = limitedList.keySet().toArray()[i];
                Message.delete
                        .replace("{item}", (String) key)
                        .replace("{view_name}", limitedList.get(key).get(0).toString())
                        .replace("{limit}", limitedList.get(key).get(1).toString())
                        .send(source);
            }
        }
        if (1 < page && page < maxPage) {
            Message.pageDown.replace("{previous_page}", String.valueOf(page - 1)).replace("{next_page}", String.valueOf(page + 1 )).send(source);
        } else if (page == maxPage && page > 1) {
            Message.pageDownLast.replace("{previous_page}", String.valueOf(page - 1)).send(source);
        } else if (maxPage > 1) {
            Message.pageDownFirst.replace("{next_page}", "2").send(source);
        } else {
            Message.pageDownZero.send(source);
        }
        return 1;
    }
}
