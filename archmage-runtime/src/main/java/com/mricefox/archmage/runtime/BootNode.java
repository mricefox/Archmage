package com.mricefox.archmage.runtime;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:Base class of boot node
 * <p>Date:2017/12/25
 */

/*package*/ abstract class BootNode implements Comparable<BootNode> {
    private final Logger logger = Logger.getLogger(BootNode.class);

    private static int sCursor;
    private final int id;
    private Object tag;

    public BootNode() {
        this.id = sCursor++;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return this.tag;
    }

    protected Class alias() {
        return this.getClass();
    }

    protected String name() {
        return null;
    }

    //----------- override object's api -----------
    @Override
    public final int compareTo(BootNode o) {
        if (this.id == o.id) {
            return 0;
        }
        if (this.id > o.id) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "BootNode{" +
                "id=" + id +
                ", name='" + name() + '\'' +
                ", tag=" + tag +
                '}';
    }
}
