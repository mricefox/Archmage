package com.mricefox.archmage.runtime;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import static com.mricefox.archmage.runtime.Utils.checkNull;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/5
 */

class Snapshot<T> {
    private final Logger logger = Logger.getLogger(Snapshot.class);
    private final Collection<T> elements;

    Snapshot(Collection<T> elements) {
        this.elements = Collections.unmodifiableCollection(new LinkedList<>(elements));
    }

    int applyToAll(ApplyCallback<T> callback) {
        checkNull(callback, "ApplyCallback");

        Iterator<T> iterator = elements.iterator();

        while (iterator.hasNext()) {
            T element = iterator.next();
            callback.apply(element);
        }
        return elements.size();
    }

    /**
     * Apply to elements which is instanceof S, return elements number
     */
    @SuppressWarnings("unchecked")
    <S extends T> int applyToType(Class<S> type, ApplyCallback<S> callback) {
        checkNull(callback, "ApplyCallback");
        checkNull(type, "Type");

        Iterator<T> iterator = elements.iterator();
        int n = 0;

        while (iterator.hasNext()) {
            T element = iterator.next();

            //element instanceof S
            if (/*element.getClass() == type*/type.isInstance(element)) {
                n++;
                callback.apply((S) element);
            }
        }
        return n;
    }
}
