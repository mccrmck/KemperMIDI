

(
var path = "/Users/mikemccormick/Downloads/Hvit flæsh.mid";
~dmxTest = SimpleMIDIFile.read(path)
.timeMode_(\seconds)
)


~dmxTest.midiEvents.do(_.postln)
.select({ |e| e[2] == \noteOff }).do(_.postln)

~noteOn = ~dmxTest.midiEvents.select({ |e| e[2] == \noteOn })
~cc = ~dmxTest.midiEvents.select({ |e| e[2] == \cc })
~nTimes[..10].do(_.postln)

(
~nTimes = ~noteOn.collect({|e| e[1]}).differentiate;
~nChan = ~noteOn.collect({|e| e[3]});
~nNote = ~noteOn.collect({ |e| e[4]});
~nAmp = ~noteOn.collect({ |e| e[5] / 127 });
)

~nTimes.removeAt(1)


// first event must be offset if  nTimes[0] != 0
Pdef(\notes,
	Pbind(
		\type,\vst_midi,
		\vst,DMXIS.vst,
		\dur,Pseq(~nTimes[..5]),
		\midicmd,\noteOn,
		\chan,Pseq(~nChan),
		\midinote,Pseq(~nNote),
		\amp,Pseq(~nAmp),
		// \hasGate,true
	).trace
);

(
~cTimes = ~cc.collect({|e| e[1]}).differentiate.drop(1);
~cChan = ~cc.collect({|e| e[3]});
~cNum = ~cc.collect({ |e| e[4]});
~cCtrl = ~cc.collect({ |e| e[5]});
)

~cTimes.do(_.postln)

Pdef(\control,
	Pbind(
		\type,\vst_midi,
		\vst,DMXIS.vst,
		\midicmd,\control,
		\chan,Pseq(~cChan),
		\ctlNum,Pseq(~cNum),
		\control,Pseq(~cCtrl),
		\dur,Pseq( ~cTimes, inf),
	)
);

DMXIS()
DMXIS.setPreset(0)
DMXIS.vst.editor
DMXIS.vst.midi.noteOn(0,24,0)

DMXIS.vst.midi.control(8,7,15)


Pdef(\mstr,
	Ppar([
		Pdef(\notes).play,
		Pdef(\control),
		// ~click
	])
).play

Pdef(\mstr).stop


//
(
var path = ;
~dmxTest = SimpleMIDIFile.read(path)
.timeMode_(\seconds)
)

~dmxTest.midiEvents.collect({ |event|
	var pat;
	case
	{ event[2] == \noteOn }{
		pat = Pbind(
			\type,\vst_midi,
			\vst,DMXIS.vst,
			\dur,Pseq( event[1] - event, 1),
			\midicmd,\noteOn,
			\chan, event[3],
			\midinote, event[4],
			\amp, event[5] /127 ,
		)
	}
	{ event[2] == \cc }{
		pat = Pbind(
			\type,\vst_midi,
			\vst,DMXIS.vst,
			\dur,Pseq( ~cTimes, inf),
			\midicmd,\control,
			\chan,Pseq(~cChan),
			\ctlNum,Pseq(~cNum),
			\control,Pseq(~cCtrl),
		)
	}

	pat;

})












DMXIS.vst.midi.noteOn(0,24,0);
DMXIS.vst.midi.noteOn(0,27,0);
DMXIS.vst.midi.noteOn(0,30,0);












