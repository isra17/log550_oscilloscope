# log550 Oscilloscope utility

Oscilloscope given for the log550 course with some fixes added

Source: https://cours.etsmtl.ca/log550/private/labo/OscilloTestJAVA.zip

Update include:
 * Fixed Sampler conversion of Unsigned Byte to Int
 * Updated RXTX and JOGL version
 * Included Linux x86_64 shared libraries

## How to run

Grab a release at https://github.com/isra17/log550_oscilloscope/releases.

### Window

Just run the Oscilloscope.jar file or make sure 'java' is in your path and run OscillotestJAVA.bat .

### Linux

The distributed binaries are for x86_64 architecture. If needed, replace them with x86 binaries (Found on [rxtx download page](http://rxtx.qbang.org/wiki/index.php/Download)). Most distribution have them in their package manager. The version used is 2.2pre2.

The user running Oscilloscope.jar must be member of the group 'lock' and might need to be in the 'uucp' group as well.
