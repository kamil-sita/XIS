# XIS
KS Image Suite will combine my previous graphical projects into a single project. Including deleting similar images, aggressive image compression and scanner helper.

## Features
* image-copy-finder - looks for similar images in various combinations of folders (including recursive folder opening and others!), shows them to user and lets user delete (that is move to some other user defined folder) selected ones
* scanner-to-note - color quantization of images and scaling their brightness in order to create better looking notes
* high-pass-filter - removing gradients and background from photos and scans
* automated filtering - same as high-pass-filter but for .pdf files
* compression - custom image compression that uses a combination of chroma subsampling, dictionary and RLE compression

----

## Building and running
Building and running XIS requires JRE/JDK at minimum 11 version. I recommend using [AdoptOpenJDK](https://github.com/AdoptOpenJDK/openjdk11-binaries/releases) one, as it's the one I use for my development and running purposes.
