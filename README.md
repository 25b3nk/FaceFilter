# Face filter android app

## Current status
- Able to draw face bounding box based using jetpack compose
- Now we draw the face box only if the user smiles

## To-DO
- [x] Draw bounding box using compose
- [x] Use face smiling probability
- [ ] Add sunglasses if the user smiles

## Challenges faced
1. The width and height of the image are interchanged, this will be determined by the image rotation degrees. This was tricky to find out.

## References
1. https://developers.google.com/ml-kit/vision/face-detection/android#kotlin
1. Used `claude` a lot for coding help
1. https://github.com/googlesamples/mlkit/tree/master/android/vision-quickstart/app/src/main/java/com/google/mlkit/vision/demo/kotlin/facedetector
1. https://github.com/google-ai-edge/mediapipe-samples/tree/main