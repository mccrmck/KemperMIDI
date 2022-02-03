
MIDIClient.init;
MIDIClient.destinations;

~midiOut = MIDIOut(0)


var path = "/Users/mikemccormick/Library/Application Support/SuperCollider/Extensions/YAWN/songs/numberFour/kemperMIDI/beginning.mid";
x = SimpleMIDIFile.read(path)
.timeMode_(\seconds)
.midiEvents;

x

x[..40].do(_.postln)


a = x.collect({ |event| event[1] }).differentiate.drop(1);
b  = x.collect({ |event| event = event.replace(\cc,'control'); event[2] });
c = x.collect({ |event| event[3] });
d  = x.collect({ |event| event[4] });
e  = x.collect({ |event| event[5] });



////

Server.default.options.device = "Saffire"

MIDIClient.restart
MIDIClient.init

KemperMIDI.midiOut


KemperMIDI("Pro40")

0.1.asBoolean
a
a = MIDIOut.newByName("Pro40","MIDI")
MIDIOut(a)

(
var pathToKemperMIDI = "/Users/mikemccormick/Library/Application Support/SuperCollider/Extensions/YAWN/Songs/numberFour/kemperMIDI/";
KemperMIDI.makePat('test1',pathToKemperMIDI ++ "count.mid")
)


Server.default.options.device = "Saffire"


~yawn = YAWNShow([\cement,\numberOne,\numberTwo,\numberFour],"Pro40",0);  //change clickOut to 2


~yawn.songArray



KemperMIDI.cues['fourBeginning'].pattern.asStream.next(())

Ptrace(g).play


a = YAWNSong('numberFour').cueFrom('fourBeginning','fourBeginning',true,false, true)
a
a.play
a.stop




Pdef('fourBeginning').stop


