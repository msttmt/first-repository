package plugin.oremining.Data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * プレイヤーのスコア情報を扱うクラス
 * プレイヤー名、点数を扱う
 */
@Getter
@Setter

public class PlayerScore {
  private int score;
  private String playerName;
}
