package com.mricefox.archmage.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:DO NOT MODIFY, BYTECODE CHANGED WHEN COMPILED INTO DEX
 * <p>Date:2017/12/15
 */

/*package*/ final class ActivatorRecord {
    private final static List<String> sActivatorClasses = new ArrayList<>();

    private ActivatorRecord() {
    }

    public static final List<String> getActivatorClasses() {
        return sActivatorClasses;
    }

}

//After bytecode modified, this class looks like below
/*
package com.mricefox.archmage.runtime;

import java.util.ArrayList;
import java.util.List;

final class ActivatorRecord {
    private static final List<String> sActivatorClasses = new ArrayList();

    private ActivatorRecord() {
    }

    public static final List<String> getActivatorClasses() {
        if(sActivatorClasses.isEmpty()) {
            sActivatorClasses.add("com.mricefox.sample.hotel.Activator");
            sActivatorClasses.add("com.mricefox.sample.main.Activator");
        }

        return sActivatorClasses;
    }
}
*/
