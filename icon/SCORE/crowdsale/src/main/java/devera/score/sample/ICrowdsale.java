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

public interface ICrowdsale {
    String name();

    String description();

    void tokenFallback(Address _from, BigInteger _value, byte[] _data);

    void fallback();

    void CrowdsaleStarted(BigInteger fundingGoal, long deadline);

    void FundTransfer(Address backer, BigInteger amount);

    void GoalReached(Address recipient, BigInteger totalAmountRaised);
}
