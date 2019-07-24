package com.njuss.collection;
import java.util.ArrayList;
import java.util.List;

public class storeDao {


    public List<store> list() {
        List<store> list = new ArrayList<store>();
        for (int i = 1; i <= 1000; i++) {
            store b = new store();
            b.setLicenseID(i);
            b.setStorename(i);
            b.setAddress(i);


            b.setLocate(i);


            list.add(b);
        }

        return list;
    }
}
