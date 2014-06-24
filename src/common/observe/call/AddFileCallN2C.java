package common.observe.call;

import java.util.List;

import nameserver.meta.StorageStatus;

public class AddFileCallN2C
    extends Call
{
    private static final long serialVersionUID = 32014432346467370L;

    private final List<StorageStatus> storages;

    public AddFileCallN2C(List<StorageStatus> storages)
    {
        super(Call.Type.ADD_FILE_N2C);
        this.storages = storages;
    }

    public List<StorageStatus> getStorages()
    {
        return storages;
    }
}
