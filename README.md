# Java Emotion Recognition

JEmotionRec is an application that takes an image of a person's face as input and outputs the emotion that they are expressing given a set of training data.

This project was originally developed as a project at the University of Waterloo and has since been migrated here and transformed into a more general purpose tool.

## Usage

    $ ./run.sh 
    Emotion Recognition Application

    Usage: <mode> <args...>

    Mode 1: fdet
        Feature detection mode. When running in this mode, the args are:
            arg0 = operation
            arg1 = image_file (PNG or JPEG format)
            arg2 = emotion_type (int)
        The output format is: <file_hash>,<emotion_type>,x1,x2,...,xN

        The operation can be one of full|mouth|edge, where full does face
        detection, mouth applies rescaling and normalization filters,
        and edge does only edge detection.

    Mode 2: recognize
        Recognition mode. When running in this mode, the args are:
            arg0 = type (backprop or kohonen)
            arg1 = num_network_nodes (int)
            arg2 = num_features (int)
            arg3 = the name of the training file
            arg4 = the name of the testing file
        The output format is: <file_hash>,<emotion_type>,p1,p2,...,pN

## Example

The following example runs the backpropagation algorithm on the given sample DRO's.

    ./run.sh recognize backprop 14 4 ./sample/training.dat ./sample/training.dat 

## License

This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <http://unlicense.org>
