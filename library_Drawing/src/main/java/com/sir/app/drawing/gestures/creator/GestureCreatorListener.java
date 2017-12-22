package com.sir.app.drawing.gestures.creator;

import com.sir.app.drawing.draw.SerializablePath;

public interface GestureCreatorListener {
  void onGestureCreated(SerializablePath serializablePath);

  void onCurrentGestureChanged(SerializablePath currentDrawingPath);
}
