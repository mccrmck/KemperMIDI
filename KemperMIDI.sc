KemperMIDI {

	classvar <addrPgKeys, <addrNrKeys, <fxKeys;
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

	fxType { |addrPg, fxKey|
		var page = this.prPageCheck( addrPg );
		var chn = (chan - 1) + 0xB0;
        var fxBits = fxKeys[ fxKey.asSymbol ];
		var pkt = [
			chn, 0x63, page,
			chn, 0x62, 0x00,
			chn, 0x06, fxBits[0],
			chn, 0x26, fxBits[1],
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
		^case
		{ addrNum.isKindOf( Integer ) }{ addrNum }
		{ addrNum.isKindOf( Symbol ) }{ addrNrKeys[page][ addrNum ] ?? { "key not found in this page".throw } }
		{ "val must be an Integer or a Symbol".throw };
	}

	*initClass {
		var fxAddr = (
			// type: 0, // there is a special method for this sucker, see above
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
			compressorIntensity: 18,
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

		fxKeys = (
			empty:             [0, 0],
			wahWah:            [0, 1],
			wahLowPass:        [0, 2],
			wahHighPass:       [0, 3],
			wahVowelFilter:    [0, 4],
			wahPhaser:         [0, 6],
			wahFlanger:        [0, 7],
			wahRateReducer:    [0, 8],
			wahRingModulator:  [0, 9],
			wahFreqShifter:    [0,10],
			pedalPitch:        [0,11],
			wahFormantShifter: [0,12],
			pedalVinylStop:    [0,13],
			bitShaper:         [0,17],
			octaShaper:        [0,18],
			softShaper:        [0,19],
			hardShaper:        [0,20],
			waveShaper:        [0,21],
			kemperDrive:       [0,32],
			greenScream:       [0,33],
			plusDS:            [0,34],
			oneDS:             [0,35],
			muffin:            [0,36],
			mouse:             [0,37],
			kemperFuzz:        [0,38],
			metalDS:           [0,39],
			fullOC:            [0,42],
			compressor:        [0,49],
			autoSwell:         [0,50],
			noiseGate2_1:      [0,57],
			noiseGate4_1:      [0,58],
			space:             [0,64],
			vintageChorus:     [0,65],
			hyperChorus:       [0,66],
			airChorus:         [0,67],
			vibrato:           [0,68],
			rotarySpeaker:     [0,69],
			tremolo:           [0,70],
			microPitch:        [0,71],
			phaser:            [0,81],
			phaserVibe:        [0,82],
			phaserOneway:      [0,83],
			flanger:           [0,89],
			flangerOneway:     [0,91],
			graphicEqualizer:  [0,97],
			studioEqualizer:   [0,98],
			metalEqualizer:    [0,99],
			acousticSimulator: [0,100],
			stereoWidener:     [0,101],
			phaseWidener:      [0,102],
			delayWidener:      [0,103],
			doubleTracker:     [0,104],
			trebleBooster:     [0,113],
			leadBooster:       [0,114],
			pureBooster:       [0,115],
			wahPedalBooster:   [0,116],
			loopMono:          [0,121],
			loopStereo:        [0,122],
			loopDistortion:    [0,123],
			transpose:         [1, 1],
			chromaticPitch:    [1, 2],
			harmonicPitch:     [1, 3],
			analogOctaver:     [1, 4],
			dualChromatic:     [1, 9],
			dualHarmonic:      [1,10],
			dualCrystal:       [1,11],
			dualLoopPitch:     [1,12],
			legacyDelay:       [1,17],
			singleDelay:       [1,18],
			dualDelay:         [1,19],
			twoTapDelay:       [1,20],
			serialTwoTapDelay: [1,21],
			crystalDelay:      [1,22],
			loopPitchDelay:    [1,23],
			freqShifterDelay:  [1,24],
			rhythmDelay:       [1,33],
			melodyChromatic:   [1,34],
			melodyHarmonic:    [1,35],
			quadDelay:         [1,36],
			quadChromatic:     [1,37],
			quadHarmonic:      [1,38],
			legacyReverb:      [1,49],
			naturalReverb:     [1,50],
			easyReverb:        [1,51],
			echoReverb:        [1,52],
			cirrusReverb:      [1,53],
			formantReverb:     [1,54],
			ionosphereReverb:  [1,55],
			springReverb:      [1,65],
		);
        
        // let's add some events while we're at it
        Event.addEventType(\kemperSet,{
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

                kMIDI.midiOut.sysex( pkt.as( Int8Array ) );
            });
        },(
            kemperMIDI: -1,
        	addrPg: 4,
            addrNr: 1,
            val: 0.5
        ));

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
        ));
    }
}
