package devera.score.example;

import score.Address;
import score.Context;
import score.DictDB;
import score.VarDB;
import score.ArrayDB;
import score.BranchDB;
import score.annotation.EventLog;
import score.annotation.External;
import score.annotation.Optional;

import java.math.BigInteger;

public class SampleContract {
    private final DictDB<Address, BigInteger> balances = Context.newDictDB("balances", BigInteger.class);
    public SampleContract(String _param1, String _param2, int _param3) {
        // implement
    }

    @External(readonly=true)
    public BigInteger balanceOf(Address _owner) {
        return this.balances.getOrDefault(_owner, BigInteger.ZERO);
    }


    @External
    public void tokenFallback(Address _from, BigInteger _value, byte[] dataBytes) {
        // impelemt external method
        BigInteger currentBalance = this.balances.getOrDefault(_from, BigInteger.ZERO);
        this.balances.set(_from, currentBalance.add(_value));
    }
}
