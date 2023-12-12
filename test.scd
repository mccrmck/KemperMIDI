

k = KemperMIDI("IAC Driver", "Bus 1")
k = KemperMIDI("MIDI Monitor (Untitled)", "MIDI Monitor (Untitled)")

MIDIClient.restart
(
Event.addEventType(\kemperSet,{
	var kMIDI = ~kemperMIDI ?? { "must include an instance of KemperMIDI".throw };
	var page = kMIDI.prPageCheck( ~addrPg );
	var num  = kMIDI.prNumCheck( page, ~addrNr );
	var chn = (~chan - 1) + 0xB0;
	var scale = (~val * (16384 - 1)).asInteger;
	var msb = (scale >> 7) & 0x7F;
	var lsb = scale        & 0x7F;
	var pkt = [
		chn, 0x63, page,
		chn, 0x62, num,
		chn, 0x06, 0,
		chn, 0x26, 1,
	];

	kMIDI.midiOut.sysex( pkt.as( Int8Array ) );
},(
	kemperMidi: nil,
	addrPg: 4,
	addrNr: 1,
	chan: 1,
	val: 0.5

	// midicmd: \sysex,
	// midiout: k.midiOut,
	// array: ,
))
)




(type: 'kemperSet', kemperMIDI: k, chan: 2).play

Event