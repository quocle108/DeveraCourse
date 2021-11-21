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

public class SampleContract implements ISampleContract {
    private final String name = "sample contract";
    protected static final Address address = new Address(new byte[Address.LENGTH]);
    private final DictDB<Address, BigInteger> balances = Context.newDictDB("balances", BigInteger.class);
    private final VarDB<BigInteger> vardbExample = Context.newVarDB("varDbExample", BigInteger.class);
    private final ArrayDB<Long> arrayDbExample = Context.newArrayDB("arrayDbExample", Long.class);
    private final BranchDB<String, DictDB<Address, BigInteger>> branchDBExample = Context.newBranchDB("branchDBExample", BigInteger.class);

    public SampleContract(String _pram1, String _param2, int _param3) {
        // implement
    }

    @External(readonly=true)
    public String readMethod1() {
        return name;
    }

    @External(readonly=true)
    public Address readMethod2() {
        return address;
    }

    @External
    public void externalMethod1(Address _param1, Address _param2, @Optional byte[] _param3) {
        // impelemt external method
        Event1(_param1, _param2, BigInteger.valueOf(10000), _param3);
    }

    protected void _internalMethod1(Address _param1, BigInteger _param2) {
        // implement internal method
    }

    private void _internalMethod2(Address _param1, BigInteger _param2) {
        // implement internal method
    }

    @EventLog(indexed=3)
    public void Event1(Address _param1, Address _param2, BigInteger _param3, byte[] _param4) {}
}
