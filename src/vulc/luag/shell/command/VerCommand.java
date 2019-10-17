package vulc.luag.shell.command;

import vulc.luag.Console;
import vulc.luag.shell.Shell;

public class VerCommand extends ShellCommand {

	public VerCommand() {
		super("ver", "version");
	}

	public void run(Shell shell, String[] args) {
		shell.write(Console.VERSION + " - By Vulcalien\n\n");
	}

}