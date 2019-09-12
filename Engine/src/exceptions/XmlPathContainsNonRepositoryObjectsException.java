package exceptions;

public class XmlPathContainsNonRepositoryObjectsException extends Exception {
    @Override
    public String getMessage() {
        return "The Path in the xml file contains files which are not repository";
    }
}
