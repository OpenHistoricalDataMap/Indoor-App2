package htw_berlin.de.mapmanager.persistence;

/**
 * Created by tognitos on 16.01.17.
 * This exception should be thrown in case of an attempt to read from disk even if a read permission
 * was not given
 */

public class ReadPermissionException extends Exception {
    public ReadPermissionException(String message){
        super(message);
    }


}
