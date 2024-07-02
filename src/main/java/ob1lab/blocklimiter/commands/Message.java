package ob1lab.blocklimiter.commands;

import com.google.common.collect.Lists;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum Message {
    onLimit, onDelete, itemNotInList, itemNotTileEntity, addItemToLimitList, onUpdateItemToLimitList, incorrectPage, pageUp, pageDown, pageDownZero, pageDownFirst, pageDownLast, delete, documentation, adminBypassText, updateConfigText, inChunkOnLimit, onChunkScanEnd;
    private List<String> msg;
    public static void load(Map<String, String> messageList) {
        for (Message message : Message.values()) {
            String obj = messageList.get(message.name());
            message.msg = Lists.newArrayList(obj);
        }
    }
    public Sender replace(String from, String to) {
        Sender sender = new Sender();
        return sender.replace(from, to);
    }
    public void send(CommandSourceStack source) {
        new Sender().send(source);
    }
    public class Sender {
        private final Map<String, String> placeholders = new HashMap<>();
        public void send(CommandSourceStack source) {
            for (String message : Message.this.msg) {
                sendMessage(source, replacePlaceholders(message).replace("&", "§"));
            }
        }
        private void sendMessage(CommandSourceStack source, String message) {
            if (message.startsWith("json:")) {
                source.sendSystemMessage(Objects.requireNonNull(Component.Serializer.fromJson(message.substring(5))));
            } else {
                source.sendSystemMessage(Component.literal(message));
            }
        }
        public Sender replace(String from, String to) {
            placeholders.put(from, to);
            return this;
        }
        private String replacePlaceholders(String message) {
            if (!message.contains("{")) return message;
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
            return message;
        }
    }
}