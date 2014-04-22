Java Emotion Recognition
---

JEmotionRec is an application that takes an image of a person's face as input and outputs the emotion that they are expressing given a set of training data.

This project was originally developed as a project at the University of Waterloo and has since been migrated here and transformed into a more general purpose tool.

Author: Matt Pillar <mpillar@uwaterloo.ca>

Usage
---

    $ ./run.sh 
    Emotion Recognition Application

    Usage: <mode> <args...>

    Mode 1: fdet
        Feature detection mode. When running in this mode, the args are:
            arg0 = operation
            arg1 = imageFile (PNG or JPEG format)
            arg2 = emotionType (int)
        The output format is: <fileHash>,<emotionType>,x1,x2,...,xN

        The op can be one of full|mouth|edge, where full does face
        detection, mouth applies rescaling and normalization filters,
        and edge does only edge detection.

    Mode 2: recognize
        Recognition mode. When running in this mode, the args are:
            arg0 = type (backprop or kohonen)
            arg1 = num_network_nodes (int)
            arg2 = num_features (int)
            arg3 = training file
            arg4 = testing file
        The output format is: <fileHash>,<emotionType>,p1,p2,...,pN

Example
---

The following example runs the backpropagation algorithm on the given sample DRO's.

    ./run.sh recognize backprop 14 4 ./sample/training.dat ./sample/training.dat 
