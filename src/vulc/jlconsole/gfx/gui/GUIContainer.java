package vulc.jlconsole.gfx.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import vulc.jlconsole.Console;
import vulc.jlconsole.gfx.Screen;
import vulc.jlconsole.input.InputHandler;
import vulc.jlconsole.input.InputHandler.Key;
import vulc.jlconsole.input.InputHandler.KeyType;

public class GUIContainer extends GUIComponent {

	protected final List<GUIComponent> comps = new ArrayList<GUIComponent>();

	private final Console console;
	public final InputHandler input = new InputHandler();
	public final Screen screen;
	public int xInputOff = 0, yInputOff = 0;

	protected final List<Character> keyBuffer = new ArrayList<Character>();
	protected final KeyAdapter keyListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			keyBuffer.add(e.getKeyChar());
		}
	};

	protected final Key mouse1 = input.new Key(KeyType.MOUSE, MouseEvent.BUTTON1);

	public GUIContainer(Console console, int x, int y, int w, int h) {
		super(x, y, w, h);
		this.console = console;
		this.screen = new Screen(w, h);
		input.init(console);
		console.addKeyListener(keyListener);
	}

	public void tick() {
		while(keyBuffer.size() != 0) {
			char c = keyBuffer.remove(0);
			for(GUIComponent comp : comps) {
				comp.onKeyPress(c);
			}
		}
		if(mouse1.isKeyDown()) {
			if(this.isPressed(input.xMouse / Console.SCALE - xInputOff,
			                  input.yMouse / Console.SCALE - yInputOff)) {
				this.press();
			}
		}
		input.tick();
	}

	@Override
	public void render(Screen screen) {
		this.screen.clear(background);
		for(GUIComponent comp : comps) {
			comp.render(this.screen);
		}
		screen.draw(this.screen, x, y);
	}

	public void add(GUIComponent comp) {
		comps.add(comp);
		if(comp instanceof GUIContainer) {
			GUIContainer container = (GUIContainer) comp;
			container.xInputOff = x;
			container.yInputOff = y;
		}
	}

	public void remove(GUIComponent comp) {
		comps.remove(comp);
	}

	@Override
	public void onRemove(GUIContainer container) {
		super.onRemove(container);
		input.remove();
		console.removeKeyListener(keyListener);
	}

	@Override
	public void press() {
		for(GUIComponent comp : comps) {
			if(comp.isPressed(input.xMouse / Console.SCALE - xInputOff,
			                  input.yMouse / Console.SCALE - yInputOff)) {
				comp.focused = true;
				comp.press();
			} else {
				comp.focused = false;
			}
		}
	}

}