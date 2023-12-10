KemperMIDINew {

	classvar <addrPgKeys, <addrNrKeys;
	var <>chan, <>midiOut;

	*new { |device, port, chan = 1|
		^super.newCopyArgs(chan).init(device, port);
	}

	init { |device, port|
		var cond = CondVar();

		fork {
			MIDIClient.init();
			cond.wait({ MIDIClient.initialized });
			midiOut = MIDIOut.newByName(device.asString,port.asString);
		};
	}

	set { |addrPg, addrNr, val = 0.5 |
		var page = this.prPageCheck( addrPg );
		var num  = this.prNumCheck( page, addrNr );
		var scale = (val * (16384 - 1)).asInteger;
		var msb = (scale >> 7) & 0x7F;
		var lsb = scale        & 0x7F;
		var chn = (chan - 1) + 0xB0;
		var pkt = [
			chn, 0x63, page,
			chn, 0x62, num,
			chn, 0x06, msb,
			chn, 0x26, lsb,
		];
		midiOut.sysex( pkt.as( Int8Array ) );
	}

	switch { |addrPg, addrNr, val = 0 |
		var page = this.prPageCheck( addrPg );
		var num  = this.prNumCheck( page, addrNr );
		var onOff = case
		{ val == 0 }{ 0x00 }
		{ val == 1 }{ 0x01 }
		{ "val must be 0 or 1".throw };
		var chn = (chan - 1) + 0xB0;
		var pkt = [
			chn, 0x63, page,
			chn, 0x62, num,
			chn, 0x06, 0x00,
			chn, 0x26, onOff,
		];
		midiOut.sysex( pkt.as( Int8Array ) );
	}

	fxType { |addrPg, msb, lsb|
		var page = this.prPageCheck( addrPg );
		var chn = (chan - 1) + 0xB0;
		var pkt = [
			chn, 0x63, page,
			chn, 0x62, 0x00,
			chn, 0x06, msb,
			chn, 0x26, lsb,
		];
		midiOut.sysex( pkt.as( Int8Array ) );
	}

	prPageCheck { |addrPg|
		^case
		{ addrPg.isKindOf( Integer ) }{ addrPg }
		{ addrPg.isKindOf( Symbol ) }{ addrPgKeys[ addrPg ] }
		{ "val must be an Integer or a Symbol".throw };
	}

	prNumCheck { |addrPg, addrNum|
		var page = addrPgKeys.findKeyForValue(addrPg);
		var num = addrNrKeys[page][ addrNum ] ?? { "key not found in this page".throw };
		^case
		{ addrNum.isKindOf( Integer ) }{ addrNum }
		{ addrNum.isKindOf( Symbol ) }{ num }
		{ "val must be an Integer or a Symbol".throw };
	}

	*initClass {
		var fxAddr = (
			// type: 0, // there is a special method for this sucker, see below
			onOff: 3,
			mix: 4,
			volume: 6,
			stereo: 7,
			wahManual: 8,
			frequencyShifter: 8,
			delayPitch: 8,
			wahPeak: 9,
			wahPedalRange: 10,
			wahPedalMode: 12, // range 0 - 5 according to selection
			wahTouchAttack: 13,
			fuzzImpedance: 13,
			wahTouchRelease: 14,
			wahTouchBoost: 15,
			delayCrossFeedback: 15,
			distortionDrive: 16,
			reverbFormantMix: 16,
			distortionTone: 17,
			reverbMidFrequency: 17,
			fuzzOcta: 18,
			CompressorIntensity: 18,
			noiseGateThreshold: 18,
			autoSwellCompressor: 18,
			compressorAttack: 19,
			legacyDelayBandwidth: 19,
			legacyReverbBandwidth: 19,
			fuzzTransistorShape: 20,
			modulationRatePhaserPhaserVibeFlanger: 20,
			autoSwell: 20,
			widenerTyne: 20,
			driveDefinition: 21,
			fuzzTransistorTune: 21,
			modulationDepth: 21,
			microPitchDetune: 21,
			doubleTrackerLooseness: 21,
			widenerIntensity: 21,
			modulationFeedback: 22,
			formantReverbVowel: 22,
			driveSlimDown: 23,
			fuzzDefinition: 23,
			modulationCrossover: 23,
			octaverLowCut: 23,
			modulationHyperChorusAmount: 24,
			modulationManual: 25,
			reverbFormantOffset: 25,
			springReverbSpectralBalance: 25,
			modulationPeakSpread: 26,
			wahPhaserPeakSpread: 26,
			reverbFormantPeak: 26,
			modulationStages: 27,
			wahPhaserStages: 27,
			legacyReverbRoomSize: 27,
			rotarySpeed: 30,
			rotaryDistance: 31,
			rotaryLowHighBalance: 32,
			compressorSquash: 33,
			legacyDelayFrequency: 33,
			legacyReverbMidFrequency: 33,
			graphicEQGain80Hz: 34,
			graphicEQGain160Hz: 35,
			graphicEQGain320Hz: 36,
			graphicEQGain640Hz: 37,
			graphicEQGain1250Hz: 38,
			graphicEQGain2500Hz: 39,
			graphicEQGain5000Hz: 40,
			graphicEQGain10000Hz: 41,
			studioEQ1: 42,                         // check this
			metalEQ1: 42,                         // check this
			metalDSLowGain: 42,
			acousticSimulatorBody: 42,
			studioEQLowFrequency: 43,
			studioEQ2: 44,                         // check this
			metalEQ2: 44,                         // check this
			metalDSHighGain: 44,
			acousticSimulatorSparkle: 44,
			studioEQHighFrequency: 45,
			studioEQMid1_1: 46,                        // check this
			metalEQ3: 46,                        // check this
			metalDSMiddleGain: 46,
			acousticSimulatorBronze: 46,
			studioEQMid1_2: 47,                        // check this
			metalEQ4: 47,                        // check this
			metalDSMiddleFrequency: 48,
			studioEQMid1Q: 48,                         // check this
			studioEQMid2Gain: 49,
			acousticSimulatorPickup: 49,
			studioEQMid2Frequency: 50,
			studioEQMid2Q: 51,
			wahPeakRange: 52,
			ducking: 53,
			mix2: 54,              // (Pitch Mix / Octaver Mix / Delay Mix Serial / Crystal Mix / Space Intensity)
			voiceBalance: 55,
			delayBalance: 55,
			voice1Pitch: 56,
			toePitch: 56,
			transposePitch: 56,
			quadDelayVoicePitch4: 56,
			delayCrystal1Pitch: 56,
			voice2Pitch: 57,
			heelPitch: 57,
			quadDelayVoicePitch3: 57,
			wahFormantPitchShift: 57,
			delayCrystal2Pitch: 57,
			pitchDetune: 58,
			detuneExceptMicroPitch: 58,
			smoothChords: 60,
			pureTuning: 61,
			voice1Interval: 62,          // values according to selection
			quadDelayVoice4Interval: 62, // values according to selection
			voice2Interval: 63,          // values according to selection
			quadDelayVoice3Interval: 63, // values according to selection
			key: 63,                     // values according to selection
			formantShiftFreeze: 65,
			formantShiftOffset: 66,
			equalizerLowCut: 67,
			equalizerHighCut: 68,
			reverbHighCut: 68,
			mix3: 69,              // (delay and reverb effects)
			mixPrePost: 70,          // (delay / reverb effects and effect loop)
			delayTime: 71,
			delay1Time: 71,
			reverbRoomSize: 71,
			reverbAttackTime: 71,
			springReverbAttackTime: 71,
			delay2Time: 72,
			reverbPredelayTime: 72,
			delay2Ratio: 73,
			quadDelayDelay3Ratio: 73,
			rateFlangerOneway: 73,
			phaserOneway: 73,
			quadDelay2Ratio: 74,
			delayRatioSerial: 74,
			quadDelay1Ratio: 75,
			delayNoteValue1: 76,
			quadDelayNoteValue4: 76,
			delayNoteValue2: 77,
			quadDelayNoteValue3: 77,
			reverbPredelayNoteValue: 77,
			quadDelayNoteValue2: 78,
			noteValueSerial: 78,
			quadDelayNoteValue1: 79,
			toTempo: 80,
			equalizerSteepLow: 80,
			delayVolume4: 81,
			delayVolume3: 82,
			delayVolume2: 83,
			delayVolume1: 84,
			delayPanorama4: 85,
			delayPanorama3: 86,
			delayPanorama2: 87,
			delayPanorama1: 88,
			voicePitch2: 89,
			crystalPitch: 89,
			voicePitch1: 90,
			voice3Interval: 91,  // values according to selection
			voice4Interval: 92,  // values according to selection
			delayFeedback: 93,
			delayFeedback1: 93,
			reverbDecayTime: 93,
			infinityFeedback: 94,
			infinity: 95,
			feedback2: 96,
			feedbackSerial: 96,
			reverbLowBoost: 96,
			echoReverbFeedback: 96,
			ionosphereReverbBuildup: 96,
			delayFeedbackSync: 97,
			delaylowCut: 98,
			reverbLowDelay: 98,
			delayHighCut: 99,
			reverbHighDecay: 99,
			reverbHighDamp: 99,
			fuzzTrueImpedance: 99,
			delayCutMore: 100,
			equalizerSteepHigh: 100,
			fullOCHPLP: 100,
			upperLowerEffectLoop: 100,
			modulation: 101,          // (delay and reverb effects)
			delayChorus: 102,
			delayFlutterIntensity: 103,
			reverbModulation: 103,   // standard reverbs
			delayFlutterRate: 104,
			reverbEarlyDiffusion: 104,
			springReverbDripston: 104,
			delayGrit: 105,
			reverbBrass: 105,
			springReverbDistortion: 105,   // (dwell)
			reverseMix: 106,
			inputSwell: 107,          // (delay and reverb effects)
			smear: 108,
			duckingPrePost: 109,
		);

		addrPgKeys = (
			rig: 4,
			inputSection: 9,
			amplifier: 10,
			equalizer: 11,
			cabinet: 12,
			effectModA: 50,
			effectModB: 51,
			effectModC: 52,
			effectModD: 53,
			effectModX: 56,
			effectModMOD: 58,
			effectModDLY: 60,
			effectModREV: 61,
			userScales: 118,
			looperAndModFreeze: 125,
			systemGlobal: 127,
		);

		addrNrKeys = (
			rig: (
				tempoBpm: 0,
				rigVolume: 1,
				tempoEnable: 2,
				panorama: 3,
				transpose: 4,  // "range 28-100"
				volumePedalLocation: 68,  // "values 0-4 in selection order"?
				volumePedalRange: 69,
				parallelPathEnable: 71,
				parallelPathMix: 72,
				rigSpilloverOff: 73,
				dlyRevRouting: 74,
			),
			inputSection: (
				noiseGateIntensity: 3,
				cleanSense: 4,
				distortionSense: 5,
			),
			amplifier: (
				onOff: 2,
				ampVolume: 3,
				gain: 4,
				definition: 6,
				clarity: 7,
				powerSagging: 8,
				pick: 9,
				compressor: 10,
				tubeShape: 11,
				tubeBias: 12,
				directMix: 15
			),
			equalizer: (
				bass: 4,
				middle: 5,
				treble: 6,
				presence: 7,
				positionPrePost: 8,
			),
			cabinet: (
				onOff: 2,
				highShift: 4,
				lowShift: 5,
				character: 6,
				pureCabinet: 7,
				koneImprintSelect: 8, // "values 0 - 18 Speaker ImprintsTM in selection order, 126 Full-Range, 127 Global Imprint"
			),
			effectModA: fxAddr,
			effectModB: fxAddr,
			effectModC: fxAddr,
			effectModD: fxAddr,
			effectModX: fxAddr,
			effectModMOD: fxAddr,
			effectModDLY: fxAddr,
			effectModREV: fxAddr,
			userScales: (                    // All User Scale steps have value range 26 – 99.
				scale1_0: 0,
				scale1_1: 1,
				scale1_2: 2,
				scale1_3: 3,
				scale1_4: 4,
				scale1_5: 5,
				scale1_6: 6,
				scale1_7: 7,
				scale1_8: 8,
				scale1_9: 9,
				scale1_10: 10,
				scale1_11: 11,
				scale2_0: 0,
				scale2_1: 1,
				scale2_2: 2,
				scale2_3: 3,
				scale2_4: 4,
				scale2_5: 5,
				scale2_6: 6,
				scale2_7: 7,
				scale2_8: 8,
				scale2_9: 9,
				scale2_10: 10,
				scale2_11: 11,
			)
			looperAndModFreeze: (
				looperRecord: 88,
				looperPlayback: 88,
				looperOverdub: 88,
				looperStop: 89,
				looperTrigger: 90,
				looperReverse: 91,
				looperHalfSpeed: 92,
				looperCancel: 93,
				reactivateOverdub: 93,
				looperEraseLoop: 94,
				effectModAFreeze: 107,
				effectModBFreeze: 108,
				effectModCFreeze: 109,
				effectModDFreeze: 110,
				effectModXFreeze: 111,
				effectModMODFreeze: 113,
				effectModDLYFreeze: 114,
				effectModREVFreeze: 115,
			),
			systemGlobal: (
				mainVolume: 0,
				headphoneVolume: 1,
				monitorVolume: 2,
				directOutVolume: 3,
				send1Volume: 3,
				SPDIFVolume: 4,
				monitorCabOff: 8,
				mainEQBass: 12,
				mainEQMiddle: 13,
				mainEQTreble: 14,
				mainEQPresence: 15,
				outputFilterLowCut: 16,
				monitorEQBass: 17,
				monitorEQMiddle: 18,
				monitorEQTreble: 19,
				monitorEQPresence: 20,
				outputFilterHighCut: 21,
				auxInMain: 32,
				auxInMonitor: 33,
				auxInHeadphone: 34,
				spaceIntensity: 36,
				spaceRouting: 37,
				koneMode: 38,
				koneBassBoost: 39,
				koneImprintSelect: 40,  // range 0 - 18 according to collection
				koneDirectivity: 41,
				koneSweetening: 42,
				inputSource: 44,     // range 0 - 3 according to selection
				pureCabinetEnable: 50,
				pureCabinetLevel: 51,
				looperVolume: 52,
				looperLocation: 53,
				auxInMono: 59,
			),
		);
	}
}