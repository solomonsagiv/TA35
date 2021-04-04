package lists;

import service.MyBaseService;
import service.ServiceEnum;

// Regular list updater
public class ListsService extends MyBaseService {

    public ListsService() {
		super();
	}
    
    @Override
    public void go() {
        insert( );
    }
    
    @Override
    public String getName() {
        return "lists";
    }

    @Override
    public int getSleep() {
        return 1000;
    }

    @Override
    public ServiceEnum getType() {
        return ServiceEnum.REGULAR_LISTS;
    }

    private void insert() {
    	try {
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
}