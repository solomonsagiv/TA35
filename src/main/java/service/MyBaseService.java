package service;

import api.BASE_CLIENT_OBJECT;

public abstract class MyBaseService implements IMyService {

    protected int sleepCount = 0;
    
    ServiceEnum type;
    
    BASE_CLIENT_OBJECT client;

    public MyBaseService(BASE_CLIENT_OBJECT client) {
        this.client = client;
        client.getServiceHandler().addService( this );
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