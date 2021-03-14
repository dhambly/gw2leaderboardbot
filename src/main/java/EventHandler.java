import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventHandler extends ListenerAdapter {
    private APIHandler api;
    private CommandHandler commands;

    public EventHandler(APIHandler a) {
        this.api = a;
        this.commands = new CommandHandler(api);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay();
        if (message.trim().equalsIgnoreCase("!chimpcheck")) {
            commands.chimpCheck(event);
        } else if (message.equalsIgnoreCase("ping")) {
            event.getChannel().sendMessage("pong").queue();
        } else if (message.equalsIgnoreCase("!rankedegirls")) {
            commands.listEgirlRanks(event);
        }
    }
}
