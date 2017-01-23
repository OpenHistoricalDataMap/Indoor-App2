package htw_berlin.de.mapmanager.persistence;

/**
 * Created by tognitos on 16.01.17.
 * This exception should be thrown in case of an attempt to write on disk even if a write permission
 * was not given
 */

public class WritePermissionException extends Exception {
    public WritePermissionException(String message){
        super(message);
    }


}
