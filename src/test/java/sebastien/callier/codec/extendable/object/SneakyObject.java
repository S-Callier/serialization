package sebastien.callier.codec.extendable.object;

/**
 * @author callier
 * @since 2017/02/16
 */
public class SneakyObject {
    private SneakyObject sneakyObject;

    public SneakyObject getSneakyObject() {
        return sneakyObject;
    }

    public void setSneakyObject(SneakyObject sneakyObject) {
        this.sneakyObject = sneakyObject;
    }

    @Override
    public boolean equals(Object o) {
        return true;
    }
}
