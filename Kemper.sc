KemperMIDI {

	classvar <cues, <>midiOut;

	*initClass {
		cues = IdentityDictionary();
	}

	*new { | device, port |
		^super.new.init(device, port);
	}

	init { | device, port |
		var server = Server.default;
		device = device.asString;
		port = port.asString;

		Routine({
			MIDIClient.init;
			server.sync;
			midiOut = MIDIOut.newByName(device,port);
		}).play;
	}

	*loadFromMIDI { |key, path, loopKey|
		var pathToMIDI = path.asString;

		if( pathToMIDI.extension == "mid",{
			var bool;
			var times, cmds, chans, nums, vals;
			var file = SimpleMIDIFile.read(pathToMIDI)
			.timeMode_(\seconds)
			.midiEvents;

			if(file.size > 0,{
				bool  = [1];
				times = file.collect({ |event| event[1] }).differentiate.add(0.0);
				cmds  = file.collect({ |event| event = event.replace(\cc,'control'); event[2] });
				chans = file.collect({ |event| event[3] });
				nums  = file.collect({ |event| event[4] });
				vals  = file.collect({ |event| event[5] ? 0 });

			},{
				bool = times = cmds = chans = nums = vals = [ nil ];
			});

			cues.put(key.asSymbol,
				(
					bool:  bool.unbubble.asBoolean,
					times: times,
					cmds:  cmds,
					chans: chans,
					nums:  nums,
					vals:  vals
				)
			);

			if( loopKey.notNil,{ cues[key.asSymbol].put(loopKey.asSymbol, true) });
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

		if( MIDIClient.initialized,{

			if( cues[uniqueKey]['bool'],{
				var times = cues[uniqueKey]['times'];
				var cmds  = cues[uniqueKey]['cmds'];
				var chans = cues[uniqueKey]['chans'];
				var nums  = cues[uniqueKey]['nums'];
				var vals  = cues[uniqueKey]['vals'];

				if(loopKey.notNil,{
					var pattern = Pseq([
						Pbind(
							\dur, Pseq([times[0]]),
							\note, Rest()
						),
						Pbind(
							\type,\midi,
							\midiout,midiOut,
							\dur, Pseq( times ),
							\midicmd, Pwhile({ cues[uniqueKey].at(loopKey.asSymbol) }, Pseq( cmds ) ) ,
							\chan, Pseq( chans ),   // 0-15

							\nums, Pseq( nums ),
							\vals, Pseq( vals ),
							\dummy, Pfunc({ |event|
								if(event['midicmd'] == 'program',{
									event.put('progNum',event['nums']);
								},{
									event.put('ctlNum',event['nums']);
									event.put('control',event['vals']);
								});
							})
						)
					]);
					cues[uniqueKey].put('pattern',pattern)
				},{
					var pattern = Pseq([
						Pbind(
							\dur, Pseq([times[0]]),
							\note, Rest()
						),
						Pbind(
							\type,\midi,
							\midiout,midiOut,
							\dur, Pseq( times[1..] ),
							\midicmd, Pseq( cmds ),
							\chan, Pseq( chans ),   // 0-15

							\nums, Pseq( nums ),
							\vals, Pseq( vals ),
							\dummy, Pfunc({ |event|
								if(event['midicmd'] == 'program',{
									event.put('progNum',event['nums']);
								},{
									event.put('ctlNum',event['nums']);
									event.put('control',event['vals']);
								});
							})
						)
					]);
					cues[uniqueKey].put('pattern',pattern)
				})
			},{
				var pattern = Pbind(
					\dur,Pseq([0],1),
					\note, Rest(0.01)
				);
				cues[uniqueKey].put('pattern',pattern)
			});
		},{
			"MIDIClient not initialized, KemperMIDI not loaded".postln;
		});

		^cues[uniqueKey]['pattern']
	}
}