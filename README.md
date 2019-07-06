# XIS
KS Image Suite combines my previous graphical projects into a single project.

## Main features
### **image-copy-finder**:
  finds similar images in given folders or their combinations and allows user to "delete" them (move them into delete location).
  
### **scanprocessing**:

allows to apply one of the available filters to either a single image or entire PDF.

Available filters:

  - high pass filter
 
 perfect for converting image full of gradients and shadows into one that is easy to print on a mediocre black and white printer
 
  - quantization
 
 selects mains colors from the image and strengthens them, perfect to further enhance scanned notes.

### **compression**:
 
 rather a proof of concept than a fully working algorithm. Generates files that are similar to size to .jpg, albeit of lower quality.
 
-
 
For more information see docs at: https://kamil-sita.github.io/XIS/

----

## Building and running
Building and running XIS requires JRE/JDK at minimum 11 version. I recommend using [AdoptOpenJDK](https://github.com/AdoptOpenJDK/openjdk11-binaries/releases) one, as it's the one I use for my development and running purposes.
