package plugin.oremining;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Score;
import plugin.oremining.Data.MiningPlayer;
import plugin.oremining.Data.PlayerScore;

public class GameStartCommand implements CommandExecutor , Listener {

  private final Main main;
  private Player player;
  private int gameTime;
  private static final String LIST = "list";
  private final PlayerScore playerScoreData = new PlayerScore();

  private final List<PlayerScore> playerScoreList = new ArrayList<>();
  private final List<MiningPlayer> executingPlayerList = new ArrayList<>();
  public GameStartCommand(Main main) {
    this.main = main;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 1 && LIST.equals(args[0])){
      try (Connection con = DriverManager.getConnection(
          "jdbc:mysql://localhost:3306/spigot_server",
          "root",
          "qSJ0");
          Statement statement = con.createStatement();
          ResultSet resultset = statement.executeQuery("select * from ore_score;")) {
        while (resultset.next()) {
          int id = resultset.getInt("id");
          String name = resultset.getString("player_name");
          int score = resultset.getInt("score");
          player.sendMessage(
              id + " | " + name + " | " + score );
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return false;

    }
    if (sender instanceof Player player) {
      if (playerScoreList.isEmpty()) {
        addNewPlayer(player);
      }else {
        for(PlayerScore playerScore : playerScoreList){
          if(!playerScore.getPlayerName().equals(player.getName())){
            addNewPlayer(player);
          }
        }
      }

      this.player = player;
      gameTime = 20 ;

      PlayerInventory inventory = player.getInventory(); //ピッケルに持ち替える
      inventory.setItemInMainHand(new ItemStack(Material.NETHERITE_PICKAXE));

      new BukkitRunnable() {
        @Override
        public void run() {
          if (gameTime <= 0) {
            cancel();
            player.sendMessage("ゲーム終了！合計スコア: " + playerScoreData + "点");

          } else {
            gameTime--;
          }
        }
      }.runTaskTimer(main, 0, 20);
    }


    return false;
  }

  private void addNewPlayer(Player player) {
    PlayerScore newPlayer = new PlayerScore();
    newPlayer.setPlayerName(player.getName());
    playerScoreList.add(newPlayer);
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    Player player = e.getPlayer();
    Block block = e.getBlock();

    if (Objects.isNull(player) || playerScoreList.isEmpty()){
      return;
    }

    playerScoreList.stream()
        .filter(playerScore -> playerScore.getPlayerName().equals(player.getName()))
        .findFirst()
        .ifPresent(miningPlayer -> {
          Material type = e.getBlock().getType();
          int point = switch (block.getType()) {
            case DIAMOND_ORE -> 100;
            case IRON_ORE -> 5;
            case COAL_ORE -> 2;
            case COPPER_ORE -> 3;
            case GOLD_ORE -> 50;
            case LAPIS_ORE -> 20;
            case EMERALD_ORE -> 150;
            default -> 0;
          };
          if (point > 0) {
            miningPlayer.setScore(miningPlayer.getScore() + point);
            player.sendMessage(player.getName() + " " + point + "点。現在のスコア: " + miningPlayer.getScore() + "点");
          }
        });
  }

  

}
