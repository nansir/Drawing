package com.sir.library.drawing.gestures.creator;

import com.sir.library.drawing.draw.SerializablePath;

public interface GestureCreatorListener {
    void onGestureCreated(SerializablePath serializablePath);

    void onCurrentGestureChanged(SerializablePath currentDrawingPath);
}
