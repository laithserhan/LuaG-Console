package vulc.luag.game.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import vulc.luag.Console;
import vulc.luag.game.Game;

public class LuaScriptCore {

	private final LuaInterface luaInterface = new LuaInterface();

	private Console console;

	private LuaFunction luaTick;

	public void init(Console console, Game game) {
		this.console = console;
		Globals globals = JsePlatform.standardGlobals();

		luaInterface.init(console, game, globals);

		try {
			InputStream input;

			if(console.cartridge == null) {
				File mainFile = new File(Game.SCRIPT_DIR + "/main.lua");
				if(!mainFile.exists()) {
					console.die("Error:\n"
					            + "'" + Game.SCRIPT_DIR_NAME + "/main.lua'"
					            + "file not found");
					return;
				}

				input = new FileInputStream(mainFile);
			} else {
				ZipEntry mainLuaEntry = game.cartridgeFile.getEntry("script/main.lua");
				if(mainLuaEntry == null || mainLuaEntry.isDirectory()) {
					console.die("Cartridge Error:\n"
					            + "'" + Game.SCRIPT_DIR_NAME + "/main.lua'"
					            + "file not found");
					return;
				}

				input = game.cartridgeFile.getInputStream(mainLuaEntry);
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			String code = "";
			String line = "";
			while((line = reader.readLine()) != null) {
				code += line + '\n';
			}
			reader.close();

			globals.load(code).call();
		} catch(LuaError e) {
			handleError(e);
			return;
		} catch(IOException e) {
			e.printStackTrace();
			return;
		}

		// LOAD TICK FUNCTION
		try {
			LuaValue tick = globals.get("tick");
			if(tick == LuaValue.NIL || !tick.isfunction()) {
				console.die("Error:\n"
				            + "'main.lua' must contain\n"
				            + "a function 'tick()'");
				return;
			} else {
				luaTick = tick.checkfunction();
			}

			LuaValue init = globals.get("init");
			if(init == LuaValue.NIL || !init.isfunction()) {
				console.die("Error:\n"
				            + "'main.lua' must contain\n"
				            + "a function 'init()'");
				return;
			} else {
				init.checkfunction().call();
			}
		} catch(LuaError e) {
			handleError(e);
			return;
		}
	}

	public void tick() {
		try {
			luaTick.call();
		} catch(LuaError e) {
			handleError(e);
		}
	}

	private void handleError(LuaError e) {
		// BUG errors will print the code, not the file name
		console.die("Script Error:\n"
		            + "see the terminal");

		String sep = "[\\\\/]";

		String msg = "LuaError: "
		             + e.getLocalizedMessage()
		                .replace("@", "")
		                .replaceAll("\\.?" + sep + Game.USERDATA_DIR.replace("./", ""), "");
		System.err.println(msg);
	}

}
