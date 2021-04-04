package service;

import api.ApiObject;

public abstract class MyBaseService implements IMyService {

    protected int sleepCount = 0;
    
    ServiceEnum type;
    
    protected ApiObject apiObject = ApiObject.getInstance();

    public MyBaseService() {
        apiObject.getServiceHandler().addService( this );
    }

    public void execute( int sleepCount ) {
        if ( sleepCount % getSleep( ) == 0 ) {
            this.sleepCount = sleepCount;
            try {
                go( );
            } catch ( Exception e ) {
                e.printStackTrace( );
            }
        }
    }

    // --------- Getters and setters --------- //
    public ServiceEnum getType() {
        return type;
    }

}