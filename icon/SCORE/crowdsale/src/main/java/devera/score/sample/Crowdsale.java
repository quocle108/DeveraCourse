package devera.score.example;

import score.Address;
import score.Context;
import score.DictDB;
import score.VarDB;
import score.annotation.EventLog;
import score.annotation.External;
import score.annotation.Payable;

import java.math.BigInteger;
import java.util.Map;

import scorex.util.ArrayList;

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
    private final VarDB<Withdrawal> withdrawal = Context.newVarDB("withdrawal", Withdrawal.class);
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

    @External(readonly=true)
    public Map withdrawal() {
        if (this.withdrawal.get() == null) {
            Context.revert("withdrawal not found");
        }
        return this.withdrawal.get().toMap();
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

    @External
    public void withdraw(BigInteger _value, String _description) {
        Address _from = Context.getCaller();
        if (!this.beneficiary.equals(_from)) {
            Context.revert("unauthorized");
        }

        if (!afterDeadline() && !goalReached()) {
            Context.revert("can not withdraw during crowdsale");
        }

        if (this.amountRaised.get().compareTo(_value) < 0) {
            Context.revert("overdraw balance");
        }

        if (this.withdrawal.get() != null) {
            Context.revert("already created withdrawal");
        }
        Withdrawal newWithdrawal = new Withdrawal(_value, BigInteger.ZERO, _description, new ArrayList<Address>());
        this.withdrawal.set(newWithdrawal);
    }

    @External
    public void withdrawHack() {
        Address _from = Context.getCaller();
        BigInteger depositedBalance = this.safeGetBalance(_from);
        if (depositedBalance.equals(BigInteger.ZERO)) {
            Context.revert("no available balance");
        }

        // transfer the funds to beneficiary
        Context.transfer(_from, depositedBalance);

        // set balance to ZERO
        this.balances.set(_from, BigInteger.ZERO);

        // emit eventlog
        FundWithdraw(this.beneficiary, depositedBalance);
    }

    @External
    public void cancelWithdrawal() {
        Address _from = Context.getCaller();
        if (!this.beneficiary.equals(_from)) {
            Context.revert("unauthorized");
        }

        if (this.withdrawal.get() == null) {
            Context.revert("withdrawal not found");
        }
        this.withdrawal.set(null);
    }

    @External
    public void voteWithdrawal() {
        Address _from = Context.getCaller();
        BigInteger depositedBalance = this.safeGetBalance(_from);
        if (depositedBalance.equals(BigInteger.ZERO)) {
            Context.revert("only depositor can vote for withdrawal");
        }
        Withdrawal currentWithdrawal = this.withdrawal.get();
        if (currentWithdrawal == null) {
            Context.revert("withdrawal not found");
        }

        currentWithdrawal.vote(_from, depositedBalance);
        this.withdrawal.set(currentWithdrawal);
    }

    @External
    public void executeWithdrawal() {
        Address _from = Context.getCaller();
        if (!this.beneficiary.equals(_from)) {
            Context.revert("unauthorized");
        }

        Withdrawal currentWithdrawal = this.withdrawal.get();
        if (currentWithdrawal == null) {
            Context.revert("withdrawal not found");
        }
        
        if (currentWithdrawal.getApprovedWeight().compareTo(this.fundingGoal) < 0) {
            Context.revert("not enough vote weight");
        }

        // transfer the funds to beneficiary
        Context.transfer(this.beneficiary, currentWithdrawal.getAmount());
        // emit eventlog
        FundWithdraw(this.beneficiary, currentWithdrawal.getAmount());
        // reset amountRaised
        this.amountRaised.set(this.amountRaised.get().subtract(currentWithdrawal.getAmount()));
        this.withdrawal.set(null);
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
