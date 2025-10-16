package studio.resonos.nano.api.command.paramter.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import studio.resonos.nano.api.command.paramter.Processor;
import studio.resonos.nano.core.util.CC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BooleanProcessor extends Processor<Boolean> {
    private final Map<String, Boolean> values = new HashMap<>();

    public BooleanProcessor() {
        // Values that mean true
        values.put("true", true);
        values.put("on", true);
        values.put("yes", true);
        values.put("enable", true);

        // Values that mean false
        values.put("false", false);
        values.put("off", false);
        values.put("no", false);
        values.put("disable", false);
    }

    public Boolean process(CommandSender sender, String supplied) {
        supplied = supplied.toLowerCase();
        if (!values.containsKey(supplied)) {
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &eValid values are: &a" + String.join(", ", values.keySet())));
            return null;
        }

        return values.get(supplied);
    }

    public List<String> tabComplete(CommandSender sender, String supplied) {
        return values.keySet().stream().filter(s -> s.toLowerCase().startsWith(supplied.toLowerCase())).collect(Collectors.toList());
    }

}
