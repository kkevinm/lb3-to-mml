package com.github.kevinm.lb3tomml.lb3;

import com.github.kevinm.lb3tomml.mml.MmlCommand;
import com.github.kevinm.lb3tomml.mml.MmlMacro;
import com.github.kevinm.lb3tomml.mml.MmlSymbol;
import com.github.kevinm.lb3tomml.util.Log;

public class PanCommand extends HexCommand {

	public PanCommand(int value) {
		super(value);

		if (value < 0x60 || value > 0x7f) {
			throw new IllegalArgumentException(String.format("Invalid pan command: 0x%02x", value));
		}
	}

	@Override
	public MmlCommand process(SongChannel channel) {
		Log.log("Processing pan command 0x%02x", value);

		String cmd;
		String pan;
		if (value == 0x60 || value == 0x70) {
			cmd = MmlSymbol.PAN;
			pan = "10";
		} else if (value < 0x70) {
			cmd = MmlMacro.PAN;
			pan = "L" + (value - 0x60);
		} else {
			cmd = MmlMacro.PAN;
			pan = "R" + (value - 0x70);
		}

		MmlCommand result = new MmlCommand(cmd, pan);
		Log.logIndent("Converted to: %s", result.toString());
		return result;
	}

}
