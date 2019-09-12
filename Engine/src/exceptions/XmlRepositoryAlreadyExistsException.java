package exceptions;

public class XmlRepositoryAlreadyExistsException extends Exception {
    @Override
    public String getMessage() {
        return "Xml repository already exists";
    }
}
