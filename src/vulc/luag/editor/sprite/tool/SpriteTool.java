package vulc.luag.editor.sprite.tool;

import vulc.bitmap.Bitmap;
import vulc.luag.editor.sprite.SpriteEditor;

public abstract class SpriteTool {

	// returns true if is editing
	public boolean onMouseDown(int x, int y, SpriteEditor editor, Bitmap<Integer> canvas) {
		return false;
	}

	public boolean onMousePress(int x, int y, SpriteEditor editor, Bitmap<Integer> canvas) {
		return false;
	}

	public boolean onMouseRelease(int x, int y, SpriteEditor editor, Bitmap<Integer> canvas) {
		return false;
	}

}
