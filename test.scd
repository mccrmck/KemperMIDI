
k = KemperMIDI("IAC Driver", "Bus 1");
k = KemperMIDI("MIDI Monitor (Untitled)", "MIDI Monitor (Untitled)");

k = KemperMIDI("SuperCollider","in0");

(
Event.addEventType(\kemperSwitch,{
	if(~kemperMIDI == -1,{
        "no KemperMIDI instance specified ".error;
    },{
        var kMIDI = ~kemperMIDI;
    	var page = kMIDI.prPageCheck( ~addrPg );
    	var num  = kMIDI.prNumCheck( page, ~addrNr );
    	var chn = kMIDI.chan - 1 + 0xB0;
    	var val = ~val.round.clip(0,1).asInteger; 
    	var pkt = [
    		chn, 0x63, page,
    		chn, 0x62, num,
    		chn, 0x06, 0x00,
    		chn, 0x26, val,
    	];
    
    	kMIDI.midiOut.sysex( pkt.as( Int8Array ) );
    }); 
},(
    kemperMIDI: -1,
	addrPg: 4,
	addrNr: 1,
	val: 0,
))
)

1.8.round.clip(0,1)

(
Pbind(
    \type,\kemperSet,
    \dur,Pseq([0.2],2),
    \kemperMIDI,k,
    \val,Pseq([0.1,0.4,0.8,1.0])
).trace.play
)



