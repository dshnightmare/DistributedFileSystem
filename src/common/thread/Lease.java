package common.thread;

public interface Lease
{
    public void renew();

    public boolean isValid();

    public void decrease();
}
