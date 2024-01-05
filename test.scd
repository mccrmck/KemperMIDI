
k = KemperMIDI("IAC Driver", "Bus 1");
k = KemperMIDI("MIDI Monitor (Untitled)", "MIDI Monitor (Untitled)");

k = KemperMIDI("Midi Through","Midi Through Port-0")

k.fxTypeOn(\effectModA,\wahWah,true)

MIDIClient.destinations.do(_.postln)


(
Event.addEventType(\kemperFxType,{
	if(~kemperMIDI == -1,{
        "no KemperMIDI instance specified ".error;
    },{
        var kMIDI = ~kemperMIDI;
    	var page = kMIDI.prPageCheck( ~addrPg );
        var fxBits = KemperMIDI.fxKeys[ ~fxKey ];
    	var chn = kMIDI.chan - 1 + 0xB0;
    	var pkt = [
    		chn, 0x63, page,
    		chn, 0x62, 0x00,
    		chn, 0x06, fxBits[0],
    		chn, 0x26, fxBits[1],
    	];
    
    	kMIDI.midiOut.sysex( pkt.as( Int8Array ) );
    }); 
},(
    kemperMIDI: -1,
	addrPg: 50,
	fxKey: 'empty', 
))
)


(
Pbind(
    \typl,\kemperFxType,
    \dur,Pseq([0.1],inf),
    \kemperMIDI,k,
    \addrPg, \effectModA,
).play
)

e = ()

