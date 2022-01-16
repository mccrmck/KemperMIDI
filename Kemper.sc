KemperMIDI {

	classvar <cues, <>midiOut;

	*initClass {
		cues = IdentityDictionary();
	}

	*new { | midiDeviceName |
		^super.new.init(midiDeviceName);
	}

	init { | device |
		var server = Server.default;
		Routine({
			MIDIClient.init;
			server.sync;
			midiOut = MIDIOut.newByName(device.asString)
		}).play;
	}

	*loadFromMIDI { |key, path, loopKey|
		var pathToMIDI = path.asString;

		if(pathToMIDI.extension == "mid",{
			var file = SimpleMIDIFile.read(pathToMIDI)
			.timeMode_(\seconds)
			.midiEvents;

			var cTimes, cChan, cNum, cVal, pTimes, pChan, pNum;

			var cc = file.select({ |event| event[2] == \cc });
			var program = file.select({ |event| event[2] == \program });

			cTimes = cc.collect({ |event| event[1] }).differentiate.drop(1);
			cChan  = cc.collect({ |event| event[3] });
			cNum   = cc.collect({ |event| event[4] });
			cVal   = cc.collect({ |event| event[5] });

			pTimes = program.collect({ |event| event[1] }).differentiate.drop(1);
			pChan  = program.collect({ |event| event[3] });
			pNum   = program.collect({ |event| event[4] });

			cues.put(key.asSymbol,
				(
					cTimes:  cTimes ? [0],
					cChan:   cChan ? [0],
					cNum:    cNum ? [0],
					control: cVal ? [0],

					pTimes: pTimes ? [0],
					pChan:  pChan ? [0],
					pNum:   pNum ? [0],
				)
			);

			if(loopKey.notNil,{ cues[key.asSymbol].put(loopKey.asSymbol, true) });
		},{
			"bad path, must be a .mid file!".throw;
		});

		^cues.at(key)
	}

	*makePat { |key, path = nil, loopKey|
		var uniqueKey = key.asSymbol;

		if(path.isNil,{
			if(cues[uniqueKey].isNil,{
				"no file loaded".throw;
			});
			loopKey = cues[uniqueKey].findKeyForValue(true);        // this doesn't seem that strong, could improve!
		},{
			this.loadFromMIDI(uniqueKey, path, loopKey);
		});

		if(MIDIClient.initialized,{
			var cTimes = cues[uniqueKey]['cTimes'];
			var cChan  = cues[uniqueKey]['cChan'];
			var cNum   = cues[uniqueKey]['cNum'];
			var cVal   = cues[uniqueKey]['control'];

			var pTimes = cues[uniqueKey]['pTimes'];
			var pChan  = cues[uniqueKey]['pChan'];
			var pNum   = cues[uniqueKey]['cNum'];

			if(loopKey.notNil,{
				// var pattern =
				// cues[uniqueKey].put('pattern',pattern);
			},{
				var pattern = Pdef(uniqueKey,
					Ppar([
						Pdef(\test,
							Pbind(
								\type,\midi,
								\midiout,midiOut,
								\midicmd, \control,

								\chan,Pseq(cChan),   // 0-15
								\ctlNum,Pseq(cNum),  // controller number to receive value
								\control,Pseq(cVal), // val
								\dur,Pseq( cTimes, inf),
							)
						),
						Pdef(\test1,
							Pbind(
								\type,\midi,
								\midiout,midiOut,
								\midicmd, \program,

								\chan,Pseq(pChan),   // 0-15
								\progNum,Pseq(pNum), // 0-127
								\dur,Pseq(pTimes, inf),
							)
						)
					])
				);
				cues[uniqueKey].put('pattern',pattern)
			})
		},{
			"MIDIClient not initialized".throw;
		});

		^cues[uniqueKey]['pattern']
	}
}

