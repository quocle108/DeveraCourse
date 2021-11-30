package devera.score.example;

import score.Address;
import score.Context;
import score.DictDB;
import score.VarDB;
import score.annotation.EventLog;
import score.annotation.External;
import score.annotation.Payable;

import java.math.BigInteger;

public class Crowdsale implements ICrowdsale
{
    private static final BigInteger ONE_ICX = new BigInteger("1000000000000000000");
    private final Address beneficiary;
    private final Address tokenScore;
    private final BigInteger fundingGoal;
    private final BigInteger tokenPrice;
    private final long deadline;
    private boolean activeCrowdsale;
    private final DictDB<Address, BigInteger> balances;
    private final VarDB<BigInteger> amountRaised;
    private BigInteger testAmount;

    public Crowdsale(BigInteger _fundingGoalInIcx, Address _tokenScore, BigInteger _durationInBlocks, BigInteger _tokenPrice) {
        // some basic requirements
        Context.require(_fundingGoalInIcx.compareTo(BigInteger.ZERO) >= 0);
        Context.require(_durationInBlocks.compareTo(BigInteger.ZERO) >= 0);

        this.beneficiary = Context.getCaller();
        this.fundingGoal = ONE_ICX.multiply(_fundingGoalInIcx);
        this.tokenScore = _tokenScore;
        this.deadline = Context.getBlockHeight() + _durationInBlocks.longValue();

        this.activeCrowdsale = false;

        this.balances = Context.newDictDB("balances", BigInteger.class);
        this.amountRaised = Context.newVarDB("amountRaised", BigInteger.class);
        this.tokenPrice = _tokenPrice;
        this.testAmount = _fundingGoalInIcx;
    }

    @External(readonly=true)
    public String name() {
        return "Sample Crowdsale";
    }

    @External(readonly=true)
    public String description() {
        return "Devera ICON dapp development course";
    }

    @External(readonly=true)
    public BigInteger balanceOf(Address _owner) {
        return this.safeGetBalance(_owner);
    }

    @External(readonly=true)
    public BigInteger amountRaised() {
        return this.amountRaised.get();
    }

    /*
     * Receives initial tokens to reward to the contributors.
     */
    @External
    public void tokenFallback(Address _from, BigInteger _value, byte[] _data) {
        // check if the caller is a token SCORE address that this SCORE is interested in
        Context.require(Context.getCaller().equals(this.tokenScore));

        // depositing tokens can only be done by owner
        Context.require(Context.getOwner().equals(_from));

        // value should be greater than zero
        Context.require(_value.compareTo(BigInteger.ZERO) >= 0);

        // start Crowdsale hereafter
        this.activeCrowdsale = true;
        // emit eventlog
        CrowdsaleStarted(this.fundingGoal, this.deadline);
    }

    /*
     * Called when anyone sends funds to the SCORE and that funds would be regarded as a contribution.
     */
    @Payable
    public void fallback() {
        // check if the crowdsale is closed
        Context.require(this.activeCrowdsale);

        Address _from = Context.getCaller();
        BigInteger _value = Context.getValue();
        Context.require(_value.compareTo(BigInteger.ZERO) > 0);

        // accept the contribution
        BigInteger fromBalance = safeGetBalance(_from);
        this.balances.set(_from, fromBalance.add(_value));

        // increase the total amount of funding
        BigInteger amountRaised = safeGetAmountRaised();
        this.amountRaised.set(amountRaised.add(_value));

        // give tokens to the contributor as a reward
        byte[] _data = "called from Crowdsale".getBytes();
        Context.call(this.tokenScore, "transfer", _from, _value.multiply(this.tokenPrice), _data);
        // emit eventlog
        FundDeposit(_from, _value);
    }

    /*
     * Withdraws the funds safely.
     *
     *  - If the funding goal has been reached, sends the entire amount to the beneficiary.
     *  - If the goal was not reached, each contributor can withdraw the amount they contributed.
     */
    @External
    public void withdraw(BigInteger _value) {
        Context.require(this.activeCrowdsale);
        Address _from = Context.getCaller();
        if (!this.beneficiary.equals(_from)) {
            Context.revert("unauthorized");
        }

        if (!afterDeadline() && !goalReached()) {
            Context.revert("can not withdraw during crowdsale");
        }

        BigInteger amountRaised = safeGetAmountRaised();

        if (amountRaised.compareTo(_value) < 0) {
            Context.revert("overdraw balance");
        }

        // transfer the funds to beneficiary
        Context.transfer(this.beneficiary, _value);
        // emit eventlog
        FundWithdraw(this.beneficiary, _value);
        // reset amountRaised
        this.amountRaised.set(amountRaised.subtract(_value));
    }

    private BigInteger safeGetBalance(Address owner) {
        return this.balances.getOrDefault(owner, BigInteger.ZERO);
    }

    private BigInteger safeGetAmountRaised() {
        return this.amountRaised.getOrDefault(BigInteger.ZERO);
    }

    private boolean afterDeadline() {
        // checks if it has been reached to the deadline block
        return Context.getBlockHeight() >= this.deadline;
    }

    private boolean goalReached() {
        if (this.amountRaised.get().compareTo(this.fundingGoal) >= 0) {
            return true;
        }

        return false;
    }

    @EventLog
    public void CrowdsaleStarted(BigInteger fundingGoal, long deadline) {}

    @EventLog(indexed=2)
    public void FundDeposit(Address backer, BigInteger amount) {}

    @EventLog(indexed=2)
    protected void FundWithdraw(Address owner, BigInteger amount) {}
}
