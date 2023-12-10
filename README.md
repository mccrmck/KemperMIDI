# KemperMIDI

A SuperCollider class for sending 14-bit MIDI to ,

Details about how the Kemper receives MIDI, relevant integers for the Address Pages / Address Numbers, and for a list of the available parameters can be found in the attached MIDI Parameter Documentation 8.6 (downloaded [here](https://www.kemper-amps.com/downloads/5/User-Manuals))

Basic usage:

```
// make an instance
k = KemperMIDI( "UM-ONE", "UM-ONE", 2 ) // MIDI device, MIDI port, channel

// set a parameter using integers, symbols, or hex notation
k.set( 4, 3, 0.2 )

// ...which is equivalent to:
k.set( \rig, \panorama, 0.2 )

// toggled values must receive 0 or 1
k.switch( \cabinet, \onOff, 0 )

// effects can be added using the codes found in the above documentation:
// note: this interface will change evenutally so symbols can be used instead of integers for MSB and LSB
k.fxType( \effectModD, 0, 81  ) // will add a phaser to effects module D
```

Note: this has only been tested on a Kemper Stage Profiler and with macOS!
