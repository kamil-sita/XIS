package XIS.sections.imagecopyfinder;

import java.io.*;
import java.util.List;

public final class SerializableIcfState implements Serializable {

    public static final long serialVersionUID = 1L;
    private List<ComparedImagePair> imagePairs;
    private String inputCommand;
    private int inputImageSize;
    private String inputCommandDeleteLocation;

    public SerializableIcfState(List<ComparedImagePair> imagePairs, String inputCommand, int inputImageSize, String inputCommandDeleteLocation) {
        this.imagePairs = imagePairs;
        this.inputCommand = inputCommand;
        this.inputImageSize = inputImageSize;
        this.inputCommandDeleteLocation = inputCommandDeleteLocation;
    }

    public List<ComparedImagePair> getImagePairs() {
        return imagePairs;
    }

    public String getInputCommand() {
        return inputCommand;
    }

    public int getInputImageSize() {
        return inputImageSize;
    }

    public String getInputCommandDeleteLocation() {
        return inputCommandDeleteLocation;
    }

    public static boolean serialize(SerializableIcfState serializableIcfState) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("icf.serialized"))) {
            outputStream.writeObject(serializableIcfState);
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static SerializableIcfState deserialize() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("icf.serialized"))) {
            return (SerializableIcfState) inputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
