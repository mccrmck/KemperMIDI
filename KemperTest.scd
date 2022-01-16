
MIDIClient.init;
MIDIClient.destinations;

~midiOut = MIDIOut(0)


var path = "/Users/mikemccormick/Library/Application Support/SuperCollider/Extensions/YAWN/songs/numberFour/kemperMIDI/buildup.mid";
x = SimpleMIDIFile.read(path)
.timeMode_(\seconds)
.midiEvents;

x.midiEvents[..40].do(_.postln)

x.size

y = x.midiEvents.select({ |e| e[2] == \cc })
z = x.midiEvents.select({ |e| e[2] == \program })

(
~cTimes = y.collect({ |e| e[1]}).differentiate.rotate(1).drop(1);
~cChan = y.collect({ |e| e[3]});
~ctlNum = y.collect({ |e| e[4]});
~cVal = y.collect({ |e| e[5]});
)

Pdef(\test,
	Pbind(
		\type,\midi,
		\midiout,~midiOut,
		\midicmd, \control,

		\chan,Pseq(~cChan), // 0-15
		\ctlNum,Pseq(~ctlNum), //controller number to receive value
		\control,Pseq(~cVal), // val
		\dur,Pseq( ~cTimes, inf),
	)
);


(
~pTimes = z.collect({|e| e[1]}).differentiate.drop(1);
~pChan = z.collect({|e| e[3]});
~pgmNum = z.collect({ |e| e[4]});
)

Pdef(\test1,
	Pbind(
		\type,\midi,
		\midiout,~midiOut,
		\midicmd, \program,

		\chan,Pseq( ~pChan ), //0-15
		\progNum,Pseq(~pgmNum), //0-127
		\dur,Pseq(~pTimes, inf),
	)
);


Pdef(\mstr,
	Ppar([
		Pdef(\test),
		Pdef(\test1),
		~click
	])
).play


Pdef(\mstr).play
~click = ~yawn.songArray[3].cueFrom('fourOutro','fourOutro',true,false,true)

~yawn.songArray[3].cueFrom(,)

~click.stop
Pdef(\test1).stop



Pdef(\mstr,
	Ppar([

		Pdef(\test,
			Pbind(
				\type,\midi,
				\midiout,~midiOut,
				\midicmd, \control,

				\chan,Pseq(~cChan), // 0-15
				\ctlNum,Pseq(~ctlNum), //controller number to receive value
				\control,Pseq(~cVal), // val
				\dur,Pseq( ~cTimes, inf),
			)
		),
		Pdef(\test1,
			Pbind(
				\type,\midi,
				\midiout,~midiOut,
				\midicmd, \program,

				\chan,Pseq( ~pChan ), //0-15
				\progNum,Pseq(~pgmNum), //0-127
				\dur,Pseq(~pTimes, inf),
			)
		),

	])
)
