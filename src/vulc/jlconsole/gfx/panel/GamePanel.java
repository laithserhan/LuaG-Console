package vulc.jlconsole.gfx.panel;

import vulc.jlconsole.Console;
import vulc.jlconsole.game.Game;

public class GamePanel extends BootablePanel {

	private final Game game;

	public GamePanel(Console console) {
		super(console);
		this.game = new Game(console);
	}

	public void preInit() {
		game.init();
	}

	public void tick() {
		game.tick();
	}

}
