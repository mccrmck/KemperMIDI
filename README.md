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
k.set( 4, 0x03, 0.2 ) // args: addrPg, addrNr, val (0-1)

// ...or symbols for the addPg and addNr arguments
k.set( \rig, \panorama, 0.2 )

```
For "Switch Parameters":
```
// toggled values must receive 0 or 1
k.switch( \cabinet, \onOff, 0 ) // args: addrPg, addrNr, val (0 or 1)
```
To switch effects (instead of using addrNr: 0):
```
k.fxType( \effectModD, \phaser )
```
Please check the documentation/class file for valid argument keys (they're case-sensitive)
***

This class also adds two `Event` types: `\kemperSet` and `\kemperSwitch` which receive keys that match their respective method arguments:
```
(
Pbind(
    \type,\kemperSet,
    \dur,0.01,
    \kemperMIDI,k, // a KemperMIDI instance must be included!
    \addrPg,\rig,
    \addrNr,\gain,
    \val,Pseg([0,1],[30]),
).play
)
```

## TO-DO
- [ ] test all parameters
- [ ] scale/clip those parameters that have weird ranges (see transpose, for example)
- [ ] decipher comments copied directly from the docs, translate into something meaningful
- [ ] GUI? maybe at least some sort of visual aid for choosing parameters
- [ ] some way to filter relevant addrNr keys for a given addrPg key?
- [ ] \fxType `Event` key could be cool - must test hardware + MIDI latency
- [ ] arg names for instance methods: is addrPg, addrNr ideal? It's supposed to match the docs, but maybe I make my own docs that are more user-friendly?
- [ ] third argument to .fxType(\addrPg, \fxType, startActive: true) ?

Feel free to open an issue/PR if I'm missing something obvious!
