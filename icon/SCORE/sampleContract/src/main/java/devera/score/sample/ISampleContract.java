package devera.score.example;

import score.Address;
import score.Context;
import score.DictDB;
import score.ArrayDB;
import score.BranchDB;
import score.annotation.EventLog;
import score.annotation.External;
import score.annotation.Optional;

import java.math.BigInteger;

public interface ISampleContract {
    String readMethod1();

    Address readMethod2();

    void externalMethod1(Address _param1, Address _param2, @Optional byte[] _param3);

    void externalMethod2(Address _param1, Address _param2, long _param3);

    void Event1(Address _param1, Address _param2, BigInteger _param3, byte[] _param4);
}
