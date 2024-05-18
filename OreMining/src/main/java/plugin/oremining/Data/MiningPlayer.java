package plugin.oremining.Data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


public class MiningPlayer {
  private String playerName;
  private int score;
  private int gameTime;

  public MiningPlayer(String playerName) {
    this.playerName = playerName;
  }
}
