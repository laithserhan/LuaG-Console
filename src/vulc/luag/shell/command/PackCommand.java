package vulc.luag.shell.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import vulc.luag.game.Game;
import vulc.luag.shell.Shell;

public class PackCommand extends ShellCommand {

	public PackCommand() {
		super("pack");

		isDevelopersOnly = true;
	}

	public void run(Shell shell, String[] args) {
		if(args.length < 1) {
			shell.write("Error: missing arguments\n"
			          + "pack [cartridge-name]\n\n");
			return;
		}

		File consoleUserdata = new File(Game.USERDATA_DIR);
		if(!consoleUserdata.isDirectory()) {
			shell.write("Error:\n"
			          + "'" + Game.USERDATA_DIR + "'\n"
			          + "folder not found\n\n");
			return;
		}

		File cartridge = new File(args[0] + "." + Game.CARTRIDGE_EXTENSION);
		if(cartridge.exists()) {
			shell.write("Error:\n"
			          + "'" + cartridge + "'\n"
			          + "file already exists\n\n");
			return;
		}

		try {
			cartridge.createNewFile();
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(cartridge));
			File[] files = new File(Game.USERDATA_DIR).listFiles();
			for(File f : files) {
				addToZip(out, "", f);
			}
			out.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

	}

	private void addToZip(ZipOutputStream zip, String folder, File file) throws IOException {
		if(file.isFile()) {
			ZipEntry entry = new ZipEntry(folder + file.getName());
			zip.putNextEntry(entry);

			InputStream in = new FileInputStream(file);
			byte[] dataBuffer = new byte[1024];
			int lengthRead;

			while((lengthRead = in.read(dataBuffer)) >= 0) {
				zip.write(dataBuffer, 0, lengthRead);
				zip.flush();
			}
			in.close();

			zip.closeEntry();
		} else if(file.isDirectory()) {
			File[] files = file.listFiles();
			for(File f : files) {
				addToZip(zip, folder + file.getName() + "/", f);
			}
		}
	}

}