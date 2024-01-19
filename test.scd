
k = KemperMIDI("IAC Driver", "Bus 1");
k = KemperMIDI("MIDI Monitor (Untitled)", "MIDI Monitor (Untitled)");

k = KemperMIDI("Midi Through","Midi Through Port-0")

k.fxType(\effectModA,\wahWah,false)

MIDIClient.destinations.do(_.postln)


(
Event.addEventType(\kemperSet,#{|server|
    if(~kemperMIDI == -1,{
        "no KemperMIDI instance specified".error;
    },{
        var kMIDI = ~kemperMIDI;
        var page = kMIDI.prPageCheck( ~addrPg );
        var num  = kMIDI.prNumCheck( page, ~addrNr );
        var chan = kMIDI.chan - 1 + 0xB0;
        var scale = (~val * (16384 - 1)).asInteger;
        var msb = (scale >> 7) & 0x7F;
        var lsb = scale        & 0x7F;
        var pkt = [
            chan, 0x63, page,
            chan, 0x62, num,
            chan, 0x06, msb,
            chan, 0x26, lsb,
        ];

        ~lag.postln;

        thisThread.clock.sched(~lag, kMIDI.midiOut.sysex( pkt.as( Int8Array ) ) )
    });
},(
    kemperMIDI: -1,
    addrPg: 4,
    addrNr: 1,
    val: 0.5
)
);
)


(
p = Ppar([
    Pbind(
        \type,\kemperSet,
        \dur,Pseq([60/130],3),
        \kemperMIDI,k,
        \addrPg, \effectModC,
        \addrNr,\onOff,
        \lag,4,
        ),
    Click(130,repeats:3).pattern
   ]).play
);

p
.latency
EventStreamPlayer

a = (type: \kemperSet, kemperMIDI: k, addrPg: 4, addrNr: 1,val: 0).play;

b = (type: \kemperFxType, kemperMIDI: k, addrPg: 50).play

k.switch(\effectModA,\mix,0)

(
Pbind(
    \type, \midi,
    \midicmd, \sysex,
    \midiout, k.midiOut,
    \array,[Int8Array[176,]]
)
)
k.midiOut.performList(\sysex,*[Int8Array[0xB0,0x63,50],Int8Array[0xB0,0x63,55]])


[Int8Array[0xB0,0x63,50]].asControlInput.flop

a = #{}

~run = 4
a = {|run,i| ~run.postln;i * 2}
a.valueEnvir
currentEnvironment
.sysex



k.fxType(\effectModC,\wahWah,true)
