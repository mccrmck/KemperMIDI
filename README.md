# KemperMIDI

A SuperCollider class for sending 14-bit MIDI to control the [Kemper](https://www.kemper-amps.com) digital guitar amplifer. (Note: this has only been tested on a Kemper Stage Profiler and on macOS)

Details about how the Kemper receives MIDI, relevant integers for the Address Pages / Address Numbers, and for a list of the available parameters can be found in the attached MIDI Parameter Documentation 8.6 (downloaded [here](https://www.kemper-amps.com/downloads/5/User-Manuals))

Basic usage:

```
// make an instance
k = KemperMIDI( "UM-ONE", "UM-ONE", 2 ) // MIDI device, MIDI port, channel
```
For "Continuous Parameters":
```
// set a parameter using integers, hex notation...
k.set( 4, 0x03, 0.2 ) // args: addrPg, addrNr, val (float between 0..1)

// ...or symbols for the addPg and addNr arguments
k.set( \rig, \panorama, 0.2 )
```
For "Switch Parameters":
```
// toggled values must receive 0 or 1
k.switch( \cabinet, \onOff, 0 ) // args: addrPg, addrNr, val
```
To switch effects (instead of using addrNr: 0):
```
k.fxType( \effectModD, \phaser, true ) // args: addrPg, addrNr, switchOn (boolean)
```
Please check the documentation and/or class file for valid argument keys (they're case-sensitive)

## Events

This class also adds three `Event` types: `\kemperSet`, `\kemperSwitch`, and `\kemperFxType` which receive keys matching their respective method arguments:
```
(
Ppar([
    Pbind(
        \type,\kemperSet,
        \dur,0.01,
        \kemperMIDI,k, // a KemperMIDI instance must be included!
        \addrPg,\rig,
        \addrNr,\gain,
        \val,Pseg([0,1],[30]),
    ).play;
    
    Pbind(
        \type,\kemperSwitch,
        \dur,0.01,
        \kemperMIDI,k, // a KemperMIDI instance must be included!
        \addrPg,\amp,
        \addrNr,\onOff,
        \val,Pseq([0,1],10),
    ).play;
    
    Pbind(
        \type,\kemperFxType,
        \dur,0.5,
        \kemperMIDI,k, // a KemperMIDI instance must be included!
        \addrPg, \effectModA,
        \fxKey, Pseq([ \wahWah, \quadDelay ],inf),
        \switchOn, true,
    ).play
])
)
```

### TO-DO
- [ ] test all parameters
- [ ] scale/clip those parameters that have weird ranges (see transpose, for example)
- [ ] decipher comments copied directly from the docs, translate into something meaningful
- [ ] GUI? maybe at least some sort of visual aid for choosing parameters
- [ ] some way to filter relevant addrNr keys for a given addrPg key?
- [ ] must test hardware + MIDI latency (especially for \kemperFxType)
- [ ] arg names for instance methods: is addrPg, addrNr ideal? It's supposed to match the docs, but maybe I make my own docs that are more user-friendly?
- [x] third argument to .fxType(\addrPg, \fxType, startActive: true) ?

Feel free to open an issue/PR if I'm missing something obvious!
