package me.miku.main;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.miku.main.papi.WorldSize;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.CachedServerIcon;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public final class A2b2tCore extends JavaPlugin implements CommandExecutor, Listener {
    public static File config;
    public static String prefix;
    public static YamlConfiguration yamlConfig;
    public static String pluginMessage;
    public static List<String> helpMessage, statsPlayer, statsConsole;
    public static String successfulReload;
    public static String errorReload;
    public static String noPermission, JoinErrorMessage;
    public static int shulkerDupe;
    public static int fzDupeTime;
    public static String ErrorConsoleMessage;
    public static String fzSuccessFulMessage;
    public static int fzDupeMaxTime;
    public static String fzErrorMessage;
    public static int AChunkMaxRedStone;
    public static int AChunkMaxPiston, coldDown;
    public static String outPistonMessage, nothingToReply, statPlayerNotFoundMessage;
    public static boolean enableQuitJoinMessage, hasAuthme = false;
    public static boolean enablemotd;
    public static boolean disablePing;
    public static List<String> motds;
    public static int onlinePlayer;
    public static int maxPlayer;
    public static boolean hidePlayer;
    public static int protocolVersion, maxDamage, MaxQueuePlayer, minQueueSeconds, maxQueueSeconds;
    public static boolean randomIcon, EnableQueue, Anti32k, TeleLogin;
    public static List<String> tabReturnMessage, statMessage;
    public static List<Player> chatColdDown = new ArrayList<>();
    public static String whisperUsageMessage, whisperMessage, ksKickMessage, version, cantPlaceBlockMessage, outRedStoneMessage, whisperReceiveMessage, whisperPlayerNotOnline, whisperCannotSendme, chatColdDownMessage, QueueMap, PositionInQueue;
    //                Player
    public static Map<String, PlayerQueue> queue = new HashMap<>();
    public static List<CachedServerIcon> icons = new ArrayList<>();
    //              Receiver  Sender
    public static Map<String, String> whispers = new HashMap<>();
    public static Map<Player, Integer> shulkerdupes = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            load();
        } catch (Exception ex) {
            //
        }
        this.getServer().getPluginManager().registerEvents(this, this);
        if(TeleLogin) {
            this.getServer().createWorld(new WorldCreator(QueueMap));
        }
        new WorldSize().register();
        for(World world : getServer().getWorlds()) {
            if(world.getGameRuleValue("announceAdvancements").equals("true")) {
                world.setGameRuleValue("announceAdvancements", "false");
            }
            if(world.getGameRuleValue("sendCommandFeedback").equals("true")) {
                world.setGameRuleValue("sendCommandFeedback", "false");
            }
            if(world.getGameRuleValue("commandBlockOutput").equals("true")) {
                world.setGameRuleValue("commandBlockOutput", "false");
            }
        }
        if(this.getServer().getPluginManager().getPlugin("Authme") == null) {
            this.getServer().getPluginManager().registerEvents(new AuthmeListener(), this);
        } else {
            hasAuthme = true;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(this.getServer().getWorld(QueueMap) != null) {
            this.getServer().unloadWorld(QueueMap, false);
        }
        shulkerdupes.clear();
        chatColdDown.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("pl") || command.getName().equals("plugins")) {
            sender.sendMessage(StringUtils.format(pluginMessage));
        }
        else if(command.getName().equals("help") || command.getName().equals("?")) {
            for (String v : helpMessage) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(v)));
            }
        }
        else if(command.getName().equals("2b2tcore")) {
            if (args.length == 0){
                sender.sendMessage("/2b2tcore reload, reload the config");
            }
            else if(args[0].toLowerCase(Locale.ROOT).equals("reload")) {
                if(sender.hasPermission("2b2tcore.admin")) {
                    try {
                        load();
                        this.getServer().createWorld(new WorldCreator(QueueMap));
                        sender.sendMessage(StringUtils.format(successfulReload));
                    } catch (Exception ex) {
                        sender.sendMessage(errorReload);
                    }
                } else {
                    sender.sendMessage(noPermission);
                }
            }
        }
        else if(command.getName().equals("fz")) {
            if(sender instanceof Player) {
                if (args.length > 0) {
                    if(Integer.parseInt(args[0]) > fzDupeMaxTime) {
                        sender.sendMessage(fzErrorMessage);
                        return super.onCommand(sender, command, label, args);
                    }
                    for (int i = 0; i < Integer.parseInt(args[0]); i++) {
                        ((Player) sender).getInventory().addItem(((Player) sender).getInventory().getItemInMainHand());
                        sender.sendMessage(fzSuccessFulMessage);
                    }
                } else {
                    for (int i = 0; i < fzDupeTime; i++) {
                        ((Player) sender).getInventory().addItem(((Player) sender).getInventory().getItemInMainHand());
                        sender.sendMessage(fzSuccessFulMessage);
                    }
                }
            } else {
                sender.sendMessage(ErrorConsoleMessage);
            }
        } else if(command.getName().equals("m") || command.getName().equals("tell") || command.getName().equals("msg") || command.getName().equals("s") || command.getName().equals("whisper") || command.getName().equals("w")) {
            if(args.length == 0 || args.length == 1) {
                String whisperUsageMessages = StringUtils.format(whisperUsageMessage);
                if(whisperUsageMessages.contains("%COMMAND_NAME%")) {
                    whisperUsageMessages = whisperUsageMessages.replaceAll("%COMMAND_NAME%", command.getName());
                }
                sender.sendMessage(whisperUsageMessages);
                return super.onCommand(sender, command, label, args);
            }
            if (this.getServer().getPlayer(args[0]) == null) {
                String whisperPlayerNotOnlines = StringUtils.format(whisperPlayerNotOnline);
                if(whisperPlayerNotOnlines.contains("%player%")) {
                    whisperPlayerNotOnlines = whisperPlayerNotOnlines.replaceAll("%player%", args[0]);
                }
                sender.sendMessage(whisperPlayerNotOnlines);
                return super.onCommand(sender, command, label, args);
            }
            if (this.getServer().getPlayer(args[0]).getName().equals(sender.getName())) {
                sender.sendMessage(StringUtils.format(whisperCannotSendme));
                return super.onCommand(sender, command, label, args);
            }
            StringBuilder message = new StringBuilder();
            for(int i = 1; i <= args.length - 1; i++) {
                message.append(ChatColor.translateAlternateColorCodes(prefix.charAt(0), args[i])).append(" ");
            }
            sender.sendMessage(StringUtils.format(whisperMessage).replaceAll("%player%", this.getServer().getPlayer(args[0]).getName()).replaceAll("%MESSAGE%", message.toString()));
            this.getServer().getPlayer(args[0]).sendMessage(StringUtils.format(whisperReceiveMessage).replace("%player%", sender.getName()).replaceAll("%MESSAGE%", message.toString()));
            whispers.put(this.getServer().getPlayer(args[0]).getName(), sender.getName());
        } else if(command.getName().equals("kill") || command.getName().equals("514")) {
            if(sender instanceof Player) {
                ((Player) sender).setHealth(0);
            } else {
                sender.sendMessage(ErrorConsoleMessage);
            }
        } else if(command.getName().equals("ks")) {
            if(sender instanceof Player) {
                ((Player) sender).kickPlayer(StringUtils.translatePlaceholderAPI(StringUtils.format(ksKickMessage), (Player) sender));
            } else {
                sender.sendMessage(ErrorConsoleMessage);
            }
        } else if(command.getName().equals("r")) {
            if(whispers.get(sender.getName()) == null) {
                sender.sendMessage(StringUtils.format(nothingToReply));
                return super.onCommand(sender, command, label, args);
            }
            StringBuilder message = new StringBuilder();
            for(int i = 0; i <= args.length - 1; i++) {
                message.append(ChatColor.translateAlternateColorCodes(prefix.charAt(0), args[i])).append(" ");
            }
            sender.sendMessage(StringUtils.format(whisperMessage).replaceAll("%player%", whispers.get(sender.getName())).replaceAll("%MESSAGE%", message.toString()));
            if(whispers.get(sender.getName()).equals("CONSOLE")) {
                System.out.println(StringUtils.format(whisperReceiveMessage).replace("%player%", sender.getName()).replaceAll("%MESSAGE%", message.toString()));
            } else {
                this.getServer().getPlayer(whispers.get(sender.getName())).sendMessage(StringUtils.format(whisperReceiveMessage).replace("%player%", sender.getName()).replaceAll("%MESSAGE%", message.toString()));
            }
        } else if(command.getName().equals("join")) {
            if(sender instanceof Player) {
                if(((Player) sender).getWorld().getName().equals(QueueMap)) {
                    Location location = ((Player) sender).getLocation();
                    if(this.getServer().getWorld(queue.get(sender.getName()).normalWorld.getName()) != null) {
                        location.setWorld(this.getServer().getWorld(queue.get(sender.getName()).normalWorld.getName()));
                    } else {
                        location.setWorld(this.getServer().getWorlds().get(0));
                    }
                    ((Player) sender).teleport(location);
                } else {
                    String message = JoinErrorMessage;
                    if(message.contains("%QueueMap%")) {
                        message = message.replaceAll("%QueueMap%", QueueMap);
                    }
                    sender.sendMessage(StringUtils.format(StringUtils.format(message)));
                }
            } else {
                sender.sendMessage(StringUtils.format(ErrorConsoleMessage));
            }
        } else if(command.getName().equals("stat")) {
            if(args.length == 0 && sender instanceof Player) {
                for(String v : statMessage) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(StringUtils.translatePlaceholderAPI(v, ((Player) sender)))));
                }
                return super.onCommand(sender, command, label, args);
            }
            if(args.length == 1) {
                if(this.getServer().getPlayer(args[0]) != null) {
                    for (String v : statMessage) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(StringUtils.translatePlaceholderAPI(v, this.getServer().getOfflinePlayer(args[0])))));
                    }
                } else {
                    sender.sendMessage(StringUtils.translatePlaceholderAPI(StringUtils.format(statPlayerNotFoundMessage.replaceAll("%player%", args[0])), null));
                }
                return super.onCommand(sender, command, label, args);
            }
            sender.sendMessage(StringUtils.format(ErrorConsoleMessage));
        } else if(command.getName().equals("stats")) {
            if(sender instanceof Player) {
                for(String v : statsPlayer) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(StringUtils.translatePlaceholderAPI(v, (Player) sender))));
                }
            } else {
                for(String v : statsConsole) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(StringUtils.translatePlaceholderAPI(v, null))));
                }
            }
        }
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(command.getName().equals("2b2tcore") && args.length == 1) {
            return Collections.singletonList("reload");
        } else if(command.getName().equals("m") || command.getName().equals("tell") || command.getName().equals("msg") || command.getName().equals("s") || command.getName().equals("whisper") || command.getName().equals("w") || command.getName().equals("stat")) {
            if(args.length == 1) {
                List<String> rtn = new ArrayList<>();
                for(Player player : this.getServer().getOnlinePlayers()) {
                    if(player.getName().toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT)) && (!player.getName().equals(sender.getName()) || command.getName().equals("stat"))) {
                        rtn.add(player.getName());
                    }
                }
                return rtn;
            } else if(args.length == 0) {
                List<String> rtn = new ArrayList<>();
                for(Player player : this.getServer().getOnlinePlayers()) {
                    if(!player.getName().equals(sender.getName()) || command.getName().equals("stat")) {
                        rtn.add(player.getName());
                    }
                }
                return rtn;
            }
        }
        return super.onTabComplete(sender, command, alias, args);
    }

    @EventHandler
    public void returnTab(AsyncTabCompleteEvent event) {
        if(event.getSender() instanceof Player) {
            if(StringUtils.getCharAmount(event.getBuffer(), ' ') == 0) event.setCompletions(tabReturnMessage);
        }
    }

    @EventHandler
    public void antiHigherDamage(EntityDamageByEntityEvent event) {
        if(event.getDamage() > maxDamage && event.getEntity() instanceof Player && Anti32k) {
            if(!((Player) event.getDamager()).getOpenInventory().getType().equals(InventoryType.HOPPER)) {
                event.setCancelled(true);
            }
            if(((Player) event.getDamager()).getInventory().getItemInMainHand().getItemMeta().isUnbreakable()) {
                ((Player) event.getDamager()).getInventory().remove(((Player) event.getDamager()).getInventory().getItemInMainHand());
            }
        }
    }

    @EventHandler
    public void antiHigherDamager(EntityDamageByBlockEvent event) {
        if(event.getDamage() > maxDamage && event.getEntity() instanceof Player && Anti32k) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void antiEMP(BlockPlaceEvent event) {
        if(event.getPlayer() != null && event.getPlayer().hasPermission("2b2tcore.admin")) return;
        if(ChunkUtils.checkBlock(event.getBlock().getChunk(), Material.PISTON_BASE) > AChunkMaxPiston || ChunkUtils.checkBlock(event.getBlock().getChunk(), Material.PISTON_STICKY_BASE) > AChunkMaxPiston) {
            event.setCancelled(true);
            if(event.getPlayer() != null) {
                event.getPlayer().sendMessage(outPistonMessage);
            }
        }
        else if(ChunkUtils.checkBlock(event.getBlock().getChunk(), Material.REDSTONE_BLOCK) > AChunkMaxRedStone) {
            event.setCancelled(true);
            if(event.getPlayer() != null) {
                event.getPlayer().sendMessage(outRedStoneMessage);
            }
        }
    }

    @EventHandler
    public void antiPME(BlockPistonExtendEvent event) {
        if(ChunkUtils.checkBlock(event.getBlock().getChunk(), Material.PISTON_BASE) > AChunkMaxPiston || ChunkUtils.checkBlock(event.getBlock().getChunk(), Material.PISTON_STICKY_BASE) > AChunkMaxPiston) {
            event.setCancelled(true);
        }
        else if(ChunkUtils.checkBlock(event.getBlock().getChunk(), Material.REDSTONE) > AChunkMaxRedStone) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void antiPPE(BlockPistonRetractEvent event) {
        if(ChunkUtils.checkBlock(event.getBlock().getChunk(), Material.PISTON_BASE) > AChunkMaxPiston || ChunkUtils.checkBlock(event.getBlock().getChunk(), Material.PISTON_STICKY_BASE) > AChunkMaxPiston) {
            event.setCancelled(true);
        }
        else if(ChunkUtils.checkBlock(event.getBlock().getChunk(), Material.REDSTONE) > AChunkMaxRedStone) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void antiShulkerCrash(BlockPlaceEvent event) {
        if(event.getPlayer() != null && event.getPlayer().hasPermission("2b2tcore.admin")) return;
        if (event.getBlockPlaced().getType().equals(Material.DISPENSER)) {
            if (event.getBlockPlaced().getLocation().getY() >= 255 || event.getBlockPlaced().getLocation().getY() <= 0) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(StringUtils.format(cantPlaceBlockMessage));
            }
        }
    }

    @EventHandler
    public void antiShulkerCrash2(BlockDispenseEvent event) {
        if(Materials.shulkers.contains(event.getItem().getType())) {
            if (event.getBlock().getLocation().getY() >= 255 || event.getBlock().getLocation().getY() <= 0) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void checkShulkerDupe(BlockPlaceEvent event) {
        if(event.getPlayer() == null) return;
        if(!Materials.shulkers.contains(event.getBlockPlaced().getType())) return;
        if(!event.getPlayer().hasPermission("2b2tcore.shulkderdupe")) return;
        if(shulkerdupes.containsKey(event.getPlayer())) {
            shulkerdupes.put(event.getPlayer(), shulkerdupes.get(event.getPlayer()) + 1);
            if(shulkerdupes.get(event.getPlayer()) > shulkerDupe) {
                event.getPlayer().getInventory().addItem(event.getItemInHand());
                shulkerdupes.remove(event.getPlayer());
            }
        } else {
            shulkerdupes.put(event.getPlayer(), 1);
        }
    }

    @EventHandler
    public void ping(PaperServerListPingEvent event) {
        if(disablePing) {
            event.setCancelled(true);
            return;
        }
        if(enablemotd) {
            String j = ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(motds.get(new Random().nextInt(motds.size()))));
            if (j.contains("%PLAYER%")) {
                j = j.replaceAll("%PLAYER%", event.getAddress().getHostName());
            }
            if(protocolVersion > 0) {
                event.setProtocolVersion(protocolVersion);
            }
            if (j.contains("%PROTOCOLVERSION%")) {
                j = j.replaceAll("%PROTOCOLVERSION%", String.valueOf(event.getProtocolVersion()));
            }
            event.setMotd(j);
            event.setMaxPlayers(maxPlayer);
            event.setNumPlayers(onlinePlayer);
            event.setHidePlayers(hidePlayer);
            event.setVersion(StringUtils.format(version));
            if(randomIcon && icons.size() > 0) {
                event.setServerIcon(icons.get(new Random().nextInt(icons.size())));
            } else if(icons.size() > 0) {
                event.setServerIcon(icons.get(0));
            }
        }
    }

    @EventHandler
    public void coldDown(AsyncPlayerChatEvent event) {
        if (event.getPlayer().hasPermission("2b2tcore.admin")) return;
        if (chatColdDown.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(StringUtils.format(chatColdDownMessage));
            return;
        }
        chatColdDown.add(event.getPlayer());
        new Thread(() -> {
            try {
                Thread.sleep(coldDown * 1000L);
            } catch (Exception exception) {
                //
            }
            chatColdDown.remove(event.getPlayer());
        }).start();
    }

    @EventHandler
    public void coldDown1(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission("2b2tcore.admin")) return;
        if (chatColdDown.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(StringUtils.format(chatColdDownMessage));
            return;
        }
        chatColdDown.add(event.getPlayer());
        new Thread(() -> {
            try {
                Thread.sleep(coldDown * 1000L);
            } catch (Exception exception) {
                //
            }
            chatColdDown.remove(event.getPlayer());
        }).start();
    }

    @EventHandler
    public void queue1(PlayerJoinEvent event) {
        if(!hasAuthme) {
            if (EnableQueue) {
                queue.put(event.getPlayer().getName(), new PlayerQueue(event.getPlayer(), new Random().nextInt(maxQueueSeconds - minQueueSeconds + 1) + minQueueSeconds, queue.size(), event.getPlayer().getWorld()));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (getServer().getPlayer(event.getPlayer().getName()) == null || !event.getPlayer().getWorld().getName().equals(QueueMap)) {
                            cancel();
                            return;
                        }
                        if (getServer().getPlayer(event.getPlayer().getName()) != null || event.getPlayer().getWorld().getName().equals(QueueMap)) {
                            if (queue.containsKey(event.getPlayer().getName())) {
                                if (EnableQueue && !event.getPlayer().hasPermission("2b2tcore.skipQueue")) {
                                    for (Player v : getServer().getOnlinePlayers()) {
                                        if (!event.getPlayer().getName().equals(v.getName())) {
                                            event.getPlayer().hidePlayer(v);
                                        }
                                    }
                                    if (!queue.containsKey(event.getPlayer().getName())) {
                                        queue.put(event.getPlayer().getName(), new PlayerQueue(event.getPlayer(), new Random().nextInt(maxQueueSeconds - minQueueSeconds + 1) + minQueueSeconds, queue.size(), event.getPlayer().getWorld()));
                                    } else {
                                        queue.get(event.getPlayer().getName()).joinServerTick++;
                                    }
                                    String positionMessage = PositionInQueue;
                                    if (positionMessage.contains("%Position%")) {
                                        positionMessage = positionMessage.replaceAll("%Position%", String.valueOf(Math.abs(queue.get(event.getPlayer().getName()).position)));
                                    }
                                    positionMessage = StringUtils.format(positionMessage);
                                    event.getPlayer().sendTitle(positionMessage, "", 0, 5, 15);
                                }
                                if (queue.get(event.getPlayer().getName()).joinServerTick >= queue.get(event.getPlayer().getName()).queueTime * 20 && queue.get(event.getPlayer().getName()).position <= 0) {
                                    getServer().getPlayer(event.getPlayer().getName()).performCommand("2b2tCore:join");
                                }
                            } else {
                                getServer().getPlayer(event.getPlayer().getName()).performCommand("2b2tCore:join");
                            }
                        }
                    }
                }.runTaskTimerAsynchronously(this, 1L, 1L);
                if (this.getServer().getWorlds().stream().filter(v -> !v.getName().equals(QueueMap)).collect(Collectors.toList()).size() > 0) {
                    Location location = event.getPlayer().getLocation();
                    location.setWorld(this.getServer().getWorld(QueueMap));
                    event.getPlayer().teleport(location);
                }
            }
        }
    }

    @EventHandler
    public void queue2(PlayerChangedWorldEvent event) {
        if (event.getFrom().getName().equals(QueueMap)) {
            queue.remove(event.getPlayer().getName());
            for(Player v : this.getServer().getOnlinePlayers()) {
                if(!event.getPlayer().getName().equals(v.getName())) {
                    event.getPlayer().showPlayer(v);
                }
            }
            for(Map.Entry<String, PlayerQueue> entry : queue.entrySet()) {
                entry.getValue().position--;
            }
        }
    }

    @EventHandler
    public void queue3(PlayerMoveEvent event) {
        if(event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName())) {
            if(event.getPlayer().getWorld().getName().equals(QueueMap)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void queue4(EntityDamageByBlockEvent event) {
        if(event.getEntity() instanceof Player) {
            if(event.getEntity().getWorld().getName().equals(QueueMap)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void queue5(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player) {
            if(event.getEntity().getWorld().getName().equals(QueueMap)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void queue6(PlayerQuitEvent event) {
        if(event.getPlayer().getWorld().getName().equals(QueueMap)) {
            Location location = event.getPlayer().getLocation();
            if(queue.get(event.getPlayer().getName()) != null) {
                if (this.getServer().getWorld(queue.get(event.getPlayer().getName()).normalWorld.getName()) == null || queue.get(event.getPlayer().getName()).normalWorld.getName().equals(QueueMap)) {
                    location.setWorld(this.getServer().getWorlds().get(0));
                } else {
                    location.setWorld(this.getServer().getWorld(queue.get(event.getPlayer().getName()).normalWorld.getName()));
                }
            } else {
                location.setWorld(this.getServer().getWorlds().get(0));
            }
            event.getPlayer().teleport(location);
            queue.remove(event.getPlayer().getName());
            for(Map.Entry<String, PlayerQueue> entry : queue.entrySet()) {
                entry.getValue().position--;
            }
        }
    }

    @EventHandler
    public void queue7(PlayerInteractEvent event) {
        if(event.getPlayer().getWorld().getName().equals(QueueMap)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void queue8(PlayerItemDamageEvent event) {
        if(event.getPlayer().getWorld().getName().equals(QueueMap)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void queue9(InventoryClickEvent event) {
        if(event.getWhoClicked().getWorld().getName().equals(QueueMap)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void queue10(PlayerDropItemEvent event) {
        if(event.getPlayer().getWorld().getName().equals(QueueMap)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void queue11(PlayerDeathEvent event) {
        if(event.getEntity() != null && event.getEntity().getWorld().getName().equals(QueueMap)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void quitMessage(PlayerQuitEvent event) {
        if(!enableQuitJoinMessage) {
            event.setQuitMessage("");
        }
    }

    @EventHandler
    public void joinMessage(PlayerJoinEvent event) {
        if(!enableQuitJoinMessage) {
            event.setJoinMessage("");
        }
    }

    public void load() throws Exception {
        if(!new File(this.getDataFolder().getCanonicalPath() + "/config.yml").exists()) this.saveResource("config.yml", false);
        config = new File(this.getDataFolder().getCanonicalPath() + "/config.yml");
        yamlConfig = YamlConfiguration.loadConfiguration(config);
        prefix = yamlConfig.getString("colorPreFix");
        pluginMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("pluginMessage"));
        helpMessage = yamlConfig.getStringList("Helps");
        successfulReload = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("successfulReload"));
        errorReload = ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(yamlConfig.getString("ErrorReload")));
        noPermission = ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(yamlConfig.getString("noPermission")));
        shulkerDupe = yamlConfig.getInt("ShulkderDupe");
        fzDupeTime = yamlConfig.getInt("fzDupeTime");
        ErrorConsoleMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(yamlConfig.getString("ErrorConsoleMessage")));
        fzSuccessFulMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(yamlConfig.getString("fzSuccessFulMessage")));
        fzDupeMaxTime = yamlConfig.getInt("fzDupeMaxTime");
        fzErrorMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(yamlConfig.getString("fzErrorMessage")));
        AChunkMaxPiston = yamlConfig.getInt("AChunkMaxPiston");
        AChunkMaxRedStone = yamlConfig.getInt("AChunkMaxRedStone");
        outRedStoneMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(yamlConfig.getString("outRedStoneMessage")));
        outPistonMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), StringUtils.format(yamlConfig.getString("outPistonMessage")));
        cantPlaceBlockMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("cantPlaceBlockMessage"));
        enableQuitJoinMessage = yamlConfig.getBoolean("enableQuitJoinMessage");
        enablemotd = yamlConfig.getBoolean("enablemotd");
        motds = yamlConfig.getStringList("motds");
        disablePing = yamlConfig.getBoolean("disablePing");
        onlinePlayer = yamlConfig.getInt("onlinePlayer");
        maxPlayer = yamlConfig.getInt("maxPlayer");
        hidePlayer = yamlConfig.getBoolean("hidePlayer");
        protocolVersion = yamlConfig.getInt("protocolVersion");
        version = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("version"));
        randomIcon = yamlConfig.getBoolean("randomIcon");
        List<String> icons1 = yamlConfig.getStringList("icons");
        icons.clear();
        for(String v : icons1) {
            try {
                icons.add(getServer().loadServerIcon(new File(getDataFolder().getCanonicalFile() + "/" + v)));
            } catch (Exception ex) {
                this.getLogger().info("Error to load Icons");
            }
        }
        ksKickMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("ksKickMessage"));
        whisperUsageMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("whisperUsageMessage"));
        whisperReceiveMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("whisperReceiveMessage"));
        whisperMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("whisperMessage"));
        whisperPlayerNotOnline = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("whisperPlayerNotOnline"));
        whisperCannotSendme = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("whisperCannotSendme"));
        chatColdDownMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("chatColdDownMessage"));
        coldDown = yamlConfig.getInt("coldDown");
        tabReturnMessage = yamlConfig.getStringList("tabReturnMessage");
        maxDamage = yamlConfig.getInt("maxDamage");
        EnableQueue = yamlConfig.getBoolean("EnableQueue");
        QueueMap = yamlConfig.getString("QueueMap");
        MaxQueuePlayer = yamlConfig.getInt("MaxQueuePlayer");
        minQueueSeconds = yamlConfig.getInt("minQueueSeconds");
        maxQueueSeconds = yamlConfig.getInt("maxQueueSeconds");
        Anti32k = yamlConfig.getBoolean("Anti32k");
        nothingToReply = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("nothingToReply"));
        TeleLogin = yamlConfig.getBoolean("TeleLogin");
        PositionInQueue = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("PositionInQueue"));
        JoinErrorMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("JoinErrorMessage"));
        statPlayerNotFoundMessage = ChatColor.translateAlternateColorCodes(prefix.charAt(0), yamlConfig.getString("statPlayerNotFoundMessage"));
        statMessage = yamlConfig.getStringList("stat");
        statsPlayer = yamlConfig.getStringList("statsPlayer");
        statsConsole = yamlConfig.getStringList("statsConsole");
        shulkerdupes.clear();
        chatColdDown.clear();
    }
}
