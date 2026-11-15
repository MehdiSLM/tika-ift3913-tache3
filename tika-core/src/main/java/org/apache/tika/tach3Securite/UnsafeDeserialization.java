package org.apache.tika.tach3Securite;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class UnsafeDeserialization {

    public Object readObject(InputStream input)
            throws IOException, ClassNotFoundException {

        ObjectInputStream stream = new ObjectInputStream(input);
        return stream.readObject();
    }
}